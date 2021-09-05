package mk.ukim.finki.manurepoapi.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.manurepoapi.enums.Department;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
@Immutable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordsPerDepartment {

    @Id
    @Enumerated
    @Column(columnDefinition = "int2")
    private Department department;

    private Long recordCount;

    public String getDepartment() {
        return department.getFullDepartment();
    }

}
