package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Query("select f from File f where f.record.id = ?1 and f.id = ?2")
    Optional<File> findFile(Long recordId, Long fileId);

}