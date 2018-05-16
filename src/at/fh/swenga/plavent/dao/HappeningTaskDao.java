package at.fh.swenga.plavent.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.HappeningTask;

@Repository
@Transactional
public interface HappeningTaskDao extends JpaRepository<HappeningTask, Integer> {
		
	public HappeningTask findByTaskIdAndHappeningHappeningId(int taskID, int happeningID);
	

	@Query("Select t From HappeningTask t WHERE t.happening.happeningId  = :happeningId and t.responsibleUser = null" )
	public List<HappeningTask> getUnassignedTasks(@Param("happeningId") int happeningId);
	
	public List<HappeningTask> findByHappeningHappeningId(int happeningId);

}
