package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.File;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Query("select f from File f where f.id = :fileId")
    @EntityGraph(value = "File.fileData")
    Optional<File> fetchFileWithData(@Param(value = "fileId") Long fileId);

}
