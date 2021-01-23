package mk.ukim.finki.manurepoapi.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AcademicDegree {

    NONE("No academic degree"),

    AA("Associate of Arts"),
    AS("Associate of Science"),
    AAS("Associate of Applied Science"),

    BA("Bachelor of Arts"),
    BS("Bachelor of Science"),
    BFA("Bachelor of Fine Arts"),
    BAS("Bachelor of Applied Science"),

    MA("Master of Arts"),
    MS("Master of Science"),
    MBA("Master of Business Administration"),
    MFA("Master of Fine Arts"),

    PHD("Doctor of Philosophy"),
    JD("Juris Doctor"),
    MD("Doctor of Medicine"),
    DDS("Doctor of Dental Surgery");

    private final String fullDegree;

}
