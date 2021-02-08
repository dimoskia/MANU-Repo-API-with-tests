package mk.ukim.finki.manurepoapi.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.request.AdminRecordsFilter;
import mk.ukim.finki.manurepoapi.dto.response.ManageRecordCard;
import mk.ukim.finki.manurepoapi.dto.response.RecordCard;
import mk.ukim.finki.manurepoapi.dto.response.RecordDetails;
import mk.ukim.finki.manurepoapi.model.File;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.service.AdminService;
import mk.ukim.finki.manurepoapi.service.FileService;
import mk.ukim.finki.manurepoapi.util.DtoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final FileService fileService;

    @GetMapping("/records")
    public Page<RecordCard> getNewRecordsPage(
            AdminRecordsFilter recordsFilter,
            @PageableDefault(sort = "dateArchived", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Record> newRecordsPage = adminService.getNewRecordsPage(recordsFilter, pageable);
        return newRecordsPage.map(DtoMapper::mapRecordToCard);
    }

    @GetMapping("/records/{recordId}")
    public ResponseEntity<RecordDetails> getRecordDetails(@PathVariable Long recordId) {
        Record record = adminService.getRecordDetails(recordId);
        return new ResponseEntity<>(DtoMapper.mapRecordToAdminDetails(record), HttpStatus.OK);
    }

    @PatchMapping("/records/{recordId}")
    public ResponseEntity<ManageRecordCard> approveRecord(@PathVariable Long recordId) {
        Record record = adminService.approveRecord(recordId);
        return new ResponseEntity<>(DtoMapper.mapRecordToManageCard(record), HttpStatus.OK);
    }

    @DeleteMapping("/records/{recordId}")
    public ResponseEntity<?> declineRecord(@PathVariable Long recordId) {
        adminService.declineRecord(recordId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/files/{fileId}")
    public void downloadFile(@PathVariable Long fileId, HttpServletResponse response) throws IOException {
        File file = fileService.getFile(fileId);
        response.setContentType(file.getContentType());
        response.setContentLengthLong(file.getSize());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");
        FileCopyUtils.copy(file.getFileData().getData(), response.getOutputStream());
        response.getOutputStream().flush();
    }

}
