package mk.ukim.finki.manurepoapi.validator;

import mk.ukim.finki.manurepoapi.dto.request.PasswordCredentials;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordCredentials> {

    @Override
    public boolean isValid(PasswordCredentials passwordCredentials, ConstraintValidatorContext constraintValidatorContext) {
        String password = passwordCredentials.getPassword();
        if (StringUtils.hasText(password)) {
            return password.equals(passwordCredentials.getConfirmPassword());
        }
        return true;
    }

}
