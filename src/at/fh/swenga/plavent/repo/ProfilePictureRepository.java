package at.fh.swenga.plavent.repo;
 
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
 
import at.fh.swenga.plavent.model.ProfilePicture;
 
@Repository
@Transactional
public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Integer> {
}