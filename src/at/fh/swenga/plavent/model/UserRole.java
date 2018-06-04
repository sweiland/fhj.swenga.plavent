package at.fh.swenga.plavent.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "UserRole")
public class UserRole implements java.io.Serializable {

	private static final long serialVersionUID = 8098173157518993615L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int roleID;

	@Column(name = "roleName", nullable = false, unique = true, length = 64)
	private String roleName;

	@Column(name = "description", nullable = true, length = 512)
	private String description;

	@ManyToMany(mappedBy = "roleList", fetch = FetchType.EAGER)
	private List<User> userList;

	@Version
	long version;

	public UserRole() {
	}

	public UserRole(String roleName, String description, List<User> userList) {
		super();
		this.roleName = roleName;
		this.description = description;
		this.userList = userList;
	}

	public UserRole(String roleName, String description) {
		super();
		this.roleName = roleName;
		this.description = description;
	}
	public UserRole(String roleName) {
		super();
		this.roleName = roleName;
	}

	public void addUser(User u) {
		userList.add(u);
	}

	public boolean removeUser(User u) {
		return userList.remove(u);
	}

	/**
	 * @return the userList
	 */
	public List<User> getUserList() {
		return userList;
	}

	/**
	 * @param userList
	 *            the userList to set
	 */
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public int getRoleID() {
		return roleID;
	}

	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
		result = prime * result + (int) (version ^ (version >>> 32));
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
		UserRole other = (UserRole) obj;
		if (roleName == null) {
			if (other.roleName != null)
				return false;
		} else if (!roleName.equals(other.roleName))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserRole [roleID=" + roleID + ", roleName=" + roleName + ", description=" + description + "]";
	}
}
