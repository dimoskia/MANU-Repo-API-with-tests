package mk.ukim.finki.manurepoapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.PublicationStatus;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(name = "Record.authorAccounts",
        attributeNodes = @NamedAttributeNode("authorAccounts")
)
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String authors;

    @Enumerated
    @Column(columnDefinition = "int2")
    private Collection collection;

    @Enumerated
    @Column(columnDefinition = "int2")
    private Department department;

    private String subject;

    private String descriptionOrAbstract;

    private String keywords;

    private String language;

    private Integer numPages;

    private LocalDate publicationDate;

    @Enumerated
    @Column(columnDefinition = "int2")
    private PublicationStatus publicationStatus;

    @Builder.Default
    private Integer downloadsCount = 0;

    @CreationTimestamp
    private LocalDateTime dateArchived;

    @Builder.Default
    private Boolean approved = false;

    private Boolean privateRecord;

    @Builder.Default
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "record", cascade = CascadeType.REMOVE)
    private Set<File> files = new HashSet<>();

    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(name = "record_account",
            joinColumns = {@JoinColumn(name = "record_id")},
            inverseJoinColumns = {@JoinColumn(name = "account_id")})
    private Set<Account> authorAccounts = new HashSet<>();

}
