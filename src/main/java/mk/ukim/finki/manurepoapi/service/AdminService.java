package mk.ukim.finki.manurepoapi.service;

import mk.ukim.finki.manurepoapi.dto.request.AdminRecordsFilter;
import mk.ukim.finki.manurepoapi.model.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {

    Page<Record> getNewRecordsPage(AdminRecordsFilter recordsFilter, Pageable pageable);

    Record getRecordDetails(Long recordId);

    Record approveRecord(Long recordId);

    void declineRecord(Long recordId);

}
