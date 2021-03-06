package mk.ukim.finki.manurepoapi.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(name = "File.fileData",
        attributeNodes = @NamedAttributeNode("fileData")
)
public class File {

    @Id
    private Long id;

    private String fileName;

    private Long size;

    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Record record;

    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "id")
    private FileData fileData;

}
