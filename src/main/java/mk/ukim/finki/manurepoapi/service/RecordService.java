package mk.ukim.finki.manurepoapi.service;

import mk.ukim.finki.manurepoapi.dto.RecordsFilter;
import mk.ukim.finki.manurepoapi.model.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecordService {

    Page<Record> getRecordsPage(RecordsFilter filter, Pageable pageable);

    Record getRecord(Long recordId);

    boolean isRecordPublic(Long recordId);

}
