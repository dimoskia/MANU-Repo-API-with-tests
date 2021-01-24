package mk.ukim.finki.manurepoapi.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EmailAvailableValidator.class)
public @interface EmailAvailable {

    String message() default "An account for that email already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
