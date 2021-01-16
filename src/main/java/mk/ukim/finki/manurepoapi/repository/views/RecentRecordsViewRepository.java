package mk.ukim.finki.manurepoapi.repository.views;

import mk.ukim.finki.manurepoapi.model.view.RecentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RecentRecordsViewRepository extends JpaRepository<RecentRecord, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "REFRESH MATERIALIZED VIEW CONCURRENTLY recent_record", nativeQuery = true)
    void refreshMaterializedView();
}
