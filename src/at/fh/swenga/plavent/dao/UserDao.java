package at.fh.swenga.plavent.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.User;

@Repository
@Transactional
public class UserDao {

	@PersistenceContext
	protected EntityManager entityManager;

	public List<User> getUsers() {

		TypedQuery<User> typedQuery = entityManager.createQuery("select u from User u", User.class);
		List<User> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}

	public List<User> getFilteredUsers(String searchString) {
		TypedQuery<User> typedQuery = entityManager.createQuery("select u from User u where u.username like %:username%", User.class);
		List<User> typedResultList = typedQuery.getResultList();
		return typedResultList;
	}

	public User getUser(String username) {
		try {

			TypedQuery<User> typedQuery = entityManager.createQuery("select u from User u where u.username = :username",
					User.class);
			typedQuery.setParameter("username", username);

			User user = typedQuery.getSingleResult();
			return user;
		} catch (NoResultException e) {
			return null;
		}
	}

	public User verifyLogin(String username, String password) {
		try {
			// Convert PW into md5 hash
			MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
			String passwordHash = new String(md5.digest(password.getBytes()));

			TypedQuery<User> typedQuery = entityManager.createQuery(
					"select u from User u where u.username = :username and u.passwordHash = :passwordHash", User.class);
			typedQuery.setParameter("username", username);
			typedQuery.setParameter("passwordHash", passwordHash);

			User user = typedQuery.getSingleResult();
			return user;
		} catch (NoResultException | NoSuchAlgorithmException e) {
			return null;
		}

	}

	public void persist(User user) {
		entityManager.persist(user);
	}

	// Update in DB
	public User merge(User user) {
		return entityManager.merge(user);
	}

	public void delete(User user) {
		entityManager.remove(user);
	}
}
