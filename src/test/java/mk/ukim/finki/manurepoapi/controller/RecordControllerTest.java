package mk.ukim.finki.manurepoapi.controller;

import mk.ukim.finki.manurepoapi.dto.request.ManageRecordsFilter;
import mk.ukim.finki.manurepoapi.dto.request.RecordRequest;
import mk.ukim.finki.manurepoapi.dto.request.RecordsFilter;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.PublicationStatus;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.security.MockUserDetailsService;
import mk.ukim.finki.manurepoapi.security.model.UserPrincipal;
import mk.ukim.finki.manurepoapi.service.RecordService;
import mk.ukim.finki.manurepoapi.service.StatisticsService;
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
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RecordController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(MockUserDetailsService.class)
class RecordControllerTest {

    @MockBean
    RecordService recordService;

    @MockBean
    StatisticsService statisticsService;

    @Autowired
    MockMvc mockMvc;

    private final Long recordId = 1L;

    private final Long accountId = 1L;

    private String validUserJwt;

    private final String validRecordRequestJSONPayload = "{" +
            "    \"title\": \"Record Title\"," +
            "    \"authorIds\": [11, 22]," +
            "    \"collection\": \"BOOK\"," +
            "    \"department\": \"MS\"," +
            "    \"subject\": \"Surgery of heart\"," +
            "    \"descriptionOrAbstract\": \"descriptionOrAbstract\"," +
            "    \"keywords\": \"k1, k2, k3\"," +
            "    \"language\": \"English\"," +
            "    \"numPages\": 123," +
            "    \"publicationDate\": \"2021-01-01\"," +
            "    \"publicationStatus\": \"PUBLISHED\"," +
            "    \"privateRecord\": false" +
            "}";

    private RecordRequest expectedRecordRequest;

    @BeforeEach
    void setUp() {
        validUserJwt = String.format("Bearer %s", TestUtils.createValidUserJwt(accountId));
        expectedRecordRequest = RecordRequest.builder()
                .title("Record Title")
                .authorIds(List.of(11L, 22L))
                .collection(Collection.BOOK)
                .department(Department.MS)
                .subject("Surgery of heart")
                .descriptionOrAbstract("descriptionOrAbstract")
                .keywords("k1, k2, k3")
                .language("English")
                .numPages(123)
                .publicationDate(LocalDate.of(2021, 1, 1))
                .publicationStatus(PublicationStatus.PUBLISHED)
                .privateRecord(false)
                .build();
    }

    @ParameterizedTest
    @MethodSource("getRecordsPagePaginationParams")
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

    static List<Arguments> getRecordsPagePaginationParams() {
        return List.of(
                Arguments.of(null, null, null, PageRequest.of(0, 10, Sort.Direction.DESC, "dateArchived")),
                Arguments.of("3", "10", "downloadsCount,desc", PageRequest.of(3, 10, Sort.Direction.DESC, "downloadsCount")),
                Arguments.of("2", "5", "downloadsCount,asc", PageRequest.of(2, 5, Sort.Direction.ASC, "downloadsCount"))
        );
    }

    @Nested
    class GetRecordDetails {

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

    @Test
    void getRecordsPageForAccount_noAuthorizationHeader_requestIsForbidden() throws Exception {
        mockMvc.perform(get("/records/manage"))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("getRecordsPageForAccountPaginationParams")
    void getRecordsPageForAccount_validJwt_returnsRecordsPage(String page, String size, String sort, Pageable expectedPageable) throws Exception {
        // given
        ManageRecordsFilter expectedManageRecordsFilter = ManageRecordsFilter.builder()
                .collection(Collection.AUDIO)
                .privateRecord(true)
                .build();
        Page<Record> recordsPage = new PageImpl<>(List.of(TestUtils.createRecord()));

        when(recordService.getManageRecordsPage(any(ManageRecordsFilter.class), any(Pageable.class), any(Authentication.class)))
                .thenReturn(recordsPage);

        // when, then
        mockMvc.perform(get("/records/manage")
                .queryParam("collection", "AUDIO")
                .queryParam("privateRecord", "true")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .header(HttpHeaders.AUTHORIZATION, validUserJwt))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].collection", is("CONFERENCE_ITEM")))
                .andExpect(jsonPath("$.content[0].collectionName", is("Conference Contribution")))
                .andExpect(jsonPath("$.content[0].department", is("SS")))
                .andExpect(jsonPath("$.content[0].dateArchived", is("2021-01-01T10:10:00")));
        verify(recordService).getManageRecordsPage(eq(expectedManageRecordsFilter), eq(expectedPageable), argThat(auth -> hasId(auth, accountId)));
    }

    private Boolean hasId(Authentication authentication, Long accountId) {
        return Optional.ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserPrincipal)
                .map(principal -> ((UserPrincipal) principal).getAccount())
                .map(Account::getId)
                .map(principalAccountId -> Objects.equals(principalAccountId, accountId))
                .orElse(false);
    }

