package mk.ukim.finki.manurepoapi.dto.response;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.PublicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class RecordDetails {

    private Long id;

    private String title;

    private Collection collection;

    private Department department;

    private String subject;

    private String keywords;

    private Integer downloadsCount;

    private LocalDateTime dateArchived;

    private String descriptionOrAbstract;

    private String language;

    private Integer numPages;

    private LocalDate publicationDate;

    private PublicationStatus publicationStatus;

    private Set<FileResponse> files;

    private Set<MemberCard> authors;

    public String getDepartment() {
        return department == null ? null : department.getFullDepartment();
    }

    public String getCollectionName() {
        return collection == null ? null : collection.getFullCollection();
    }

}
