package mk.ukim.finki.manurepoapi.dto.request;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.Collection;

@Getter
@Setter
public class ManageRecordsFilter {

    private String title;

    private Collection collection;

    private Integer year;

    private Boolean privateRecord;

    private Boolean approved;

}
