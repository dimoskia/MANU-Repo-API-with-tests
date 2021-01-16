package mk.ukim.finki.manurepoapi.dto;

import lombok.Getter;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;

import java.util.List;

@Getter
@Setter
public class RecordsFilter {

    private String titleOrKeyword;

    private List<Collection> collections;

    private Department department;

    private String subject;

    private Integer year;

}
