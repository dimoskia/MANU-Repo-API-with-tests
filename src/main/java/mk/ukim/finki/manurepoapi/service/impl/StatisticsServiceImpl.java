package mk.ukim.finki.manurepoapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.response.RecordStatistics;
import mk.ukim.finki.manurepoapi.model.view.PopularRecord;
import mk.ukim.finki.manurepoapi.model.view.RecentRecord;
import mk.ukim.finki.manurepoapi.model.view.RecordsPerCollection;
import mk.ukim.finki.manurepoapi.model.view.RecordsPerDepartment;
import mk.ukim.finki.manurepoapi.repository.views.PopularRecordsViewRepository;
import mk.ukim.finki.manurepoapi.repository.views.RecentRecordsViewRepository;
import mk.ukim.finki.manurepoapi.repository.views.RecordsPerCollectionRepository;
import mk.ukim.finki.manurepoapi.repository.views.RecordsPerDepartmentRepository;
import mk.ukim.finki.manurepoapi.service.StatisticsService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final RecentRecordsViewRepository recentRecordsViewRepository;
    private final PopularRecordsViewRepository popularRecordsViewRepository;
    private final RecordsPerCollectionRepository recordsPerCollectionRepository;
    private final RecordsPerDepartmentRepository recordsPerDepartmentRepository;

    @Override
    public RecordStatistics getRecordStatistics() {
        List<RecordsPerCollection> perCollections = recordsPerCollectionRepository.findAll();
        List<RecordsPerDepartment> perDepartments = recordsPerDepartmentRepository.findAll();
        long totalCount = perCollections.stream()
                .mapToLong(RecordsPerCollection::getRecordCount)
                .sum();
        return new RecordStatistics(totalCount, perCollections, perDepartments, getMostRecentRecords(), getMostPopularRecords());
    }

    @Override
    public void refreshAllStats() {
        recentRecordsViewRepository.refreshMaterializedView();
        popularRecordsViewRepository.refreshMaterializedView();
        recordsPerCollectionRepository.refreshMaterializedView();
        recordsPerDepartmentRepository.refreshMaterializedView();
    }

    private List<RecentRecord> getMostRecentRecords() {
        Sort sortCriteria = Sort.by(Sort.Direction.DESC, "dateArchived");
        return recentRecordsViewRepository.findAll(sortCriteria);
    }

    private List<PopularRecord> getMostPopularRecords() {
        Sort sortCriteria = Sort.by(Sort.Direction.DESC, "downloadsCount");
        return popularRecordsViewRepository.findAll(sortCriteria);
    }

}
