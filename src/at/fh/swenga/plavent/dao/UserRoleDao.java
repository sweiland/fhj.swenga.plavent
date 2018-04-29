package at.fh.swenga.plavent.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.UserRole;

@Repository
@Transactional
public class UserRoleDao {

	@PersistenceContext
	protected EntityManager entityManager;

	public List<UserRole> getUserRoles() {

		TypedQuery<UserRole> typedQuery = entityManager.createQuery("select r from UserRole r", UserRole.class);
		List<UserRole> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}

	public UserRole getUserRole(int roleID) {
		try {

			TypedQuery<UserRole> typedQuery = entityManager
					.createQuery("select r from UserRole r where r.roleID = :roleID", UserRole.class);
			typedQuery.setParameter("roleID", roleID);
			
			UserRole userRole = typedQuery.getSingleResult();
			return userRole;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public UserRole getUserRole(String roleName) {
		try {

			TypedQuery<UserRole> typedQuery = entityManager
					.createQuery("select r from UserRole r where r.roleName = :roleName", UserRole.class);
			typedQuery.setParameter("roleName", roleName);
			
			UserRole userRole = typedQuery.getSingleResult();
			return userRole;
		} catch (NoResultException e) {
			return null;
		}
	}

}
