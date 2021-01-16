package mk.ukim.finki.manurepoapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.RecordsFilter;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.repository.RecordRepository;
import mk.ukim.finki.manurepoapi.repository.specification.RecordSpecification;
import mk.ukim.finki.manurepoapi.service.RecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;

    @Override
    public Page<Record> getRecordsPage(RecordsFilter filter, Pageable pageable) {
        Specification<Record> recordSpecification = RecordSpecification.browseRecordsSpec(filter);
        return recordRepository.findAll(recordSpecification, pageable);
    }

    @Override
    public Record getRecord(Long recordId) {
        return recordRepository.findById(recordId).orElseThrow(RuntimeException::new);
    }

}
