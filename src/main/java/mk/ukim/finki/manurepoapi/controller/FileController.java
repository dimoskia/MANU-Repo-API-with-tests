package mk.ukim.finki.manurepoapi.controller;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.manurepoapi.dto.FileResponse;
import mk.ukim.finki.manurepoapi.model.File;
import mk.ukim.finki.manurepoapi.service.FileService;
import mk.ukim.finki.manurepoapi.util.DtoMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    @PostMapping("/{recordId}")
    public FileResponse addFileToRecord(@RequestParam(name = "file") MultipartFile multipartFile,
                                        @PathVariable Long recordId) throws IOException {
        File file = fileService.saveFileToRecord(multipartFile, recordId);
        return DtoMapper.mapFileToResponse(file);
    }

}
