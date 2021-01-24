package mk.ukim.finki.manurepoapi.model;

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
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
    private Collection collection;

    @Enumerated
    private Department department;

    private String subject;

    private String descriptionOrAbstract;

    private String keywords;

    private String language;

    private Integer numPages;

    private LocalDate publicationDate;

    @Enumerated
    private PublicationStatus publicationStatus;

    private Integer downloadsCount;

    @CreationTimestamp
    private LocalDateTime dateArchived;

    private Boolean approved;

    private Boolean privateRecord;

    @OneToMany(mappedBy = "record", cascade = CascadeType.REMOVE)
    private Set<File> files;

    @ManyToMany
    @JoinTable(name = "record_account",
            joinColumns = {@JoinColumn(name = "record_id")},
            inverseJoinColumns = {@JoinColumn(name = "account_id")})
    private Set<Account> authorAccounts;

    public Record() {
        this.downloadsCount = 0;
        this.approved = false;
        this.files = new HashSet<>();
        this.authorAccounts = new HashSet<>();
    }

}
