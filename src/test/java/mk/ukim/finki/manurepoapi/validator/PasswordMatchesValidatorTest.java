package mk.ukim.finki.manurepoapi.validator;

import mk.ukim.finki.manurepoapi.dto.request.AccountRequest;
import mk.ukim.finki.manurepoapi.dto.request.PasswordCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PasswordMatchesValidatorTest {

    PasswordMatchesValidator passwordMatchesValidator;

    @BeforeEach
    void setUp() {
        passwordMatchesValidator = new PasswordMatchesValidator();
    }

    /*
         --- Interface based IDM ---

         Two parameters: password (String) , confirmPassword (String)
         Returns: Boolean

         ID  Characteristic              Block 1                     Block 2                     Block 3
         ---------------------------------------------------------------------------------------------------------------------
         C1  State of password           password is null            password is empty           password is not empty
         C2  State of confirmPassword    confirmPassword is null     confirmPassword is empty    confirmPassword is not empty

         COMPLETENESS and DISJOINTNESS satisfied

         Base Choice Coverage (BCC): Test Requirements (no infeasible TRs)
         -----------------------------------------------------------------
         password is not empty, confirmPassword is not empty (BASE TEST)

         password is not empty, confirmPassword is empty
         password is not empty, confirmPassword is null

         password is empty,     confirmPassword is not empty
         password is null,      confirmPassword is not empty
    */
    @ParameterizedTest
    @CsvSource(value = {
            "Password1!, Password1!, true",
            "Password1!, password, false",
            "Password1!, '', false",
            "Password1!, null, false",
            "'', Password1!, true",
            "null, Password1!, true"
    }, nullValues = "null")
    void isValid(String password, String confirmPassword, Boolean expectedValidity) {
        // given
        PasswordCredentials passwordCredentials = AccountRequest.builder()
                .password(password)
                .confirmPassword(confirmPassword)
                .build();

        // when
        boolean isValid = passwordMatchesValidator.isValid(passwordCredentials, null);

        // then
        assertThat(isValid).isEqualTo(expectedValidity);
    }
}
