package mk.ukim.finki.manurepoapi.validator;

import mk.ukim.finki.manurepoapi.dto.request.RecordRequest;
import mk.ukim.finki.manurepoapi.enums.PublicationStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class PublicationStatusDateValidator implements ConstraintValidator<PublicationStatusDate, RecordRequest> {

    @Override
    public boolean isValid(RecordRequest recordRequest, ConstraintValidatorContext constraintValidatorContext) {
        PublicationStatus status = recordRequest.getPublicationStatus();
        LocalDate date = recordRequest.getPublicationDate();
        return status == PublicationStatus.PUBLISHED || date == null;
    }

}
