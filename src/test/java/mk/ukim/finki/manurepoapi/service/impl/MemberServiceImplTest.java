package mk.ukim.finki.manurepoapi.service.impl;

import mk.ukim.finki.manurepoapi.dto.request.MembersFilter;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.ProfileImage;
import mk.ukim.finki.manurepoapi.repository.AccountRepository;
import mk.ukim.finki.manurepoapi.repository.ProfileImageRepository;
import mk.ukim.finki.manurepoapi.repository.projection.MemberProjection;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    ProfileImageRepository profileImageRepository;

    @InjectMocks
    MemberServiceImpl memberService;

    @Test
    void getMembersPage_givenBrowsingFilters_shouldConstructSpecAndCallRepository() {
        // given
        MembersFilter membersFilter = new MembersFilter();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> expectedAccountsPage = new PageImpl<>(List.of(new Account()));

        when(accountRepository.findAll(ArgumentMatchers.<Specification<Account>>any(), any(Pageable.class))).thenReturn(expectedAccountsPage);

        // when
        Page<Account> actualAccountsPage = memberService.getMembersPage(membersFilter, pageable);

        // then
        assertThat(actualAccountsPage).isEqualTo(expectedAccountsPage);
    }

    @Nested
    class GetMemberDetails {

        private final Long accountId = 1L;

        @Test
        void getMemberDetails_accountDoesNotExists_exceptionIsThrown() {
            // given
            when(accountRepository.findByIdAndEnabledTrue(accountId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> memberService.getMemberDetails(accountId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Account was not found for {id=1}");
        }

        @Test
        void getMemberDetails_accountExists_accountIsReturned() {
            // given
            Account account = Account.builder().id(accountId).build();
            when(accountRepository.findByIdAndEnabledTrue(accountId)).thenReturn(Optional.of(account));

            // when
            Account actualAccount = memberService.getMemberDetails(accountId);

            // then
            assertThat(actualAccount).isEqualTo(account);
        }
    }

    @Nested
    class GetProfileImage {

        private final Long profileImageId = 10L;

        @Test
        void getProfileImage_profileImageDoesNotExists_exceptionIsThrown() {
            // given
            when(profileImageRepository.findById(profileImageId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> memberService.getProfileImage(profileImageId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("ProfileImage was not found for {id=10}");
        }

        @Test
        void getProfileImage_profileImageExists_accountIsReturned() {
            // given
            ProfileImage profileImage = ProfileImage.builder().id(profileImageId).build();
            when(profileImageRepository.findById(profileImageId)).thenReturn(Optional.of(profileImage));

            // when
            ProfileImage actualProfileImage = memberService.getProfileImage(profileImageId);

            // then
            assertThat(actualProfileImage).isEqualTo(profileImage);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "10, 10",
            "20, 15"
    })
    void searchMembersByName_givenSearchQuery_shouldConstructPageRequestAndCallRepository(Integer resultSize, Integer expectedPageSize) {
        // given
        String query = "search";
        PageRequest expectedPageRequest = PageRequest.of(0, expectedPageSize);
        List<MemberProjection> expectedMembers = List.of(TestUtils.createMember(1L, "Member Projection"));
        when(accountRepository.searchByName(query, expectedPageRequest)).thenReturn(expectedMembers);

        // when
        List<MemberProjection> actualMembers = memberService.searchMembersByName(query, resultSize);

        // then
        assertThat(actualMembers).isEqualTo(expectedMembers);
    }


}
