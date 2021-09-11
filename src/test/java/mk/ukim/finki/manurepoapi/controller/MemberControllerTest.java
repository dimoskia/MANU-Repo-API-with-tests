package mk.ukim.finki.manurepoapi.controller;

import mk.ukim.finki.manurepoapi.dto.request.MembersFilter;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.ProfileImage;
import mk.ukim.finki.manurepoapi.repository.projection.MemberProjection;
import mk.ukim.finki.manurepoapi.security.MockUserDetailsService;
import mk.ukim.finki.manurepoapi.service.MemberService;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(MockUserDetailsService.class)
class MemberControllerTest {

    @MockBean
    MemberService memberService;

    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("getNewAccountsPagePaginationParams")
    void getMembersPage_filtersSpecifiedAsQueryParams_returnRecordsPage(String page, String size, String sort, Pageable expectedPageable) throws Exception {
        // given
        MembersFilter expectedMembersFilter = MembersFilter.builder()
                .searchTerm("alek")
                .department(Department.LLS)
                .firstLetter("D")
                .build();
        Page<Account> accountPage = new PageImpl<>(List.of(TestUtils.createAccount("Aleksandar", "Dimoski")));

        when(memberService.getMembersPage(expectedMembersFilter, expectedPageable)).thenReturn(accountPage);

        // when, then
        MockHttpServletRequestBuilder requestBuilder = get("/members")
                .queryParam("searchTerm", "alek")
                .queryParam("department", "LLS")
                .queryParam("firstLetter", "D")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName", is("Aleksandar")))
                .andExpect(jsonPath("$.content[0].lastName", is("Dimoski")))
                .andExpect(jsonPath("$.content[0].department", is("Department of Natural, Mathematical and Biotechnological Sciences")))
                .andExpect(jsonPath("$.content[0].memberType", is("Corresponding Member")))
                .andExpect(jsonPath("$.content[0].imageUrl", IsNull.nullValue()));

        verify(memberService).getMembersPage(expectedMembersFilter, expectedPageable);
    }

    static List<Arguments> getNewAccountsPagePaginationParams() {
        return List.of(
                Arguments.of(null, null, null, PageRequest.of(0, 15, Sort.Direction.ASC, "lastName")),
                Arguments.of(null, "7", "firstName,desc", PageRequest.of(0, 7, Sort.Direction.DESC, "firstName")),
                Arguments.of("2", null, "firstName,asc", PageRequest.of(2, 15, Sort.Direction.ASC, "firstName"))
        );
    }

    @Test
    void getMemberDetails_givenAccountId_returnsMemberDetails() throws Exception {
        // given
        final Long accountId = 10L;
        Account account = TestUtils.createAccount("Aleksandar", "Dimoski");
        when(memberService.getMemberDetails(anyLong())).thenReturn(account);

        // when, then
        mockMvc.perform(get("/members/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is("Aleksandar.Dimoski@email.com")))
                .andExpect(jsonPath("$.memberType", is("Corresponding Member")))
                .andExpect(jsonPath("$.department", is("Department of Natural, Mathematical and Biotechnological Sciences")))
                .andExpect(jsonPath("$.academicDegree").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.academicRank").value(IsNull.nullValue()));

        verify(memberService).getMemberDetails(accountId);
    }

    @Test
    void getProfileImage_givenImageId_imageIsFetchedAndReturned() throws Exception {
        // given
        final Long imageId = 1L;
        byte[] imageDataBytes = "imageData".getBytes();
        ProfileImage profileImage = ProfileImage.builder()
                .data(imageDataBytes)
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .build();

        when(memberService.getProfileImage(anyLong())).thenReturn(profileImage);

        // when, then
        mockMvc.perform(get("/members/profileImage/{imageId}", imageId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes(imageDataBytes));

        verify(memberService).getProfileImage(imageId);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, 10",
            "5, 5"
    }, nullValues = "null")
    void searchMembersByName_filtersSpecifiedInQueryParams_returnsListOfMembers(String resultSize, Integer expectedResultSize) throws Exception {
        // given
        String querySearchTerm = "aleksandar";
        MemberProjection memberProjection = TestUtils.createMember(1L, "Aleksandar Dimoski");
        when(memberService.searchMembersByName(anyString(), anyInt())).thenReturn(List.of(memberProjection));

        // when, then
        mockMvc.perform(get("/members/search")
                .queryParam("query", querySearchTerm)
                .queryParam("resultSize", resultSize))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].fullName", is("Aleksandar Dimoski")));

        verify(memberService).searchMembersByName(querySearchTerm, expectedResultSize);
    }
}
