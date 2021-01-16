package mk.ukim.finki.manurepoapi.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.RecordCard;
import mk.ukim.finki.manurepoapi.dto.RecordDetails;
import mk.ukim.finki.manurepoapi.dto.RecordsFilter;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.service.RecordService;
import mk.ukim.finki.manurepoapi.util.DtoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public Page<RecordCard> getRecordsPage(
            RecordsFilter filter,
            @PageableDefault(sort = "dateArchived", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Record> recordsPage = recordService.getRecordsPage(filter, pageable);
        return recordsPage.map(DtoMapper::mapRecordToCard);
    }

    @GetMapping("/{recordId}")
    public RecordDetails getRecordDetails(@PathVariable Long recordId) {
        Record record = recordService.getRecord(recordId);
        return DtoMapper.mapRecordToDetails(record);
    }

}
