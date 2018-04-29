package at.fh.swenga.plavent.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.HappeningCategory;
import at.fh.swenga.plavent.model.HappeningStatus;

@Repository
@Transactional
public class HappeningCategoryDao {

	@PersistenceContext
	protected EntityManager entityManager;

	public List<HappeningCategory> getHappeningCategories() {

		TypedQuery<HappeningCategory> typedQuery = entityManager.createQuery("select c from HappeningCategory c", HappeningCategory.class);
		List<HappeningCategory> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}

	public HappeningCategory getCategory(String name) {
		try {

			TypedQuery<HappeningCategory> typedQuery = entityManager
					.createQuery("select c from HappeningCategory c where c.categoryName = :categoryName", HappeningCategory.class);
			typedQuery.setParameter("categoryName", name);
			
			HappeningCategory category = typedQuery.getSingleResult();
			return category;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public HappeningCategory getHappeningCategory(int categoryID) {
		try {

			TypedQuery<HappeningCategory> typedQuery = entityManager
					.createQuery("select c from HappeningCategory c where c.categoryID = :categoryID", HappeningCategory.class);
			typedQuery.setParameter("categoryID", categoryID);
			
			HappeningCategory category = typedQuery.getSingleResult();
			return category;
		} catch (NoResultException e) {
			return null;
		}
	}

}
