package mk.ukim.finki.manurepoapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.manurepoapi.enums.Department;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
