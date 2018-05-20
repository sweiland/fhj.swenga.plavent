package at.fh.swenga.plavent.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.HappeningTask;
import at.fh.swenga.plavent.model.User;

@Repository
@Transactional
public interface HappeningGuestlistRepository extends JpaRepository<User, Integer> {
		
	@Query(value = "SELECT u.* " + 
		   	"FROM User u " +
			"WHERE u.username != (SELECT h.username " + 
			"					 FROM Happening h " + 
			"                     WHERE h.happeningId = :happeningID) " + 
			"  AND u.username NOT IN (SELECT g.username " +
			"						 FROM Guestlist g " +
			"                         WHERE g.happeningId = :happeningID)", nativeQuery = true)
	public List<User> getPotentialGuestsForHappening(@Param("happeningID") int happeningId);
	
	
	@Query(value = "SELECT u.* " + 
		   			"FROM Guestlist g " + 
		   			"INNER JOIN User u ON(u.username = g.username) " + 
		   			"WHERE g.happeningId = :happeningID", nativeQuery = true)	
	public List<User> getGuestList(@Param("happeningID") int happeningId);
}
