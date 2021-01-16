package mk.ukim.finki.manurepoapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.manurepoapi.model.view.PopularRecord;
import mk.ukim.finki.manurepoapi.model.view.RecentRecord;
import mk.ukim.finki.manurepoapi.model.view.RecordsPerCollection;
import mk.ukim.finki.manurepoapi.model.view.RecordsPerDepartment;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecordStatistics {

    private Long totalRecords;

    private List<RecordsPerCollection> recordsPerCollection;

    private List<RecordsPerDepartment> recordsPerDepartment;

    private List<RecentRecord> recentRecords;

    private List<PopularRecord> popularRecords;

}
