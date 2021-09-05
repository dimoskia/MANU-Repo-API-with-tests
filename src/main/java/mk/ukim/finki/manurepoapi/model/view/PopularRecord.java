package mk.ukim.finki.manurepoapi.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.manurepoapi.enums.Collection;
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
public class PopularRecord {

    @Id
    private Long id;

    private String title;

    @Enumerated
    @Column(columnDefinition = "int2")
    private Collection collection;

    private Integer downloadsCount;

    private String authors;

}
