package mk.ukim.finki.manurepoapi.service.impl;

import mk.ukim.finki.manurepoapi.dto.request.AccountRequest;
import mk.ukim.finki.manurepoapi.dto.request.ChangePasswordRequest;
import mk.ukim.finki.manurepoapi.dto.request.EditAccountRequest;
import mk.ukim.finki.manurepoapi.enums.AcademicDegree;
import mk.ukim.finki.manurepoapi.enums.AcademicRank;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.MemberType;
import mk.ukim.finki.manurepoapi.enums.Role;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.exception.InvalidTokenException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.ProfileImage;
import mk.ukim.finki.manurepoapi.model.VerificationToken;
import mk.ukim.finki.manurepoapi.repository.AccountRepository;
import mk.ukim.finki.manurepoapi.security.model.UserPrincipal;
import mk.ukim.finki.manurepoapi.service.VerificationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    VerificationTokenService verificationTokenService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AccountServiceImpl accountService;

    @Mock
    Authentication authentication;

    private final Long accountId = 1L;

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder().id(accountId).build();
    }

    @Nested
    class GetAccount {
        @Test
        void getAccount_accountExists_accountIsReturned() {
            // given
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            // when
            Account actualAccount = accountService.getAccount(accountId);

            // then
            assertThat(actualAccount).isEqualTo(account);
        }

        @Test
        void getAccount_accountDoesNotExists_EntityNotFoundExceptionIsThrown() {
            // given
            when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> accountService.getAccount(accountId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Account was not found for {id=1}");
        }
    }

    @Nested
    class AuthenticatedUserActions {

        @BeforeEach
        void setUp() {
            UserPrincipal userPrincipal = new UserPrincipal(accountId, Role.ROLE_USER);
            when(authentication.getPrincipal()).thenReturn(userPrincipal);
        }

        @Test
        void getAccount_givenAuthentication_accountIsReturned() {
            // given
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            // when
            Account actualAccount = accountService.getAccount(authentication);

            // then
            assertThat(actualAccount).isEqualTo(account);
        }

        @Test
        void getAccountRef_givenAuthentication_accountRefIsReturned() {
            // given
            when(accountRepository.getOne(accountId)).thenReturn(account);

            // when
            Account actualAccount = accountService.getAccountRef(authentication);

            // then
            assertThat(actualAccount).isEqualTo(account);
        }

        @Test
        void setProfileImage_newProfileImage_shouldSetImageAndReturnAccount() throws IOException {
            // given
            MockMultipartFile mockImageFile = new MockMultipartFile("name", "", "text/plain", "content".getBytes());
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(accountRepository.save(any(Account.class))).then(returnsFirstArg());
            ProfileImage expectedProfileImage = ProfileImage.builder()
                    .id(null)
                    .data("content".getBytes())
                    .contentType("text/plain")
                    .build();
            // when
            Account actualAccount = accountService.setProfileImage(authentication, mockImageFile);

            // then
            assertThat(actualAccount).isEqualTo(account);
            verify(accountRepository).save(argThat(argAccount -> argAccount.getProfileImage().equals(expectedProfileImage)));
        }

        @Test
        void setProfileImage_editProfileImage_shouldSetNewImageAndReturnAccount() throws IOException {
            // given
            MockMultipartFile mockNewImageFile = new MockMultipartFile("name", "", "image/png", "newImage".getBytes());

            ProfileImage oldProfileImage = ProfileImage.builder()
                    .id(1L)
                    .data("oldImage".getBytes())
                    .contentType("image/jpeg")
                    .build();
            account.setProfileImage(oldProfileImage);

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(accountRepository.save(any(Account.class))).then(returnsFirstArg());

            ProfileImage expectedProfileImage = ProfileImage.builder()
                    .id(1L)
                    .data("newImage".getBytes())
                    .contentType("image/png")
                    .build();

            // when
            Account actualAccount = accountService.setProfileImage(authentication, mockNewImageFile);

            // then
            assertThat(actualAccount).isEqualTo(account);
            verify(accountRepository).save(argThat(argAccount -> argAccount.getProfileImage().equals(expectedProfileImage)));
        }

        @Test
        void deleteProfileImage_givenAuthentication_setNullForProfileImage() {
            // given
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            // when
            accountService.deleteProfileImage(authentication);

            // then
            verify(accountRepository).save(argThat(argAccount -> Objects.isNull(argAccount.getProfileImage())));
        }

        @Test
        void editPersonalInfo_givenEditAccountRequest_patchesAccountDataExceptEmail() {
            // given
            account.setEmail("old@email.com");
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            EditAccountRequest editAccountRequest = EditAccountRequest.builder()
                    .email("new@email.com")
                    .firstName("firstName")
                    .lastName("lastName")
                    .memberType(MemberType.CORRESPONDING)
                    .department(Department.A)
                    .academicDegree(AcademicDegree.AAS)
                    .academicRank(AcademicRank.ASSISTANT)
                    .shortBio("shortBio")
                    .phoneNumber("phoneNumber")
                    .workplace("workplace")
                    .build();
            Account expectedAccount = Account.builder()
                    .id(accountId)
                    .email("old@email.com")
                    .firstName("firstName")
                    .lastName("lastName")
                    .memberType(MemberType.CORRESPONDING)
                    .department(Department.A)
                    .academicDegree(AcademicDegree.AAS)
                    .academicRank(AcademicRank.ASSISTANT)
                    .shortBio("shortBio")
                    .phoneNumber("phoneNumber")
                    .workplace("workplace")
                    .build();

            when(accountRepository.save(expectedAccount)).then(returnsFirstArg());

            // when
            Account actualAccount = accountService.editPersonalInfo(authentication, editAccountRequest);

            // then
            assertThat(actualAccount).isEqualTo(expectedAccount);
        }

        @Nested
        class ChangePassword {

            private final String currentPassword = "currentPassword";
            private final String newPassword = "newPassword";
            private final String encodedNewPassword = "encodedNewPassword";

            private ChangePasswordRequest changePasswordRequest;

            @BeforeEach
            void setUp() {
                account.setPassword(currentPassword);
                when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
                changePasswordRequest = ChangePasswordRequest.builder()
                        .currentPassword(currentPassword)
                        .password(newPassword)
                        .build();
            }

            @Test
            void changePassword_oldPasswordIsCorrect_passwordIsChanged() {
                // given
                when(passwordEncoder.matches(currentPassword, currentPassword)).thenReturn(true);
                when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

                // when
                accountService.changePassword(authentication, changePasswordRequest);

                // then
                verify(accountRepository).save(argThat(argAccount -> argAccount.getPassword().equals(encodedNewPassword)));
            }

            @Test
            void changePassword_oldPasswordIsIncorrect_requestRejectedAndRequestThrown() {
                // given
                when(passwordEncoder.matches(currentPassword, currentPassword)).thenReturn(false);

                // when, then
                assertThatThrownBy(() -> accountService.changePassword(authentication, changePasswordRequest))
                        .isInstanceOf(ResponseStatusException.class)
                        .hasFieldOrPropertyWithValue("reason", "No or invalid authentication details provided")
                        .hasFieldOrPropertyWithValue("status", HttpStatus.UNAUTHORIZED);
                verifyNoMoreInteractions(accountRepository);
            }
        }
    }

    @Test
    void isEmailAvailable_givenEmailAddress_shouldCallRepositoryMethodAndInvertResult() {
        // given
        String email = "email@test.com";
        when(accountRepository.existsByEmail(email)).thenReturn(true);

        // when
        boolean emailAvailable = accountService.isEmailAvailable(email);

        // then
        assertThat(emailAvailable).isFalse();
    }

    @Test
    void createAccount_givenAccountRequestForCreation_createAccountAndPopulateData() {
        // given
        final String rawPassword = "password";
        final String encodedPassword = "encodedPassword";

        AccountRequest accountRequest = AccountRequest.builder()
                .email("email")
                .password(rawPassword)
                .confirmPassword(rawPassword)
                .firstName("firstName")
                .lastName("lastName")
                .memberType(MemberType.CORRESPONDING)
                .department(Department.A)
                .build();

        Account expectedAccount = Account.builder()
                .email("email")
                .password(encodedPassword)
                .firstName("firstName")
                .lastName("lastName")
                .memberType(MemberType.CORRESPONDING)
                .department(Department.A)
                .build();

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(accountRepository.save(expectedAccount)).then(returnsFirstArg());

        // when
        Account actualAccount = accountService.createAccount(accountRequest);

        // then
        assertThat(actualAccount).isEqualTo(expectedAccount);
    }

    @Nested
    class ConfirmRegistration {

        private final String token = "token";
        private VerificationToken verificationToken;

        @BeforeEach
        void setUp() {
            verificationToken = VerificationToken.builder()
                    .account(account)
                    .token(token)
                    .build();
            when(verificationTokenService.getToken(token)).thenReturn(verificationToken);
        }

        @Test
        void confirmRegistration_expiredToken_throwsInvalidTokenException() {
            // given
            when(verificationTokenService.isTokenExpired(verificationToken)).thenReturn(true);

            // when, then
            assertThatThrownBy(() -> accountService.confirmRegistration(token))
                    .isInstanceOf(InvalidTokenException.class);
            verifyNoMoreInteractions(accountRepository);
            verifyNoMoreInteractions(verificationTokenService);
        }

        @Test
        void confirmRegistration_validToken_enablesAccountWithAppropriateIdAndDeletesVerificationToken() {
            // given
            when(verificationTokenService.isTokenExpired(verificationToken)).thenReturn(false);

            // when
            accountService.confirmRegistration(token);

            // then
            verify(accountRepository).enableAccount(accountId);
            verify(verificationTokenService).deleteToken(verificationToken);
        }
    }
}
