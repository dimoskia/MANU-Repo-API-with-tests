package mk.ukim.finki.manurepoapi.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberType {

    FULL("Full Member"),
    CORRESPONDING("Corresponding Member"),
    FOREIGN("Foreign Member"),
    HONORARY("Honorary Member");

    private final String fullType;

}
