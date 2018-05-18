package at.fh.swenga.plavent.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.Happening;

@Repository
@Transactional
public interface HappeningRepository extends JpaRepository<Happening, Integer> {


	public List<Happening> findByHappeningId(int happeningID);
	
	public List<Happening> findByHappeningName(String happeningName);
	
	public List<Happening> findByHappeningHostUsername(String username);

}
