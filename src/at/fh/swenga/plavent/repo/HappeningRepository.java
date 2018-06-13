package at.fh.swenga.plavent.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.Happening;

@Repository
@Transactional(isolation = Isolation.READ_COMMITTED)
public interface HappeningRepository extends JpaRepository<Happening, Integer> {

	@Transactional(readOnly=true)
	public List<Happening> findByHappeningId(int happeningID);

	@Transactional(readOnly=true)
	public Page<Happening> findByHappeningName(String happeningName, Pageable pageable);

	// No pageination variante (e.g. for InitialSetupController)
	@Transactional(readOnly=true)
	public List<Happening> findByHappeningName(String happeningName);

	@Transactional(readOnly=true)
	public Page<Happening> findByHappeningNameAndHappeningHostUsername(String happeningName, String username,
			Pageable pageable);

	@Transactional(readOnly=true)
	public List<Happening> findByHappeningHostUsername(String username);

	@Transactional(readOnly=true)
	@Query(value = "SELECT h " +
					"FROM Happening h " + 
					"WHERE h.happeningHost.username = :host " + 
					"  AND LOWER(h.happeningStatus.statusName) = LOWER('ACTIVE')", nativeQuery = false)
	public Page<Happening> getActiveHappeningsForHost(@Param("host") String hostUsername, Pageable pageable);

	@Transactional(readOnly=true)
	@Query(value = "SELECT h.* " + 
				   "FROM Happening h " + 
				   "WHERE h.happeningID = (SELECT happeningID "  +
				   "					    FROM HappeningTask " + 
				   "						WHERE taskId = :taskId)", nativeQuery = true)
	public Happening getHappeningForTask(@Param("taskId") int taskId);

	@Transactional(readOnly=true)
	@Query(value = "SELECT h.* " +
					"FROM Happening h " +
					"INNER JOIN Status s ON(s.statusID = h.statusId) " +
					"LEFT JOIN Guestlist gl ON(gl.happeningId = h.happeningId) " +
					"WHERE h.start > CURRENT_TIMESTAMP() " +
					"  AND s.statusName = 'ACTIVE' " +
					"  And ( gl.username = :username OR h.username = :username) " +
					"ORDER BY h.start ",
					nativeQuery = true)
	public List<Happening> getHappeningForGuestInFuture(@Param("username") String username);
	
	@Transactional(readOnly=true)
	@Query(value="SELECT h.* FROM Happening h " +
				"INNER JOIN Status s ON s.statusID = h.statusId " +
				"WHERE h.happeningId NOT IN (SELECT gl.happeningId FROM Guestlist gl) " +
				"AND h.start > CURRENT_TIMESTAMP() " +
				"AND s.statusName = 'ACTIVE' " +
				"AND h.username != :username " +
				"ORDER BY h.start",
				nativeQuery = true)
	public List<Happening> getHappeningInFutureWhereGuestNotInvited(@Param("username") String username);
}
