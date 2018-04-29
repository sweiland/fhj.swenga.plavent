package at.fh.swenga.plavent.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.HappeningTask;

@Repository
@Transactional
public class HappeningTaskDao {
	@PersistenceContext
	protected EntityManager entityManager;

	public List<HappeningTask> getHappeningTasks() {

		TypedQuery<HappeningTask> typedQuery = entityManager.createQuery("select t from HappeningTask t", HappeningTask.class);
		List<HappeningTask> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}

	public HappeningTask getHappeningTask(int taskId, int happeningId) {
		try {

			TypedQuery<HappeningTask> typedQuery = entityManager
					.createQuery("select t from HappeningTask t where t.taskId = :taskId and t.happeningId = :happeningId", HappeningTask.class);
			typedQuery.setParameter("taskId", taskId);
			typedQuery.setParameter("happeningId", happeningId);

			
			HappeningTask task = typedQuery.getSingleResult();
			return task;
		} catch (NoResultException e) {
			return null;
		}
	}

}
