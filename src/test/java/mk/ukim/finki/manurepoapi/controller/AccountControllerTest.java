package mk.ukim.finki.manurepoapi.controller;

import mk.ukim.finki.manurepoapi.dto.request.AccountRequest;
import mk.ukim.finki.manurepoapi.dto.request.ChangePasswordRequest;
import mk.ukim.finki.manurepoapi.dto.request.EditAccountRequest;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.MemberType;
import mk.ukim.finki.manurepoapi.exception.InvalidTokenException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.ProfileImage;
import mk.ukim.finki.manurepoapi.security.MockUserDetailsService;
import mk.ukim.finki.manurepoapi.service.AccountService;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static mk.ukim.finki.manurepoapi.utils.TestUtils.hasId;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(MockUserDetailsService.class)
class AccountControllerTest {

    @MockBean
    AccountService accountService;

    @Autowired
    MockMvc mockMvc;

    @Nested
    class CreateAccount {

        @Test
        void createAccount_invalidAccountRequestPayload_badRequestWithAppropriateErrorMessage() throws Exception {
            // given
            final String invalidAccountRequestJSON = "{" +
                    "  \"email\": \"Aleksandar.Dimoski\"," +
                    "  \"password\": \"Password1!\"," +
                    "  \"confirmPassword\": \"Password1!\"," +
                    "  \"firstName\": \"Aleksandar\"," +
                    "  \"lastName\": \"Dimoski\"," +
                    "  \"memberType\": \"CORRESPONDING\"," +
                    "  \"department\": \"MBS\"" +
                    "}";

            when(accountService.isEmailAvailable(any(String.class))).thenReturn(true);

            // when, then
            mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidAccountRequestJSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is("Validation error")))
                    .andExpect(jsonPath("$.subErrors").isArray())
                    .andExpect(jsonPath("$.subErrors", hasSize(1)))
                    .andExpect(jsonPath("$.subErrors[0].field", is("email")))
                    .andExpect(jsonPath("$.subErrors[0].rejectedValue", is("Aleksandar.Dimoski")))
                    .andExpect(jsonPath("$.subErrors[0].message", is("Invalid email address")));
        }

        @Test
        void createAccount_invalidJSONPayload_badRequestWithAppropriateErrorMessage() throws Exception {
            // given
            final String malformedAccountRequestJSON = "{" +
                    "  \"email\": \"Aleksandar.Dimoski@email.com\"," +
                    "  \"password\": \"Password1!\"," +
                    "  \"confirmPassword\": \"Password1!\"," +
                    "  \"firstName\": \"Aleksandar\"," +
                    "  \"lastName\": \"Dimoski\"," +
                    "  \"memberType\": \"123\"," +
                    "  \"department\": \"notADepartment\"" +
                    "}";

            when(accountService.isEmailAvailable(any(String.class))).thenReturn(true);

            // when, then
            mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(malformedAccountRequestJSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message", is("Malformed JSON request")));
        }

        @Test
        void createAccount_validAccountRequestPayload_accountIsCreatedAndReturned() throws Exception {
            // given
            final String validAccountRequestJSON = "{" +
                    "  \"email\": \"Aleksandar.Dimoski@email.com\"," +
                    "  \"password\": \"Password1!\"," +
                    "  \"confirmPassword\": \"Password1!\"," +
                    "  \"firstName\": \"Aleksandar\"," +
                    "  \"lastName\": \"Dimoski\"," +
                    "  \"memberType\": \"CORRESPONDING\"," +
                    "  \"department\": \"MBS\"" +
                    "}";

            AccountRequest expectedAccountRequest = AccountRequest.builder()
                    .email("Aleksandar.Dimoski@email.com")
                    .password("Password1!")
                    .confirmPassword("Password1!")
                    .firstName("Aleksandar")
                    .lastName("Dimoski")
                    .memberType(MemberType.CORRESPONDING)
                    .department(Department.MBS)
                    .build();

            when(accountService.createAccount(expectedAccountRequest)).thenReturn(TestUtils.createAccount("Aleksandar", "Dimoski"));
            when(accountService.isEmailAvailable(any(String.class))).thenReturn(true);

            // when, then
            mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validAccountRequestJSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.email", is("Aleksandar.Dimoski@email.com")))
                    .andExpect(jsonPath("$.firstName", is("Aleksandar")))
                    .andExpect(jsonPath("$.lastName", is("Dimoski")))
                    .andExpect(jsonPath("$.memberType", is("Corresponding Member")))
                    .andExpect(jsonPath("$.department", is("Department of Natural, Mathematical and Biotechnological Sciences")));

            verify(accountService).createAccount(expectedAccountRequest);
        }
    }

    @Nested
    class ConfirmRegistration {
        private final String uuidVerificationToken = "uuidToken";

        @Test
        void confirmRegistration_validVerificationToken_redirectedToSuccessPage() throws Exception {
            // given
            final String expectedRedirectionUrl = "http://localhost:4200/registration/success";

            // when, then
            mockMvc.perform(get("/accounts/confirmRegistration")
                    .queryParam("token", uuidVerificationToken))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(expectedRedirectionUrl));

            verify(accountService).confirmRegistration(uuidVerificationToken);
        }

        @Test
        void confirmRegistration_invalidVerificationToken_redirectedToFailurePage() throws Exception {
            // given
            final String expectedRedirectionUrl = "http://localhost:4200/registration/failed";
            doThrow(new InvalidTokenException()).when(accountService).confirmRegistration(any(String.class));

            // when, then
            mockMvc.perform(get("/accounts/confirmRegistration")
                    .queryParam("token", uuidVerificationToken))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(expectedRedirectionUrl));

            verify(accountService).confirmRegistration(uuidVerificationToken);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void checkEmailAvailable_givenEmailAddress_returnsBoolean(Boolean emailAvailable) throws Exception {
        // given
        final String email = "user@manu.com";
        when(accountService.isEmailAvailable(anyString())).thenReturn(emailAvailable);

        // when, then
        mockMvc.perform(get("/accounts/emailAvailable")
                .header("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(emailAvailable)));

        verify(accountService).isEmailAvailable(email);
    }

    @Nested
    class AuthenticatedAPIs {
        private final Long accountId = 1L;
        private String validUserJwt;

        @BeforeEach
        void setUp() {
            validUserJwt = String.format("Bearer %s", TestUtils.createValidUserJwt(accountId));
        }

        /*
            Logic coverage with CACC
            Predicate: (a or b)
            Elaboration for clauses:
                a - contentType is null
                b - contentType starts with 'image'
            Test cases: TF (not exactly feasible), FT, FF
        */
        @Nested
        class SetProfileImage {

            @Test
            void setProfileImage_contentTypeIsNull_badRequest() throws Exception {
                // given
                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                        "imageFile", "hello.txt", null, "data".getBytes()
                );

                // when, then
                mockMvc.perform(multipart("/accounts/profileImage")
                        .file(mockMultipartFile)
                        .header(HttpHeaders.AUTHORIZATION, validUserJwt))
                        .andExpect(status().isBadRequest())
                        .andExpect(status().reason("No image file present in the request"));
                verifyNoInteractions(accountService);
            }

            @Test
            void setProfileImage_contentTypeIsNotImage_badRequest() throws Exception {
                // given
                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                        "imageFile", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "data".getBytes()
                );

                // when, then
                mockMvc.perform(multipart("/accounts/profileImage")
                        .file(mockMultipartFile)
                        .header(HttpHeaders.AUTHORIZATION, validUserJwt))
                        .andExpect(status().isBadRequest())
                        .andExpect(status().reason("No image file present in the request"));
                verifyNoInteractions(accountService);
            }

            @Test
            void setProfileImage_contentTypeIsValid_shouldSetProfileImageAndReturnAvatar() throws Exception {
                // given
                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                        "imageFile", "profileImage.png", MediaType.IMAGE_PNG_VALUE, "data".getBytes()
                );

                final long profileImageId = 5L;
                Account account = Account.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .profileImage(ProfileImage.builder().id(profileImageId).build())
                        .build();
                when(accountService.setProfileImage(any(Authentication.class), any(MultipartFile.class)))
                        .thenReturn(account);

                // when, then
                mockMvc.perform(multipart("/accounts/profileImage")
                        .file(mockMultipartFile)
                        .header(HttpHeaders.AUTHORIZATION, validUserJwt))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("firstName", is("firstName")))
                        .andExpect(jsonPath("lastName", is("lastName")))
                        .andExpect(jsonPath("imageUrl", is("http://localhost/members/profileImage/" + profileImageId)));

                verify(accountService).setProfileImage(argThat(auth -> hasId(auth, accountId)), eq(mockMultipartFile));
            }
        }

        @Test
        void removeProfileImage_validJwt_noContent() throws Exception {
            // when, then
            mockMvc.perform(delete("/accounts/profileImage")
                    .header(HttpHeaders.AUTHORIZATION, validUserJwt))
                    .andExpect(status().isNoContent());
            verify(accountService).deleteProfileImage(argThat(auth -> hasId(auth, accountId)));
        }

        @Test
        void getPersonalInfo_validJwt_returnsEditAccountRequest() throws Exception {
            // given
            when(accountService.getAccount(any(Authentication.class))).thenReturn(TestUtils.createAccount("Aleksandar", "Dimoski"));

            // when, then
            mockMvc.perform(get("/accounts/edit")
                    .header(HttpHeaders.AUTHORIZATION, validUserJwt))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email", is("Aleksandar.Dimoski@email.com")))
                    .andExpect(jsonPath("$.memberType", is("CORRESPONDING")))
                    .andExpect(jsonPath("$.department", is("MBS")))
                    .andExpect(jsonPath("$.academicDegree").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$.academicRank").value(IsNull.nullValue()));

            verify(accountService).getAccount(ArgumentMatchers.<Authentication>argThat(auth -> hasId(auth, accountId)));
        }

        @Nested
        class EditPersonalInfo {

            @Test
            void editPersonalInfo_invalidJsonPayload_validationIsTriggeredBadRequestReturned() throws Exception {
                // given
                final String invalidEditAccountRequestJSON = "{" +
                        "    \"firstName\": \"\"," +
                        "    \"lastName\": \"\"," +
                        "    \"memberType\": null," +
                        "    \"department\": null," +
                        "    \"academicDegree\": \"BS\"," +
                        "    \"academicRank\": \"STUDENT\"" +
                        "}";

                // when, then
                mockMvc.perform(patch("/accounts/edit")
                        .header(HttpHeaders.AUTHORIZATION, validUserJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEditAccountRequestJSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", is("Validation error")))
                        .andExpect(jsonPath("$.subErrors").isArray())
                        .andExpect(jsonPath("$.subErrors", hasSize(4)))
                        .andExpect(jsonPath("$.subErrors[*].field", containsInAnyOrder("firstName", "lastName", "memberType", "department")))
                        .andExpect(jsonPath("$.subErrors[?(@.field=='firstName')].rejectedValue", contains("")))
                        .andExpect(jsonPath("$.subErrors[?(@.field=='firstName')].message", contains("You must provide first name")));

                verifyNoInteractions(accountService);
            }

            @Test
            void editPersonalInfo_validJsonPayload_accountIsEdited() throws Exception {
                // given
                final String validEditAccountRequestJSON = "{" +
                        "    \"email\": \"emailThatShouldNotBeDeserialized\"," +
                        "    \"firstName\": \"Aleksandar\"," +
                        "    \"lastName\": \"Dimoski\"," +
                        "    \"memberType\": \"CORRESPONDING\"," +
                        "    \"department\": \"MBS\"" +
                        "}";

                EditAccountRequest expectedEditAccountRequest = EditAccountRequest.builder()
                        .email(null)
                        .firstName("Aleksandar")
                        .lastName("Dimoski")
                        .memberType(MemberType.CORRESPONDING)
                        .department(Department.MBS)
                        .build();

                Account account = TestUtils.createAccount("Aleksandar", "Dimoski");
                when(accountService.editPersonalInfo(any(Authentication.class), any(EditAccountRequest.class))).thenReturn(account);

                // when, then
                mockMvc.perform(patch("/accounts/edit")
                        .header(HttpHeaders.AUTHORIZATION, validUserJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validEditAccountRequestJSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.email", is("Aleksandar.Dimoski@email.com")))
                        .andExpect(jsonPath("$.firstName", is("Aleksandar")))
                        .andExpect(jsonPath("$.lastName", is("Dimoski")))
                        .andExpect(jsonPath("$.memberType", is("CORRESPONDING")))
                        .andExpect(jsonPath("$.department", is("MBS")));

                verify(accountService).editPersonalInfo(argThat(auth -> hasId(auth, accountId)), eq(expectedEditAccountRequest));
            }
        }

        @Nested
        class ChangePassword {

            @Test
            void changePassword_validChangePasswordRequest_passwordIsChangedReturnNoContent() throws Exception {
                // given
                final String validChangePasswordRequest = "{" +
                        "    \"currentPassword\": \"OldPassword1!\"," +
                        "    \"password\": \"NewPassword1!\"," +
                        "    \"confirmPassword\": \"NewPassword1!\"" +
                        "}";

                ChangePasswordRequest expectedChangePasswordRequest = ChangePasswordRequest.builder()
                        .currentPassword("OldPassword1!")
                        .password("NewPassword1!")
                        .confirmPassword("NewPassword1!")
                        .build();

                // when, then
                mockMvc.perform(patch("/accounts/password")
                        .header(HttpHeaders.AUTHORIZATION, validUserJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validChangePasswordRequest))
                        .andExpect(status().isNoContent());

                verify(accountService).changePassword(argThat(auth -> hasId(auth, accountId)), eq(expectedChangePasswordRequest));
            }

            @Test
            void changePassword_invalidChangePasswordRequest_badRequest() throws Exception {
                // given
                final String invalidChangePasswordRequest = "{" +
                        "    \"currentPassword\": \"\"," +
                        "    \"password\": \"NewPassword1!\"," +
                        "    \"confirmPassword\": \"NewPassword1!\"" +
                        "}";

                // when, then
                mockMvc.perform(patch("/accounts/password")
                        .header(HttpHeaders.AUTHORIZATION, validUserJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidChangePasswordRequest))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", is("Validation error")))
                        .andExpect(jsonPath("$.subErrors").isArray())
                        .andExpect(jsonPath("$.subErrors", hasSize(1)))
                        .andExpect(jsonPath("$.subErrors[0].field", is("currentPassword")))
                        .andExpect(jsonPath("$.subErrors[0].rejectedValue", is("")))
                        .andExpect(jsonPath("$.subErrors[0].message", is("You must enter your current password")));

                verifyNoInteractions(accountService);
            }
        }

    }

}
