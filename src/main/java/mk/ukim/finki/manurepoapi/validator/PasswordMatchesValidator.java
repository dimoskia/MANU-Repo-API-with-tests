package mk.ukim.finki.manurepoapi.validator;

import mk.ukim.finki.manurepoapi.dto.request.PasswordCredentials;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordCredentials> {

    @Override
    public boolean isValid(PasswordCredentials passwordCredentials, ConstraintValidatorContext constraintValidatorContext) {
        String password = passwordCredentials.getPassword();
        if (password != null && !password.isEmpty()) {
            return password.equals(passwordCredentials.getConfirmPassword());
        }
        return true;
    }

}
