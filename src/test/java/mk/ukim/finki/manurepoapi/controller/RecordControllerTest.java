package mk.ukim.finki.manurepoapi.controller;

import mk.ukim.finki.manurepoapi.dto.request.RecordsFilter;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.security.MockUserDetailsService;
import mk.ukim.finki.manurepoapi.service.RecordService;
import mk.ukim.finki.manurepoapi.service.StatisticsService;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RecordController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(MockUserDetailsService.class)
class RecordControllerTest {

    @MockBean
    RecordService recordService;

    @MockBean
    StatisticsService statisticsService;

    @MockBean
    ApplicationEventPublisher eventPublisher;

    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("getPaginationParams")
    void getRecordsPage_filtersSpecifiedAsQueryParams_returnRecordsPage(String page, String size, String sort, Pageable expectedPageable) throws Exception {
        // given
        RecordsFilter expectedRecordsFilter = RecordsFilter.builder()
                .titleOrKeyword("Demo")
                .collections(List.of(Collection.ARTICLE, Collection.BOOK))
                .department(Department.MBS)
                .build();
        Page<Record> recordsPage = new PageImpl<>(List.of(TestUtils.createRecord()));

        when(recordService.getRecordsPage(expectedRecordsFilter, expectedPageable)).thenReturn(recordsPage);

        // when, then
        MockHttpServletRequestBuilder requestBuilder = get("/records/browse")
                .queryParam("titleOrKeyword", "Demo")
                .queryParam("collections", "ARTICLE", "BOOK")
                .queryParam("department", "MBS")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].collection", is("CONFERENCE_ITEM")))
                .andExpect(jsonPath("$.content[0].collectionName", is("Conference Contribution")))
                .andExpect(jsonPath("$.content[0].department", is("Department of Social Sciences")))
                .andExpect(jsonPath("$.content[0].dateArchived", is("2021-01-01T10:10:00")));
        verify(recordService).getRecordsPage(expectedRecordsFilter, expectedPageable);
    }

    static List<Arguments> getPaginationParams() {
        return List.of(
                Arguments.of(null, null, null, PageRequest.of(0, 10, Sort.Direction.DESC, "dateArchived")),
                Arguments.of("3", "10", "downloadsCount,desc", PageRequest.of(3, 10, Sort.Direction.DESC, "downloadsCount")),
                Arguments.of("2", "5", "downloadsCount,asc", PageRequest.of(2, 5, Sort.Direction.ASC, "downloadsCount"))
        );
    }

    @Nested
    class GetRecordDetails {

        private final Long recordId = 1L;

        @Test
        void getRecordDetails_givenRecordId_returnsRecordWithFilesAndAuthorsData() throws Exception {
            // given
            Record record = TestUtils.createRecordWithFilesAndAuthors(recordId);
            when(recordService.getPublicRecord(recordId)).thenReturn(record);

            // when
            mockMvc.perform(get("/records/browse/{recordId}", recordId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.files", hasSize(1)))
                    .andExpect(jsonPath("$.files[0].fileDownloadUri", is("http://localhost/files/11")))
                    .andExpect(jsonPath("$.authors", hasSize(1)))
                    .andExpect(jsonPath("$.authors[0].memberType", is("Corresponding Member")))
                    .andExpect(jsonPath("$.authors[0].imageUrl", is("http://localhost/members/profileImage/111")));
            verify(recordService).getPublicRecord(recordId);
        }

        @Test
        void getRecordDetails_recordNotFound_httpStatusIsNotFound() throws Exception {
            // given
            when(recordService.getPublicRecord(any(Long.class))).thenThrow(new EntityNotFoundException(Record.class, recordId));

            // when
            mockMvc.perform(get("/records/browse/{recordId}", recordId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.httpStatus", is("NOT_FOUND")))
                    .andExpect(jsonPath("$.message", is(String.format("Record was not found for {id=%d}", recordId))));
        }
    }
}
