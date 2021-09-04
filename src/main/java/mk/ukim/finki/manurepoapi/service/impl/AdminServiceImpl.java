package mk.ukim.finki.manurepoapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.request.AdminRecordsFilter;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.repository.RecordRepository;
import mk.ukim.finki.manurepoapi.repository.specification.RecordSpecification;
import mk.ukim.finki.manurepoapi.service.AdminService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final RecordRepository recordRepository;

    @Override
    public Page<Record> getNewRecordsPage(AdminRecordsFilter recordsFilter, Pageable pageable) {
        Specification<Record> recordSpecification = RecordSpecification.adminRecordsSpec(recordsFilter);
        return recordRepository.findAll(recordSpecification, pageable);
    }

    @Override
    public Record getRecordDetails(Long recordId) {
        return recordRepository.fetchRecordWithAuthors(recordId)
                .orElseThrow(() -> new EntityNotFoundException(Record.class, recordId));
    }

    @Override
    public Record approveRecord(Long recordId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(Record.class, recordId));
        record.setApproved(true);
        return recordRepository.save(record);
    }

    @Override
    public void declineRecord(Long recordId) {
        try {
            recordRepository.deleteById(recordId);
        } catch (EmptyResultDataAccessException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

}
