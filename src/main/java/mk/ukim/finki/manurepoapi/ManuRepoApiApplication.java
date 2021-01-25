package mk.ukim.finki.manurepoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ManuRepoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManuRepoApiApplication.class, args);
    }

}
