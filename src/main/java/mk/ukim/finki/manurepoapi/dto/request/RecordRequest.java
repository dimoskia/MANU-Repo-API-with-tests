package mk.ukim.finki.manurepoapi.dto.request;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.PublicationStatus;
import mk.ukim.finki.manurepoapi.validator.PublicationStatusDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@PublicationStatusDate
public class RecordRequest {

    @NotEmpty(message = "Title must not be empty")
    private String title;

    private List<Long> authorIds;

    private Collection collection = Collection.OTHER;

    @NotNull(message = "Department must not be empty")
    private Department department;

    @NotEmpty(message = "Subject must not be empty")
    private String subject;

    @NotEmpty(message = "A description or abstract must be provided")
    private String descriptionOrAbstract;

    private String keywords;

    private String language;

    private Integer numPages;

    private LocalDate publicationDate;

    private PublicationStatus publicationStatus;

    private Boolean privateRecord = false;

}
