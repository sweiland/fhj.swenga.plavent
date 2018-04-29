package at.fh.swenga.plavent.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "User")
public class User implements Serializable {
	
	@Id
	@Column(name="username") //Take care with changes: 1:1 Relation(Host) & N:M Relation(Guests) to Happening!
	private String username;
	
	@Column(name="password", nullable = false, length = 512)
	private String passwordHash;
	
	@Column(nullable = false, length = 64)
	private String firstname;
	@Column(nullable = false, length = 64)
	private String lastname;
	
	@Column(nullable = true, length = 256)
	private String eMail;
	@Column(nullable = true, length = 64)
	private String telNumber;
	
	/*cascade = CascadeType.PERSIST: Changes the default setting for this relationship, so the EntityManager saves unmanaged related object automatically*/
	@ManyToOne (cascade = CascadeType.PERSIST)
	private UserRole role;
	
	//TODO: add all required attributes
	
	
	@ManyToMany(mappedBy = "guestList",fetch=FetchType.EAGER)
	private List<Happening> happenings;
	
	@Version
	long version;
	
	public User() {
		// TODO Auto-generated constructor stub
	}
	
	public User(String username, String passwordHash, String firstname, String lastname, UserRole role) {
		super();
		this.username = username;
		this.passwordHash = passwordHash;
		this.firstname = firstname;
		this.lastname = lastname;
		this.role = role;
	}
	
	public User(String username, String passwordHash, String firstname, String lastname, String eMail, String telNumber,
			UserRole role) {
		super();
		this.username = username;
		this.passwordHash = passwordHash;
		this.firstname = firstname;
		this.lastname = lastname;
		this.eMail = eMail;
		this.telNumber = telNumber;
		this.role = role;
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
	
	public List<Happening> getHappenings() {
		return happenings;
	}

	public void setHappenings(List<Happening> happenings) {
		this.happenings = happenings;
	}
	
	public void addHappening(Happening happening)
	{
		happenings.add(happening);
	}
	
	public void removeHapping(Happening happeing)
	{
		happenings.remove(happeing);
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
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
