package mk.ukim.finki.manurepoapi.repository.views;


import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.model.view.RecordsPerCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RecordsPerCollectionRepository extends JpaRepository<RecordsPerCollection, Collection> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "REFRESH MATERIALIZED VIEW CONCURRENTLY records_per_collection", nativeQuery = true)
    void refreshMaterializedView();
}
