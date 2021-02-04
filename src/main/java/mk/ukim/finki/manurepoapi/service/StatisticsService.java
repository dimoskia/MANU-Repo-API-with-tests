package mk.ukim.finki.manurepoapi.service;

import mk.ukim.finki.manurepoapi.dto.response.RecordStatistics;

public interface StatisticsService {

    RecordStatistics getRecordStatistics();

    void refreshAllStats();

    void refreshRecentRecordStats();

    void refreshPopularRecordStats();

    Boolean isRecentRecord(Long recordId);

    Boolean isPopularRecord(Long recordId);

}
