package at.fh.swenga.plavent.repo;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.HappeningCategory;

@Repository
@Transactional
public interface HappeningCategoryRepository extends JpaRepository<HappeningCategory, Integer> {
	
	
	public HappeningCategory findFirstByCategoryName(String name);
	
	public HappeningCategory findFirstByCategoryID(int categoryID);
	
	public List<HappeningCategory> findByEnabledTrue();
	
	public List<HappeningCategory> findAll();

	@Query(value = "SELECT DISTINCT c.* " +
					"FROM HappeningCategory c " +
					"INNER JOIN Happening h ON(h.categoryId = c.categoryID)", nativeQuery = true)
	public List<HappeningCategory> getUsedCategories();
	
	
	public Page<HappeningCategory> findByCategoryName(String name,Pageable pageable); 
}