    static List<Arguments> getRecordsPageForAccountPaginationParams() {
        return List.of(
                Arguments.of(null, null, null, PageRequest.of(0, 12, Sort.Direction.DESC, "dateArchived")),
                Arguments.of("1", "9", "downloadsCount,desc", PageRequest.of(1, 9, Sort.Direction.DESC, "downloadsCount")),
                Arguments.of("1", "9", "id,asc", PageRequest.of(1, 9, Sort.Direction.ASC, "id"))
        );
    }

    @Nested
    class CreateRecord {

        @Test
        void createRecord_noAuthorizationHeader_requestIsForbidden() throws Exception {
            mockMvc.perform(post("/records/manage")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void createRecord_validJwtPresentInvalidPayload_shouldValidatePayloadAndThrowException() throws Exception {
            // given
            String invalidRecordJSONPayload = "{" +
                    "    \"title\": \"\"," +
                    "    \"authorIds\": []," +
                    "    \"collection\": \"OTHER\"," +
                    "    \"department\": \"MBS\"," +
                    "    \"subject\": \"some subject\"," +
                    "    \"descriptionOrAbstract\": \"ha\"," +
                    "    \"keywords\": \"k1, k2, k3\"," +
                    "    \"language\": \"mkd\"," +
                    "    \"numPages\": 123," +
                    "    \"publicationDate\": \"2021-01-01\"," +
                    "    \"publicationStatus\": \"SUBMITTED\"," +
                    "    \"privateRecord\": false" +
                    "}";

            // when, then
            mockMvc.perform(post("/records/manage")
                    .header(HttpHeaders.AUTHORIZATION, validUserJwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidRecordJSONPayload))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("Validation error")))
                    .andExpect(jsonPath("$.subErrors").isArray())
                    .andExpect(jsonPath("$.subErrors", hasSize(2)))
                    .andExpect(jsonPath("$.subErrors[0].field", is("title")))
                    .andExpect(jsonPath("$.subErrors[0].rejectedValue", emptyString()))
                    .andExpect(jsonPath("$.subErrors[0].message", is("Title must not be empty")))
                    .andExpect(jsonPath("$.subErrors[1].message", is("Publication date only applicable for PUBLISHED publication status")));
        }

        @Test
        void createRecord_validJwtPresentValidPayload_shouldCreateNewRecord() throws Exception {
            // given
            when(recordService.createRecord(any(), any())).thenReturn(TestUtils.createRecordWithFilesAndAuthors(recordId));

            // when, then
            mockMvc.perform(post("/records/manage")
                    .header(HttpHeaders.AUTHORIZATION, validUserJwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validRecordRequestJSONPayload))
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, is("http://localhost/records/" + recordId)));

            verify(recordService).createRecord(argThat(auth -> hasId(auth, accountId)), eq(expectedRecordRequest));
        }

    }

    @Nested
    class DeleteRecord {

        @Test
        void deleteRecord_noAuthorizationHeader_requestIsForbidden() throws Exception {
            mockMvc.perform(delete("/records/manage/{recordId}", recordId))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteRecord_validJwtRecordDoesNotBelongToAccount_returnsBadRequest() throws Exception {
            // given
            doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                    .when(recordService)
                    .deleteRecord(any(Authentication.class), eq(recordId));

            // when, then
            mockMvc.perform(delete("/records/manage/{recordId}", recordId)
                    .header(HttpHeaders.AUTHORIZATION,validUserJwt))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void deleteRecord_validJwt_recordDeletedStatusNoContent() throws Exception {
            // when, then
            mockMvc.perform(delete("/records/manage/{recordId}", recordId)
                    .header(HttpHeaders.AUTHORIZATION, validUserJwt))
                    .andExpect(status().isNoContent());

            verify(recordService).deleteRecord(argThat(authentication -> hasId(authentication, accountId)), eq(recordId));
        }
    }

    @Nested
    class EditRecord {

        @Test
        void editRecord_noAuthorizationHeader_requestIsForbidden() throws Exception {
            mockMvc.perform(patch("/records/manage/{recordId}", recordId))
                    .andExpect(status().isForbidden());
        }

        @Test
        void editRecord_validJwtRecordDoesNotBelongToAccount_returnsNotFound() throws Exception {
            // given
            when(recordService.editRecord(any(Authentication.class), eq(recordId), any(RecordRequest.class)))
                    .thenThrow(new EntityNotFoundException(Record.class, recordId));

            // when, then
            mockMvc.perform(patch("/records/manage/{recordId}", recordId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validRecordRequestJSONPayload)
                    .header(HttpHeaders.AUTHORIZATION, validUserJwt))
                    .andExpect(status().isNotFound());
        }

        @Test
        void editRecord_validJwtRecordValidPayload_recordIsEditedAndStatusOK() throws Exception {
            // given
            when(recordService.editRecord(any(Authentication.class), eq(recordId), any(RecordRequest.class)))
                    .thenReturn(TestUtils.createRecordWithFilesAndAuthors(recordId));

            // when, then
            mockMvc.perform(patch("/records/manage/{recordId}", recordId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validRecordRequestJSONPayload)
                    .header(HttpHeaders.AUTHORIZATION, validUserJwt))
                    .andExpect(status().isOk());
            verify(recordService).editRecord(argThat(auth -> hasId(auth, accountId)), eq(recordId), eq(expectedRecordRequest));
        }

    }
}
