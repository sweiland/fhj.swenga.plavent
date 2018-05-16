package at.fh.swenga.plavent.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.HappeningStatus;

@Repository
@Transactional
public interface HappeningStatusDao extends JpaRepository<HappeningStatus, Integer> {

	
	public HappeningStatus findFirstByStatusName(String name);
	
	public HappeningStatus findFirstByStatusID(int statusID);
}
