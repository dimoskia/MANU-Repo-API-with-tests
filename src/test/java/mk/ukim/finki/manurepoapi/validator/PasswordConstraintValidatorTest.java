package mk.ukim.finki.manurepoapi.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordConstraintValidatorTest {

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Mock
    ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    @InjectMocks
    PasswordConstraintValidator validator;

    @Test
    void isValid_passwordIsValid_returnsTrue() {
        // given
        final String validPassword = "Password1!";

        // when
        boolean isValid = validator.isValid(validPassword, constraintValidatorContext);

        // then
        assertThat(isValid).isTrue();
        verifyNoInteractions(constraintValidatorContext);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Pass1!, 'Password must be 8 or more characters in length.'",
            "Paaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaassword1!, 'Password must be no more than 30 characters in length.'",
            "password1!, 'Password must contain 1 or more uppercase characters.'",
            "PASSWORD1!, 'Password must contain 1 or more lowercase characters.'",
            "Password!, 'Password must contain 1 or more digit characters.'",
            "Password1, 'Password must contain 1 or more special characters.'",
            "Password 1!, 'Password contains a whitespace character.'",
            "password!, 'Password must contain 1 or more uppercase characters. Password must contain 1 or more digit characters.'"
    })
    void isValid_passwordIsInvalid_returnsFalseAndSetsErrorMessage(String invalidPassword, String expectedErrorMessage) {
        // given
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addConstraintViolation()).thenReturn(constraintValidatorContext);

        // when
        boolean isValid = validator.isValid(invalidPassword, constraintValidatorContext);

        // then
        assertThat(isValid).isFalse();
        verify(constraintValidatorContext).buildConstraintViolationWithTemplate(expectedErrorMessage);
        verify(constraintValidatorContext).disableDefaultConstraintViolation();
    }
}
