package mk.ukim.finki.manurepoapi.validator;


import mk.ukim.finki.manurepoapi.dto.AccountRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, AccountRequest> {

    @Override
    public boolean isValid(AccountRequest accountRequest, ConstraintValidatorContext constraintValidatorContext) {
        String password = accountRequest.getPassword();
        if (password != null && !password.isEmpty()) {
            return password.equals(accountRequest.getConfirmPassword());
        }
        return true;
    }

}
