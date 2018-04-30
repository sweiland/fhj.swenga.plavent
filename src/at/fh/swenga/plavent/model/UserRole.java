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
	
	@Column(nullable = false)
	private boolean permissionUserMgmt;
	
	@Column(nullable = false)
	private boolean permissionHappeningMgmt;
	
	@Column(nullable = false)
	private boolean permissionCategoryMgmt;
	
	
	@Version
	long version;
	
	public UserRole() {
		// TODO Auto-generated constructor stub
	}

	public UserRole(String roleName, String description, boolean permissionUserMgmt, boolean permissionHappeningMgmt,
			boolean permissionCategoryMgmt) {
		super();
		this.roleName = roleName;
		this.description = description;
		this.permissionUserMgmt = permissionUserMgmt;
		this.permissionHappeningMgmt = permissionHappeningMgmt;
		this.permissionCategoryMgmt = permissionCategoryMgmt;
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

	public boolean isPermissionUserMgmt() {
		return permissionUserMgmt;
	}

	public void setPermissionUserMgmt(boolean permissionUserMgmt) {
		this.permissionUserMgmt = permissionUserMgmt;
	}

	public boolean isPermissionHappeningMgmt() {
		return permissionHappeningMgmt;
	}

	public void setPermissionHappeningMgmt(boolean permissionHappeningMgmt) {
		this.permissionHappeningMgmt = permissionHappeningMgmt;
	}

	public boolean isPermissionCategoryMgmt() {
		return permissionCategoryMgmt;
	}

	public void setPermissionCategoryMgmt(boolean permissionCategoryMgmt) {
		this.permissionCategoryMgmt = permissionCategoryMgmt;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + roleID;
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
		UserRole other = (UserRole) obj;
		if (roleID != other.roleID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserRole [roleID=" + roleID + ", roleName=" + roleName + ", description=" + description
				+ ", permissionUserMgmt=" + permissionUserMgmt + ", permissionHappeningMgmt=" + permissionHappeningMgmt
				+ ", permissionCategoryMgmt=" + permissionCategoryMgmt + "]";
	}
}
