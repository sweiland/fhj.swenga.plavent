package at.fh.swenga.plavent.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.Happening;

@Repository
@Transactional
public interface HappeningRepository extends JpaRepository<Happening, Integer> {


	public List<Happening> findByHappeningId(int happeningID);
	
	public List<Happening> findByHappeningName(String happeningName);
	
	public List<Happening> findByHappeningHostUsername(String username);

	
	@Query(value = "SELECT h " + 
		   			"FROM Happening h " +
		   			"WHERE h.happeningHost.username = :host " +
		   			"  AND LOWER(h.happeningStatus.statusName) = LOWER('ACTIVE')", nativeQuery = false)
	public List<Happening> getActiveHappeningsForHost(@Param("host") String hostUsername);
}
