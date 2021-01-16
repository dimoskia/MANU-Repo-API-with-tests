package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByIdAndRecordId(Long fileId, Long recordId);

}