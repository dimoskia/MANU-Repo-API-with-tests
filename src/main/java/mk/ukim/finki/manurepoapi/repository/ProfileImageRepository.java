package mk.ukim.finki.manurepoapi.repository;

import mk.ukim.finki.manurepoapi.model.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
}
