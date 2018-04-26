package at.fh.swenga.plavent.model;

import java.security.MessageDigest;

public class User {
	
	private String username;
	private String passwordHash;
	private String firstname;
	private String lastname;
	//TODO: add all required attributes
		
	public User() {
		// TODO Auto-generated constructor stub
	}
	
	public User(String username, String passwordHash, String firstname, String lastname) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public void updateModel(User newUserModel) {
		setFirstname(newUserModel.getFirstname());
		setLastname(newUserModel.getLastname());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
