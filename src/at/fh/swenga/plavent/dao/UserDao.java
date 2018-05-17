package at.fh.swenga.plavent.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.User;

@Repository
@Transactional
public interface UserDao extends JpaRepository<User, Integer> {
	
	@Query("Select u From User u where LOWER(u.username) LIKE LOWER(CONCAT('%',:username ,'%'))" )
	public List<User> getFilteredUsers(@Param("username") String username);

	public User findFirstByUsername(String username);
	
	@Query("select u from User u where u.username = :username and u.passwordHash = :passwordHash")
	public User verifyLogin(@Param("username") String username, @Param("passwordHash") String password);
	
	
	@Query(value = "SELECT u.* " + 
			"FROM User u " + 
			"WHERE u.username != (SELECT h.username FROM Happening h WHERE h.happeningId = :happeningID) " + 
			"  AND u.username NOT IN (SELECT hu.guestList_username " +
			"                         FROM Happening_User hu " + 
			"                         WHERE hu.happenings_happeningId = :happeningID)", nativeQuery = true)
	public List<User> getPotentialGuestsForHappening(@Param("happeningID") int happeningId);
}
