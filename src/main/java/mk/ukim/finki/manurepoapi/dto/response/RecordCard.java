package mk.ukim.finki.manurepoapi.dto.response;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecordCard {

    private Long id;

    private String title;

    private String authors;

    private Collection collection;

    private Department department;

    private String subject;

    private String keywords;

    private Integer downloadsCount;

    private LocalDateTime dateArchived;

    public String getDepartment() {
        return department == null ? null : department.getFullDepartment();
    }

    public String getCollectionName() {
        return collection == null ? null : collection.getFullCollection();
    }

}
