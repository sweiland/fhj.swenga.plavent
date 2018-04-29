package at.fh.swenga.plavent.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "UserRole")
public class UserRole implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int roleID;
	
	@Column(nullable = false, unique=true, length = 64)
	private String roleName;

	@Column(nullable = true, length = 512)
	private String description;
	
	@Version
	long version;
	
	public UserRole() {
		// TODO Auto-generated constructor stub
	}

	public UserRole(String roleName, String description) {
		super();
		this.roleName = roleName;
		this.description = description;
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

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "UserRole [roleID=" + roleID + ", roleName=" + roleName + ", description=" + description + ", version="
				+ version + "]";
	}
	
	

}
