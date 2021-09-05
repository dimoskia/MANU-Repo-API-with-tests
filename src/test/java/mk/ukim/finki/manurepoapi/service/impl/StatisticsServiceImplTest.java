package mk.ukim.finki.manurepoapi.service.impl;

import mk.ukim.finki.manurepoapi.dto.response.RecordStatistics;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.model.view.PopularRecord;
import mk.ukim.finki.manurepoapi.model.view.RecentRecord;
import mk.ukim.finki.manurepoapi.model.view.RecordsPerCollection;
import mk.ukim.finki.manurepoapi.model.view.RecordsPerDepartment;
import mk.ukim.finki.manurepoapi.repository.views.PopularRecordsViewRepository;
import mk.ukim.finki.manurepoapi.repository.views.RecentRecordsViewRepository;
import mk.ukim.finki.manurepoapi.repository.views.RecordsPerCollectionRepository;
import mk.ukim.finki.manurepoapi.repository.views.RecordsPerDepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    RecentRecordsViewRepository recentRecordsViewRepository;

    @Mock
    PopularRecordsViewRepository popularRecordsViewRepository;

    @Mock
    RecordsPerCollectionRepository recordsPerCollectionRepository;

    @Mock
    RecordsPerDepartmentRepository recordsPerDepartmentRepository;

    @InjectMocks
    StatisticsServiceImpl statisticsService;

    @Test
    void getRecordStatistics_givenMaterializedViews_shouldFetchDataAndConstructRecordStatisticsObject() {
        // given
        RecordsPerCollection articleRecords = RecordsPerCollection.builder()
                .collection(Collection.ARTICLE)
                .recordCount(10L)
                .build();
        RecordsPerCollection bookRecords = RecordsPerCollection.builder()
                .collection(Collection.BOOK)
                .recordCount(5L)
                .build();
        List<RecordsPerCollection> recordsPerCollection = List.of(articleRecords, bookRecords);
        when(recordsPerCollectionRepository.findAll()).thenReturn(recordsPerCollection);

        RecordsPerDepartment artRecords = RecordsPerDepartment.builder()
                .department(Department.A)
                .recordCount(15L)
                .build();
        List<RecordsPerDepartment> recordsPerDepartment = List.of(artRecords);
        when(recordsPerDepartmentRepository.findAll()).thenReturn(recordsPerDepartment);

        List<RecentRecord> recentRecords = List.of(RecentRecord.builder().title("Recent Record").build());
        when(recentRecordsViewRepository.findAll(Sort.by(Sort.Direction.DESC, "dateArchived"))).thenReturn(recentRecords);

        List<PopularRecord> popularRecords = List.of(PopularRecord.builder().title("Popular Record").build());
        when(popularRecordsViewRepository.findAll(Sort.by(Sort.Direction.DESC, "downloadsCount"))).thenReturn(popularRecords);

        RecordStatistics expectedRecordStatistics = RecordStatistics.builder()
                .totalRecords(15L)
                .recordsPerCollection(recordsPerCollection)
                .recordsPerDepartment(recordsPerDepartment)
                .recentRecords(recentRecords)
                .popularRecords(popularRecords)
                .build();
        // when
        RecordStatistics actualRecordStatistics = statisticsService.getRecordStatistics();

        // then
        assertThat(actualRecordStatistics).isEqualTo(expectedRecordStatistics);
    }

    @Test
    void refreshAllStats_givenMaterializedViews_shouldRefreshViews() {
        // given, when
        statisticsService.refreshAllStats();

        // then
        verify(recentRecordsViewRepository).refreshMaterializedView();
        verify(popularRecordsViewRepository).refreshMaterializedView();
        verify(recordsPerCollectionRepository).refreshMaterializedView();
        verify(recordsPerDepartmentRepository).refreshMaterializedView();
    }

    @Test
    void refreshRecentRecordStats_givenMaterializedView_shouldRefreshOnlyRecentRecordsViews() {
        // given, when
        statisticsService.refreshRecentRecordStats();

        // then
        verify(recentRecordsViewRepository).refreshMaterializedView();
        verifyNoMoreInteractions(popularRecordsViewRepository);
        verifyNoMoreInteractions(recordsPerCollectionRepository);
        verifyNoMoreInteractions(recordsPerDepartmentRepository);
    }

    @Test
    void refreshPopularRecordStats_givenMaterializedView_shouldRefreshOnlyPopularRecordsViews() {
        // given, when
        statisticsService.refreshPopularRecordStats();

        // then
        verify(popularRecordsViewRepository).refreshMaterializedView();
        verifyNoMoreInteractions(recentRecordsViewRepository);
        verifyNoMoreInteractions(recordsPerCollectionRepository);
        verifyNoMoreInteractions(recordsPerDepartmentRepository);
    }

    @Test
    void isRecentRecord_givenRecordId_shouldCheckRecentRecordsView() {
        // given
        Long recordId = 1L;
        when(recentRecordsViewRepository.existsById(recordId)).thenReturn(true);

        // when
        Boolean isRecordRecent = statisticsService.isRecentRecord(recordId);

        // then
        assertThat(isRecordRecent).isTrue();
    }

    @Test
    void isPopularRecord_givenRecordId_shouldCheckPopularRecordsView() {
        // given
        Long recordId = 1L;
        when(popularRecordsViewRepository.existsById(recordId)).thenReturn(true);

        // when
        Boolean isRecordPopular = statisticsService.isPopularRecord(recordId);

        // then
        assertThat(isRecordPopular).isTrue();
    }
}
