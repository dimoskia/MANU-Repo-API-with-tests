package mk.ukim.finki.manurepoapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.PublicationStatus;
import mk.ukim.finki.manurepoapi.validator.PublicationStatusDate;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PublicationStatusDate
public class RecordRequest {

    @NotEmpty(message = "Title must not be empty")
    @Length(max = 256)
    private String title;

    @Builder.Default
    private List<Long> authorIds = new ArrayList<>();

    @Builder.Default
    private Collection collection = Collection.OTHER;

    @NotNull(message = "Department must not be empty")
    private Department department;

    @NotEmpty(message = "Subject must not be empty")
    @Length(max = 64)
    private String subject;

    @NotEmpty(message = "A description or abstract must be provided")
    private String descriptionOrAbstract;

    @Length(max = 128)
    private String keywords;

    @Length(max = 64)
    private String language;

    private Integer numPages;

    private LocalDate publicationDate;

    private PublicationStatus publicationStatus;

    @Builder.Default
    private Boolean privateRecord = false;

}
