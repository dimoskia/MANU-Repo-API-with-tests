package mk.ukim.finki.manurepoapi.util;

import mk.ukim.finki.manurepoapi.dto.FileResponse;
import mk.ukim.finki.manurepoapi.dto.MemberCard;
import mk.ukim.finki.manurepoapi.dto.RecordCard;
import mk.ukim.finki.manurepoapi.dto.RecordDetails;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.File;
import mk.ukim.finki.manurepoapi.model.Record;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Set;
import java.util.stream.Collectors;

public class DtoMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static RecordCard mapRecordToCard(Record record) {
        RecordCard recordCard = modelMapper.map(record, RecordCard.class);
        recordCard.setDateArchived(record.getDateArchived().toLocalDate());
        return recordCard;
    }

    public static RecordDetails mapRecordToDetails(Record record) {
        RecordDetails recordDetails = new RecordDetails();
        BeanUtils.copyProperties(record, recordDetails, "dateArchived", "files");
        recordDetails.setDateArchived(record.getDateArchived().toLocalDate());
        Set<FileResponse> files = record.getFiles().stream()
                .map(DtoMapper::mapFileToResponse)
                .collect(Collectors.toSet());
        Set<MemberCard> memberAuthors = record.getAuthorAccounts().stream()
                .map(DtoMapper::mapAccountToMemberCard)
                .collect(Collectors.toSet());
        recordDetails.setFiles(files);
        recordDetails.setAuthorsDetails(memberAuthors);
        return recordDetails;
    }

    public static FileResponse mapFileToResponse(File file) {
        FileResponse fileResponse = new FileResponse();
        BeanUtils.copyProperties(file, fileResponse);
        String downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment("files")
                .path(file.getId().toString())
                .toUriString();
        fileResponse.setFileDownloadUri(downloadUri);
        return fileResponse;
    }

    public static MemberCard mapAccountToMemberCard(Account account) {
        return modelMapper.map(account, MemberCard.class);
    }

}
