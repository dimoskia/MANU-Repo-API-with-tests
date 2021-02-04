package mk.ukim.finki.manurepoapi.listener;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.event.OnRecordDeletedEvent;
import mk.ukim.finki.manurepoapi.service.StatisticsService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecordsListener {

    private final StatisticsService statisticsService;

    @EventListener
    public void refreshAffectedRecordStats(OnRecordDeletedEvent event) {
        Long deletedRecordId = (Long) event.getSource();
        if (statisticsService.isRecentRecord(deletedRecordId)) {
            statisticsService.refreshRecentRecordStats();
        }
        if (statisticsService.isPopularRecord(deletedRecordId)) {
            statisticsService.refreshPopularRecordStats();
        }
    }

}
