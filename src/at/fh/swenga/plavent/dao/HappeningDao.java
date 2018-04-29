package at.fh.swenga.plavent.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.Happening;

@Repository
@Transactional
public class HappeningDao {


	@PersistenceContext
	protected EntityManager entityManager;

	public List<Happening> getHappenings() {

		TypedQuery<Happening> typedQuery = entityManager.createQuery("select h from Happening h", Happening.class);
		List<Happening> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}

	public Happening getHappening(int happeningId) {
		try {

			TypedQuery<Happening> typedQuery = entityManager
					.createQuery("select h from Happening h where h.happeningId = :happeningId", Happening.class);
			typedQuery.setParameter("happeningId", happeningId);
			
			Happening happening = typedQuery.getSingleResult();
			return happening;
		} catch (NoResultException e) {
			return null;
		}
	}
}
