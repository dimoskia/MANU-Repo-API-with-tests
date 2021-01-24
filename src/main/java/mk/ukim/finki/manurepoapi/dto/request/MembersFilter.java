package mk.ukim.finki.manurepoapi.dto.request;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.Department;

@Getter
@Setter
public class MembersFilter {

    private String searchTerm;

    private Department department;

    private String firstLetter;

    public void setFirstLetter(String firstLetter) {
        if (firstLetter != null) {
            firstLetter = firstLetter.substring(0, 1);
        }
        this.firstLetter = firstLetter;
    }

}
