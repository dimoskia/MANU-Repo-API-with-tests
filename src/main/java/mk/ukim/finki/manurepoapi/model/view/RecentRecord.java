package mk.ukim.finki.manurepoapi.model.view;

import lombok.Data;
import mk.ukim.finki.manurepoapi.enums.Collection;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Immutable
@Data
public class RecentRecord {

    @Id
    private Long id;

    private String title;

    @Enumerated
    private Collection collection;

    private LocalDate dateArchived;

    private String authors;

}
