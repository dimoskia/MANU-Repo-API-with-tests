package mk.ukim.finki.manurepoapi.model.view;

import lombok.Data;
import mk.ukim.finki.manurepoapi.enums.Collection;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Immutable
@Data
public class RecentRecord {

    @Id
    private Long id;

    private String title;

    @Enumerated
    @Column(columnDefinition = "int2")
    private Collection collection;

    private LocalDateTime dateArchived;

    private String authors;

}
