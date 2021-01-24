package mk.ukim.finki.manurepoapi.model;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(indexes = {@Index(columnList = "email", unique = true)})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean enabled;

    private String firstName;

    private String lastName;

    @Enumerated
    private AcademicDegree academicDegree;

    @Enumerated
    private AcademicRank academicRank;

    @Enumerated
    private MemberType memberType;

    @Enumerated
    private Department department;

    private String shortBio;

    private String phoneNumber;

    private String workplace;

    @ManyToMany(mappedBy = "authorAccounts")
    private Set<Record> records;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ProfileImage profileImage;

    public Account() {
        this.records = new HashSet<>();
    }

}
