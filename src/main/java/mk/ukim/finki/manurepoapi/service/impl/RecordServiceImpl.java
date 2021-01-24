package mk.ukim.finki.manurepoapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.request.ManageRecordsFilter;
import mk.ukim.finki.manurepoapi.dto.request.RecordRequest;
import mk.ukim.finki.manurepoapi.dto.response.RecordsFilter;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.repository.RecordRepository;
import mk.ukim.finki.manurepoapi.repository.specification.RecordSpecification;
import mk.ukim.finki.manurepoapi.service.AccountService;
import mk.ukim.finki.manurepoapi.service.RecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;
    private final AccountService accountService;

    @Override
    public Page<Record> getRecordsPage(RecordsFilter filter, Pageable pageable) {
        Specification<Record> recordSpecification = RecordSpecification.browseRecordsSpec(filter);
        return recordRepository.findAll(recordSpecification, pageable);
    }

    @Override
    public Record getPublicRecord(Long recordId) {
        return recordRepository.findByIdAndApprovedTrueAndPrivateRecordFalse(recordId)
                .orElseThrow(() -> new EntityNotFoundException(Record.class, recordId));
    }

    @Override
    public Record getRecord(Long recordId) {
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(Record.class, recordId));
    }

    @Override
    public void deleteRecord(Long recordId) {
        try {
            recordRepository.deleteById(recordId);
        } catch (EmptyResultDataAccessException exception) {
            throw new EntityNotFoundException(Record.class, recordId);
        }
    }

    @Override
    public Record createRecord(RecordRequest recordRequest) {
        Record record = new Record();
        BeanUtils.copyProperties(recordRequest, record);
        if (recordRequest.getAuthorIds() != null && recordRequest.getAuthorIds().size() > 0) {
            List<Account> authorAccounts = accountService.getMultipleAccounts(recordRequest.getAuthorIds());
            record.setAuthorAccounts(new HashSet<>(authorAccounts));
            String authors = authorAccounts.stream()
                    .map(account -> String.format("%s %s", account.getFirstName(), account.getLastName()))
                    .collect(Collectors.joining(", "));
            record.setAuthors(authors);
        }
        return recordRepository.save(record);
    }

    @Override
    public Page<Record> getManageRecordsPage(ManageRecordsFilter filter, Pageable pageable, Long accountId) {
        Specification<Record> recordSpecification = RecordSpecification.manageRecordsSpec(filter, accountId);
        return recordRepository.findAll(recordSpecification, pageable);
    }

    @Override
    public boolean isRecordPublic(Long recordId) {
        return recordRepository.existsByIdAndApprovedTrueAndPrivateRecordFalse(recordId);
    }

}
