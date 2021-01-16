package mk.ukim.finki.manurepoapi.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    private Long id;

    private String fileName;

    private Long size;

    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Record record;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "id")
    private FileData fileData;

}
