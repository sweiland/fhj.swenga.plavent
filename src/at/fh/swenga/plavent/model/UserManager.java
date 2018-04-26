package at.fh.swenga.plavent.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;

@Repository
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class UserManager {
	
	private List<User> users = new ArrayList<User>();
	
	public UserManager() {
		// TODO Auto-generated constructor stub
		
		//Add test dummy user to have access to web page
		addUser(new User("admin","admin","admin","admin"));
		addUser(new User("guest","guest","guest","guest"));
		addUser(new User("manager","manager","manager","manager"));
	}
	
	public List<User> getAllUsers() {
		return users;
	}
	
	public void addUser(User newUser) {
		users.add(newUser);
	}
	
	public boolean removeUser(String username) {
		//TODO: Handle that via DB
		return users.remove(new User(username,null,null,null));
	}
	
	
	/**
	 * Return UserModel if given data is valid otherwhise null
	 * @param username
	 * @param passwordHash
	 * @return
	 */
	public User verifyLogin(String username, String passwordHash) {
		User model = this.getUser(username);
		
		if (model != null && model.getPasswordHash().equals(passwordHash)) {
			return model;
		} 
		
		return null;
	}
	
	/**
	 * Returns user object with given username or null when not found
	 * @param username Username to identify user object
	 * @return null or userobject with given name
	 */
	public User getUser(String username) {
		for(User user: users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}

	public List<User> getFilteredUsers(String searchString) {
		List<User> filteredList = new ArrayList<User>();

		for (User model : users) {
			if ((model.getUsername() != null && model.getUsername().contains(searchString))
					|| (model.getFirstname() != null && model.getFirstname().contains(searchString)) 
					|| (model.getLastname() != null && model.getLastname().contains(searchString)) ) {
				filteredList.add(model);
			}
		}
		return filteredList;
	}
}
