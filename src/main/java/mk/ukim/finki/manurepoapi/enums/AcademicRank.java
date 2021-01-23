package mk.ukim.finki.manurepoapi.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AcademicRank {

    STUDENT("Student"),
    PROFESSOR("Professor"),
    ASSOCIATE_PROFESSOR("Associate professor"),
    ELECTED_ASSOCIATE_PROFESSOR("Elected associate professor"),
    ASSISTANT_PROFESSOR("Assistant professor"),
    ELECTED_ASSISTANT_PROFESSOR("Elected assistant professor"),
    ASSISTANT("Assistant"),
    ASSISTANT_DOCTORAND("Assistant doctorand"),
    ASSOCIATE("Associate"),
    DEMONSTRATOR("Demonstrator"),
    ACADEMIC("Academic");

    private final String fullRank;

}
