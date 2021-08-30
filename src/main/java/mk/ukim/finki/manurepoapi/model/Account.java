package mk.ukim.finki.manurepoapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(indexes = {@Index(columnList = "email", unique = true)})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;

    @Builder.Default
    private Boolean enabled = false;

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

    @Builder.Default
    @ManyToMany(mappedBy = "authorAccounts")
    private Set<Record> records = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ProfileImage profileImage;

}
