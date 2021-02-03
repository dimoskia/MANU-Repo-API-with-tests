package mk.ukim.finki.manurepoapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.request.ManageRecordsFilter;
import mk.ukim.finki.manurepoapi.dto.request.RecordRequest;
import mk.ukim.finki.manurepoapi.dto.request.RecordsFilter;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.repository.RecordRepository;
import mk.ukim.finki.manurepoapi.repository.specification.RecordSpecification;
import mk.ukim.finki.manurepoapi.security.model.UserPrincipal;
import mk.ukim.finki.manurepoapi.service.AccountService;
import mk.ukim.finki.manurepoapi.service.RecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
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
    public void deleteRecord(Authentication authentication, Long recordId) {
        Account accountRef = accountService.getAccountRef(authentication);
        Record recordToDelete = recordRepository.findByIdAndAuthorAccountsContaining(recordId, accountRef)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        recordRepository.delete(recordToDelete);
    }

    @Override
    public Record createRecord(Authentication authentication, RecordRequest recordRequest) {
        Record record = new Record();
        BeanUtils.copyProperties(recordRequest, record);

        Long accountId = ((UserPrincipal) authentication.getPrincipal()).getAccountId();
        recordRequest.getAuthorIds().add(accountId);
        List<Account> authorAccounts = accountService.getMultipleAccounts(recordRequest.getAuthorIds());
        record.setAuthorAccounts(new HashSet<>(authorAccounts));
        String authors = authorAccounts.stream()
                .map(account -> String.format("%s %s", account.getFirstName(), account.getLastName()))
                .collect(Collectors.joining(", "));
        record.setAuthors(authors);

        return recordRepository.save(record);
    }

    @Override
    public Page<Record> getManageRecordsPage(ManageRecordsFilter filter, Pageable pageable, Authentication authentication) {
        Long accountId = ((UserPrincipal) authentication.getPrincipal()).getAccountId();
        Specification<Record> recordSpecification = RecordSpecification.manageRecordsSpec(filter, accountId);
        return recordRepository.findAll(recordSpecification, pageable);
    }

    @Override
    public boolean checkRecordPermissions(Long recordId, Authentication authentication) {
        Account accountRef = accountService.getAccountRef(authentication);
        return recordRepository.existsByIdAndAuthorAccountsContaining(recordId, accountRef);
    }

    @Override
    public Record getRecordRef(Long recordId) {
        return recordRepository.getOne(recordId);
    }

    @Override
    @Transactional
    public void incrementDownloads(Long recordId) {
        recordRepository.incrementDownloads(recordId);
    }

    @Override
    public boolean isRecordPublic(Long recordId) {
        return recordRepository.existsByIdAndApprovedTrueAndPrivateRecordFalse(recordId);
    }

}
