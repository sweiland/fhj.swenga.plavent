package at.fh.swenga.plavent.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.HappeningCategory;

@Repository
@Transactional
public interface HappeningCategoryDao extends JpaRepository<HappeningCategory, Integer> {
	
	
	public HappeningCategory findFirstByCategoryName(String name);
	
	public HappeningCategory findFirstByCategoryID(int categoryID);

}
