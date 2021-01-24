package mk.ukim.finki.manurepoapi.validator;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.service.AccountService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class EmailAvailableValidator implements ConstraintValidator<EmailAvailable, String> {

    private final AccountService accountService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return accountService.isEmailAvailable(email);
    }

}
