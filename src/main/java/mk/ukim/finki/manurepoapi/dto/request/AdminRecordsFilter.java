package mk.ukim.finki.manurepoapi.dto.request;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.Collection;

@Getter
@Setter
public class AdminRecordsFilter {

    private String title;

    private String author;

    private Collection collection;

}
