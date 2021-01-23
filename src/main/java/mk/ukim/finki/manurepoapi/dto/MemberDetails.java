package mk.ukim.finki.manurepoapi.dto;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.AcademicDegree;
import mk.ukim.finki.manurepoapi.enums.AcademicRank;

@Getter
@Setter
public class MemberDetails {

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

}
