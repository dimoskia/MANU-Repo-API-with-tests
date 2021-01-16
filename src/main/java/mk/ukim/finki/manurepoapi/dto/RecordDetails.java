package mk.ukim.finki.manurepoapi.dto;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.PublicationStatus;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class RecordDetails {

    private Long id;

    private String title;

    private String authors;

    private Collection collection;

    private Department department;

    private String subject;

    private String keywords;

    private Integer downloadsCount;

    private LocalDate dateArchived;

    private String descriptionOrAbstract;

    private String language;

    private Integer numPages;

    private LocalDate publicationDate;

    private PublicationStatus publicationStatus;

    private Set<FileResponse> files;

    public String getDepartment() {
        return department.getFullDepartment();
    }

    public String getCollectionName() {
        return collection.getFullCollection();
    }

}
