package at.fh.swenga.plavent.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Entity
@Table(name = "User")
public class User {
	
	@Id
	@Column(name = "username") // Take care with changes: 1:1 Relation(Host) & N:M Relation(Guests) to
	// Happening!
	private String username;

	@Column(name = "password", nullable = false, length = 512)
	private String password;

	@Column(nullable = false, length = 64)
	private String firstname;
	@Column(nullable = false, length = 64)
	private String lastname;

	@Column(nullable = true, length = 256)
	private String eMail;
	@Column(nullable = true, length = 15)
	private String telNumber;
	
	/*
	 * cascade = CascadeType.PERSIST: Changes the default setting for this
	 * relationship, so the EntityManager saves unmanaged related object
	 * automatically
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "User_UserRole",
		joinColumns = { @JoinColumn(name = "username") },
		inverseJoinColumns = { @JoinColumn(name = "roleId") })
	@Fetch(value = FetchMode.SUBSELECT)
	private List<UserRole> roleList;

	// TODO: add all required attributes

	@ManyToMany(mappedBy = "guestList", fetch = FetchType.EAGER)
	private List<Happening> happenings;
	
	@Version
	long version;
	
	public User() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param username
	 * @param password
	 * @param firstname
	 * @param lastname
	 * @param eMail
	 * @param telNumber
	 */
	public User(String username, String password, String firstname, String lastname, String eMail,
			String telNumber, List<UserRole> roles, List<Happening> happenings) {
		super();
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.eMail = eMail;
		this.telNumber = telNumber;
		this.roleList = roles;
		this.happenings = happenings;
	}
	
	public User(String username, String password, String firstname, String lastname, String eMail,
			String telNumber, List<UserRole> roles) {
		super();
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.eMail = eMail;
		this.telNumber = telNumber;
		this.roleList = roles;
	}
	
	public User(String username, String password, String firstname, String lastname, List<UserRole> roles) {
		super();
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.roleList = roles;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return the eMail
	 */
	public String geteMail() {
		return eMail;
	}

	/**
	 * @param eMail the eMail to set
	 */
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	/**
	 * @return the telNumber
	 */
	public String getTelNumber() {
		return telNumber;
	}

	/**
	 * @param telNumber the telNumber to set
	 */
	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	/**
	 * @return the roleList
	 */
	public List<UserRole> getRoleList() {
		return roleList;
	}

	/**
	 * @param roleList the roleList to set
	 */
	public void setRoleList(List<UserRole> roleList) {
		this.roleList = roleList;
	}
	
	public void addUserRole(UserRole role) {
		roleList.add(role);
	}
	
	public boolean removeUserRole(UserRole role) {
		return roleList.remove(role);
	}

	/**
	 * @return the happenings
	 */
	public List<Happening> getHappenings() {
		return happenings;
	}

	/**
	 * @param happenings the happenings to set
	 */
	public void setHappenings(List<Happening> happenings) {
		this.happenings = happenings;
	}
	
	public void addHappening(Happening h) {
		happenings.add(h);
	}
	
	public boolean removeHappening(Happening h) {
		return happenings.remove(h);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserNew [username=" + username + ", password=" + password + ", firstname=" + firstname + ", lastname="
				+ lastname + ", eMail=" + eMail + ", telNumber=" + telNumber + "]";
	}
	
	
	
	

}
