package mk.ukim.finki.manurepoapi.dto;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.AcademicDegree;
import mk.ukim.finki.manurepoapi.enums.AcademicRank;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.MemberType;

@Getter
@Setter
public class MemberDetails {

    private Long id;

    private String firstName;

    private String lastName;

    private Department department;

    private MemberType memberType;

    private String imageUrl;

    private String email;

    private AcademicDegree academicDegree;

    private AcademicRank academicRank;

    private String shortBio;

    private String phoneNumber;

    private String workplace;

    public String getAcademicDegree() {
        return academicDegree.getFullDegree();
    }

    public String getAcademicRank() {
        return academicRank.getFullRank();
    }

    public String getMemberType() {
        return memberType.getFullType();
    }

    public String getDepartment() {
        return department.getFullDepartment();
    }

}
