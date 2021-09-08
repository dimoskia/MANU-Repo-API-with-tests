package mk.ukim.finki.manurepoapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordsFilter {

    private String titleOrKeyword;

    private List<Collection> collections;

    private Department department;

    private String subject;

    private Integer year;

    private Long authorId;

}
