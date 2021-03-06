package mk.ukim.finki.manurepoapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mk.ukim.finki.manurepoapi.enums.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(indexes = {@Index(columnList = "email", unique = true)})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
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
    @Column(columnDefinition = "int2")
    private AcademicDegree academicDegree;

    @Enumerated
    @Column(columnDefinition = "int2")
    private AcademicRank academicRank;

    @Enumerated
    @Column(columnDefinition = "int2")
    private MemberType memberType;

    @Enumerated
    @Column(columnDefinition = "int2")
    private Department department;

    private String shortBio;

    private String phoneNumber;

    private String workplace;

    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "authorAccounts")
    private Set<Record> records = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ProfileImage profileImage;

}
