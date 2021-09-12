package mk.ukim.finki.manurepoapi.controller;

import mk.ukim.finki.manurepoapi.dto.request.AdminRecordsFilter;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.File;
import mk.ukim.finki.manurepoapi.model.FileData;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.security.MockUserDetailsService;
import mk.ukim.finki.manurepoapi.service.AdminService;
import mk.ukim.finki.manurepoapi.service.FileService;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(MockUserDetailsService.class)
class AdminControllerTest {

    @MockBean
    AdminService adminService;

    @MockBean
    FileService fileService;

    @Autowired
    MockMvc mockMvc;

    private final Long recordId = 10L;

    private String validAdminJwt;

    @BeforeEach
    void setUp() {
        Long accountId = 1L;
        validAdminJwt = String.format("Bearer %s", TestUtils.createValidAdminJwt(accountId));
    }

    @ParameterizedTest
    @MethodSource("getNewRecordsPagePaginationParams")
    void getNewRecordsPage_filtersSpecifiedAsQueryParams_returnRecordsPage(String page, String size, String sort, Pageable expectedPageable) throws Exception {
        // given
        AdminRecordsFilter expectedAdminRecordsFilter = AdminRecordsFilter.builder()
                .title("title")
                .author("author")
                .collection(Collection.AUDIO)
                .build();
        Page<Record> recordsPage = new PageImpl<>(List.of(TestUtils.createRecord()));

        when(adminService.getNewRecordsPage(expectedAdminRecordsFilter, expectedPageable)).thenReturn(recordsPage);

        // when, then
        MockHttpServletRequestBuilder requestBuilder = get("/admin/records")
                .queryParam("title", "title")
                .queryParam("author", "author")
                .queryParam("collection", "AUDIO")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .header(HttpHeaders.AUTHORIZATION, validAdminJwt);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].collection", is("CONFERENCE_ITEM")))
                .andExpect(jsonPath("$.content[0].collectionName", is("Conference Contribution")))
                .andExpect(jsonPath("$.content[0].department", is("Department of Social Sciences")))
                .andExpect(jsonPath("$.content[0].dateArchived", is("2021-01-01T10:10:00")));

        verify(adminService).getNewRecordsPage(expectedAdminRecordsFilter, expectedPageable);
    }

    static List<Arguments> getNewRecordsPagePaginationParams() {
        return List.of(
                Arguments.of(null, null, null, PageRequest.of(0, 10, Sort.Direction.DESC, "dateArchived")),
                Arguments.of("3", "7", "downloadsCount,desc", PageRequest.of(3, 7, Sort.Direction.DESC, "downloadsCount")),
                Arguments.of("2", "5", "downloadsCount,asc", PageRequest.of(2, 5, Sort.Direction.ASC, "downloadsCount"))
        );
    }

    @Nested
    class GetRecordDetails {

        @Test
        void getRecordDetails_givenRecordId_returnsRecordWithFilesAndAuthorsData() throws Exception {
            // given
            Record record = TestUtils.createRecordWithFilesAndAuthors(recordId);
            when(adminService.getRecordDetails(recordId)).thenReturn(record);

            // when
            mockMvc.perform(get("/admin/records/{recordId}", recordId)
                    .header(HttpHeaders.AUTHORIZATION, validAdminJwt))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.files", hasSize(1)))
                    .andExpect(jsonPath("$.files[0].fileDownloadUri", is("http://localhost/admin/files/11")))
                    .andExpect(jsonPath("$.authors", hasSize(1)))
                    .andExpect(jsonPath("$.authors[0].memberType", is("Corresponding Member")))
                    .andExpect(jsonPath("$.authors[0].imageUrl", is("http://localhost/members/profileImage/111")));
            verify(adminService).getRecordDetails(recordId);
        }

        @Test
        void getRecordDetails_recordNotFound_httpStatusIsNotFound() throws Exception {
            // given
            when(adminService.getRecordDetails(anyLong())).thenThrow(new EntityNotFoundException(Record.class, recordId));

            // when
            mockMvc.perform(get("/admin/records/{recordId}", recordId)
                    .header(HttpHeaders.AUTHORIZATION, validAdminJwt))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.httpStatus", is("NOT_FOUND")))
                    .andExpect(jsonPath("$.message", is(String.format("Record was not found for {id=%d}", recordId))));
        }
    }

    @Test
    void approveRecord_givenRecordId_recordIsApproved() throws Exception {
        // given
        Record record = TestUtils.createRecord();
        when(adminService.approveRecord(recordId)).thenReturn(record);

        // when, then
        mockMvc.perform(patch("/admin/records/{recordId}", recordId)
                .header(HttpHeaders.AUTHORIZATION, validAdminJwt))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.collectionName", is("Conference Contribution")))
                .andExpect(jsonPath("$.department", is("SS")))
                .andExpect(jsonPath("$.dateArchived", is("2021-01-01T10:10:00")))
                .andExpect(jsonPath("$.collection", is("CONFERENCE_ITEM")));
        verify(adminService).approveRecord(recordId);
    }

    @Nested
    class DeclineRecord {

        @Test
        void declineRecord_givenRecordId_recordIsDeletedStatusNoContent() throws Exception {
            // when, then
            mockMvc.perform(delete("/admin/records/{recordId}", recordId)
                    .header(HttpHeaders.AUTHORIZATION, validAdminJwt))
                    .andExpect(status().isNoContent());
            verify(adminService).declineRecord(recordId);
        }

        @Test
        void declineRecord_givenRecordId_recordNotFoundBadRequest() throws Exception {
            // given
            doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST)).when(adminService).declineRecord(anyLong());

            // when, then
            mockMvc.perform(delete("/admin/records/{recordId}", recordId)
                    .header(HttpHeaders.AUTHORIZATION, validAdminJwt))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void downloadFile_givenFileId_shouldDownloadFile() throws Exception {
        // given
        final Long fileId = 1L;
        byte[] data = "textFileContent".getBytes();
        long fileSize = data.length;
        File file = File.builder()
                .id(fileId)
                .fileName("testFile.txt")
                .size(fileSize)
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .fileData(new FileData(data))
                .build();

        when(fileService.getFile(anyLong())).thenReturn(file);

        // when, then
        mockMvc.perform(get("/admin/files/{fileId}", fileId)
                .header(HttpHeaders.AUTHORIZATION, validAdminJwt))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, fileSize))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"testFile.txt\""))
                .andExpect(content().bytes(data));
        verify(fileService).getFile(fileId);
    }
}
