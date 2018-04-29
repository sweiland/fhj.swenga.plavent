package at.fh.swenga.plavent.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.HappeningStatus;
import at.fh.swenga.plavent.model.UserRole;

@Repository
@Transactional
public class HappeningStatusDao {

	@PersistenceContext
	protected EntityManager entityManager;

	public List<HappeningStatus> getHappeingStatus() {

		TypedQuery<HappeningStatus> typedQuery = entityManager.createQuery("select s from HappeningStatus s", HappeningStatus.class);
		List<HappeningStatus> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}

	public HappeningStatus getHappeningStatus(String name) {
		try {

			TypedQuery<HappeningStatus> typedQuery = entityManager
					.createQuery("select s from HappeningStatus s where s.statusName = :statusName", HappeningStatus.class);
			typedQuery.setParameter("statusName", name);
			
			HappeningStatus status = typedQuery.getSingleResult();
			return status;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public HappeningStatus getHappeningStatus(int statusID) {
		try {

			TypedQuery<HappeningStatus> typedQuery = entityManager
					.createQuery("select s from HappeningStatus r where r.statusId = :statusID", HappeningStatus.class);
			typedQuery.setParameter("statusID", statusID);
			
			HappeningStatus status = typedQuery.getSingleResult();
			return status;
		} catch (NoResultException e) {
			return null;
		}
	}

}
