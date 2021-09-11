package mk.ukim.finki.manurepoapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.manurepoapi.enums.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRecordsFilter {

    private String title;

    private String author;

    private Collection collection;

}
