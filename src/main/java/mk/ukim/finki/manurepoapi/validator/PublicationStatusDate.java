package mk.ukim.finki.manurepoapi.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PublicationStatusDateValidator.class)
public @interface PublicationStatusDate {

    String message() default "Publication date only applicable for PUBLISHED publication status";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
