package mk.ukim.finki.manurepoapi.repository.views;

import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.model.view.RecordsPerDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RecordsPerDepartmentRepository extends JpaRepository<RecordsPerDepartment, Department> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "REFRESH MATERIALIZED VIEW CONCURRENTLY records_per_department", nativeQuery = true)
    void refreshMaterializedView();

}
