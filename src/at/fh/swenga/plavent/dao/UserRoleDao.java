package at.fh.swenga.plavent.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.UserRole;

@Repository
@Transactional
public interface UserRoleDao extends JpaRepository<UserRole, Integer> {

	public UserRole findFirstByRoleID(int roleId);
	
	public UserRole findFirstByRoleName(String name);
	
	@Query(value = "SELECT ur.* " + 
			"FROM UserRole ur " + 
			"JOIN User_UserRole uur ON(uur.roleId = ur.roleID) " + 
			"WHERE uur.username = :username", nativeQuery=true)
	public List<UserRole> getUserRolesForUser(@Param("username") String username);

}
