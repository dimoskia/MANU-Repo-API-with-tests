package mk.ukim.finki.manurepoapi.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Department {

    LLS("Department of Linguistics and Literary Science"),
    SS("Department of Social Sciences"),
    MS("Department of Medical Sciences"),
    TS("Department of Technical Sciences"),
    MBS("Department of Natural, Mathematical and Biotechnological Sciences"),
    A("Department of Arts");

    private final String fullDepartment;

}
