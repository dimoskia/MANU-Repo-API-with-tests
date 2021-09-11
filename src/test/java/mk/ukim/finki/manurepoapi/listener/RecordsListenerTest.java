package mk.ukim.finki.manurepoapi.listener;

import mk.ukim.finki.manurepoapi.event.OnRecordDeletedEvent;
import mk.ukim.finki.manurepoapi.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordsListenerTest {

    @Mock
    StatisticsService statisticsService;

    @InjectMocks
    RecordsListener recordsListener;

    private final Long recordId = 10L;

    private OnRecordDeletedEvent onRecordDeletedEvent;

    @BeforeEach
    void setUp() {
        onRecordDeletedEvent = new OnRecordDeletedEvent(recordId);
    }

    @Test
    void refreshAffectedRecordStats_recordIsPopularAndRecent_popularAndRecentStatsAreRefreshed() {
        // given
        when(statisticsService.isRecentRecord(recordId)).thenReturn(true);
        when(statisticsService.isPopularRecord(recordId)).thenReturn(true);

        // when
        recordsListener.refreshAffectedRecordStats(onRecordDeletedEvent);

        // then
        verify(statisticsService).refreshRecentRecordStats();
        verify(statisticsService).refreshPopularRecordStats();
    }

    @Test
    void refreshAffectedRecordStats_recordIsNotPopularNorRecent_popularAndRecentStatsAreRefreshed() {
        // given
        when(statisticsService.isRecentRecord(recordId)).thenReturn(false);
        when(statisticsService.isPopularRecord(recordId)).thenReturn(false);

        // when
        recordsListener.refreshAffectedRecordStats(onRecordDeletedEvent);

        // then
        verify(statisticsService, times(0)).refreshRecentRecordStats();
        verify(statisticsService, times(0)).refreshPopularRecordStats();
    }
}
