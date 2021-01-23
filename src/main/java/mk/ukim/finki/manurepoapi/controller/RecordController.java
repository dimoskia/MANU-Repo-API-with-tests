package mk.ukim.finki.manurepoapi.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.*;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.service.RecordService;
import mk.ukim.finki.manurepoapi.service.StatisticsService;
import mk.ukim.finki.manurepoapi.util.DtoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;
    private final StatisticsService statisticsService;

    @GetMapping
    public Page<RecordCard> getRecordsPage(
            RecordsFilter filter,
            @PageableDefault(sort = "dateArchived", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Record> recordsPage = recordService.getRecordsPage(filter, pageable);
        return recordsPage.map(DtoMapper::mapRecordToCard);
    }

    @GetMapping("/{recordId}")
    public RecordDetails getRecordDetails(@PathVariable Long recordId) {
        Record record = recordService.getPublicRecord(recordId);
        return DtoMapper.mapRecordToDetails(record);
    }

    @GetMapping("/statistics")
    public RecordStatistics getRecordStatistics() {
        return statisticsService.getRecordStatistics();
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long recordId) {
        recordService.deleteRecord(recordId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<RecordRequest> createRecord(@RequestBody @Valid RecordRequest recordRequest) {
        return new ResponseEntity<>(recordRequest, HttpStatus.OK);
    }
}
