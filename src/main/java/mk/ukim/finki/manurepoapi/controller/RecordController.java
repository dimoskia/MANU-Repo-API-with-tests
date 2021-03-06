package mk.ukim.finki.manurepoapi.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.request.ManageRecordsFilter;
import mk.ukim.finki.manurepoapi.dto.request.RecordRequest;
import mk.ukim.finki.manurepoapi.dto.request.RecordsFilter;
import mk.ukim.finki.manurepoapi.dto.response.*;
import mk.ukim.finki.manurepoapi.event.OnRecordDeletedEvent;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.service.RecordService;
import mk.ukim.finki.manurepoapi.service.StatisticsService;
import mk.ukim.finki.manurepoapi.util.DtoMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;
    private final StatisticsService statisticsService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping("/browse")
    public Page<RecordCard> getRecordsPage(
            RecordsFilter filter,
            @PageableDefault(sort = "dateArchived", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Record> recordsPage = recordService.getRecordsPage(filter, pageable);
        return recordsPage.map(DtoMapper::mapRecordToCard);
    }

    @GetMapping("/browse/{recordId}")
    public RecordDetails getRecordDetails(@PathVariable Long recordId) {
        Record record = recordService.getPublicRecord(recordId);
        return DtoMapper.mapRecordToDetails(record);
    }

    @GetMapping("/browse/statistics")
    public RecordStatistics getRecordStatistics() {
        return statisticsService.getRecordStatistics();
    }

    @GetMapping("/manage")
    public Page<ManageRecordCard> getRecordsPageForAccount(
            Authentication authentication,
            ManageRecordsFilter recordsFilter,
            @PageableDefault(sort = "dateArchived", direction = Sort.Direction.DESC, value = 12) Pageable pageable) {
        Page<Record> recordsPage = recordService.getManageRecordsPage(recordsFilter, pageable, authentication);
        return recordsPage.map(DtoMapper::mapRecordToManageCard);
    }

    @PostMapping("/manage")
    public ResponseEntity<RecordDetails> createRecord(Authentication authentication,
                                                      @RequestBody @Valid RecordRequest recordRequest) {
        Record record = recordService.createRecord(authentication, recordRequest);
        RecordDetails recordDetails = DtoMapper.mapRecordToDetails(record);
        String locationUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment("records")
                .path(record.getId().toString())
                .toUriString();
        return ResponseEntity.created(URI.create(locationUri)).body(recordDetails);
    }

    @DeleteMapping("/manage/{recordId}")
    public ResponseEntity<?> deleteRecord(Authentication authentication, @PathVariable Long recordId) {
        recordService.deleteRecord(authentication, recordId);
        eventPublisher.publishEvent(new OnRecordDeletedEvent(recordId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/manage/{recordId}")
    public ResponseEntity<RecordDetails> editRecord(Authentication authentication,
                                                    @PathVariable Long recordId,
                                                    @RequestBody @Valid RecordRequest recordRequest) {
        Record record = recordService.editRecord(authentication, recordId, recordRequest);
        return new ResponseEntity<>(DtoMapper.mapRecordToDetails(record), HttpStatus.OK);
    }

}
