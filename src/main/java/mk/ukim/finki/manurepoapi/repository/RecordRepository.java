package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
}
