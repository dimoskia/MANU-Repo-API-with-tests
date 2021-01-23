package mk.ukim.finki.manurepoapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.IOException;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contentType;

    private byte[] data;

    public ProfileImage(MultipartFile image) throws IOException {
        this.contentType = image.getContentType();
        this.data = image.getBytes();
    }

}
