package mk.ukim.finki.manurepoapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {@Index(columnList = "token", unique = true)})
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime expiration;

    @OneToOne(fetch = FetchType.LAZY)
    private Account account;

    public VerificationToken(Account account, int durationInHours) {
        this.token = UUID.randomUUID().toString();
        this.account = account;
        this.expiration = LocalDateTime.now().plusHours(durationInHours);
    }

}
