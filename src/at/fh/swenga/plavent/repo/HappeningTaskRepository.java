package at.fh.swenga.plavent.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.HappeningTask;
import at.fh.swenga.plavent.model.User;

@Repository
@Transactional
public interface HappeningTaskRepository extends JpaRepository<HappeningTask, Integer> {

	public HappeningTask findByTaskIdAndHappeningHappeningId(int taskID, int happeningID);

	@Query("Select t From HappeningTask t WHERE t.happening.happeningId  = :happeningId and t.responsibleUser = null")
	public List<HappeningTask> getUnassignedTasks(@Param("happeningId") int happeningId);

	public List<HappeningTask> findByHappeningHappeningId(int happeningId);

	public Page<HappeningTask> findByHappeningHappeningId(int happeningId, Pageable page);

	@Query("Select t From HappeningTask t WHERE t.happening.happeningId  = :happeningId AND  LOWER(t.topic) LIKE LOWER(CONCAT('%',:searchstring,'%'))")
	public Page<HappeningTask> getFilteredTasks(@Param("happeningId") int happeningId,
			@Param("searchstring") String searchstring, Pageable page);

	public List<HappeningTask> findByResponsibleUserUsernameAndHappeningHappeningId(String username, int happeningId);

	@Query("Select t FROM HappeningTask t WHERE t.happening.happeningId = :happeningId AND t.responsibleUser IS NULL")
	public List<HappeningTask> getAllUnassignedTasks(@Param("happeningId") int happeningId);

	@Query("Select t FROM HappeningTask t WHERE t.happening.happeningId = :happeningId AND t.responsibleUser = :user")
	public List<HappeningTask> getAllAssignedTasks(@Param("happeningId") int happeningId, @Param("user") User user);

	@Transactional(readOnly = true)
	@Query(value = "SELECT COUNT(*) as assignedTasks " + "FROM HappeningTask t "
			+ "INNER JOIN Happening h  ON(h.happeningID = t.happeningID) "
			+ "INNER JOIN Status s ON( s.statusID = h.statusId) " + "WHERE s.statusName = 'ACTIVE' "
			+ "  AND t.responsibleUser = :username", nativeQuery = true)
	public int getNumOfAssignedTasksForUser(@Param("username") String username);

	@Transactional(readOnly = true)
	@Query(value = "SELECT t.* " + "FROM HappeningTask t " + "WHERE t.responsibleUser = :username", nativeQuery = true)
	public List<HappeningTask> getAllAssignedTasksForUser(@Param("username") String username);
}
