package at.fh.swenga.plavent.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.HappeningStatus;

@Repository
@Transactional
public interface HappeningStatusRepository extends JpaRepository<HappeningStatus, Integer> {

	
	public HappeningStatus findFirstByStatusName(String name);
	
	public HappeningStatus findFirstByStatusID(int statusID);
	
	@Query(value="SELECT COUNT(*) FROM Status", nativeQuery = true)
	public int getAmountOfHappeningStatus();
}
