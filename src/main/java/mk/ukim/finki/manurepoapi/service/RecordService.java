package mk.ukim.finki.manurepoapi.service;

import mk.ukim.finki.manurepoapi.dto.request.ManageRecordsFilter;
import mk.ukim.finki.manurepoapi.dto.request.RecordRequest;
import mk.ukim.finki.manurepoapi.dto.request.RecordsFilter;
import mk.ukim.finki.manurepoapi.model.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface RecordService {

    Page<Record> getRecordsPage(RecordsFilter filter, Pageable pageable);

    Record getPublicRecord(Long recordId);

    boolean isRecordPublic(Long recordId);

    Record getRecord(Long recordId);

    void deleteRecord(Authentication authentication, Long recordId);

    Record createRecord(Authentication authentication, RecordRequest recordRequest);

    Page<Record> getManageRecordsPage(ManageRecordsFilter filter, Pageable pageable, Authentication authentication);

    boolean checkRecordPermissions(Long recordId, Authentication authentication);

    Record getRecordRef(Long recordId);

    void incrementDownloads(Long recordId);

}
