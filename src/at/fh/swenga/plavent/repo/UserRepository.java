package at.fh.swenga.plavent.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {

	
	public Optional<User> findByUsername(String username);
	
	public User findFirstByUsername(String username);

	@Query("Select u from User u")
	public List<User> findAllBy();
	
	public List<User> findByUsernameContainingIgnoreCase(String username);
	
	int countByUsernameContainingIgnoreCase(String username);
	
	//@Query("Select u from User u where u.enabled == false")
	public List<User> findByEnabledFalse();
	
	public List<User> findByEnabledTrue();
	
	@Query(value = "SELECT u.* " + 
				   	"FROM User u " +
					"WHERE u.username != (SELECT h.username " + 
					"					 FROM Happening h " + 
					"                     WHERE h.happeningId = :happeningID) " + 
					"  AND u.username NOT IN (SELECT g.username " +
					"						 FROM Guestlist g " +
					"                         WHERE g.happeningId = :happeningID)", nativeQuery = true)
	public List<User> getPotentialGuestsForHappening(@Param("happeningID") int happeningId);
	
	@Query("Select u From User u where LOWER(u.username) LIKE LOWER(CONCAT('%',:username ,'%'))" )
	public List<User> getFilteredUsers(@Param("username") String username);

	
	@Query( value = "SELECT u.*" + 
			"FROM User u " +
			"JOin User_UserRole uur ON(uur.username = u.username)" + 
			"JOIN UserRole ur ON(ur.roleID = uur.roleId) " + 
			"WHERE LOWER(ur.roleName) LIKE LOWER(CONCAT('%',:rolename ,'%'))", nativeQuery = true)
	public List<User> getUsersByRolename(@Param("rolename") String rolename);
	
	@Query("Delete from User u where u.username = :username")
	public void deleteByUserName(@Param ("username") String username);
	
	
	public User findFirstByToken(String token);
}
