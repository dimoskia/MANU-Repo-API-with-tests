package mk.ukim.finki.manurepoapi.model.view;

import lombok.Data;
import mk.ukim.finki.manurepoapi.enums.Collection;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
@Immutable
@Data
public class RecordsPerCollection {

    @Id
    @Enumerated
    @Column(columnDefinition = "int2")
    private Collection collection;

    private Long recordCount;

    public String getCollection() {
        return collection.getFullCollection();
    }

}
