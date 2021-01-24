package mk.ukim.finki.manurepoapi.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponse {

    private String fileName;

    private Long size;

    private String contentType;

    private String fileDownloadUri;

}
