package at.fh.swenga.plavent.model;
 
import java.util.Date;
 
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
 
@Entity
@Table(name = "ProfilePicture")
public class ProfilePicture implements java.io.Serializable {
 
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
 
	@Column(name = "name")
	private String name;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "created")
	private Date created;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
	private User assignedUser;
 
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "pic")
	private byte[] pic;

 
	/**
	 * 
	 */
	public ProfilePicture() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param created
	 * @param pic
	 */
	public ProfilePicture(int id, String name, String type, Date created, byte[] pic, User user) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.created = created;
		this.pic = pic;
		this.assignedUser = user;
	}

	@Version
	long version;


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the pic
	 */
	public byte[] getPic() {
		return pic;
	}

	/**
	 * @param pic the pic to set
	 */
	public void setPic(byte[] pic) {
		this.pic = pic;
	}

	public User getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(User assignedUser) {
		this.assignedUser = assignedUser;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignedUser == null) ? 0 : assignedUser.hashCode());
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
		ProfilePicture other = (ProfilePicture) obj;
		if (assignedUser == null) {
			if (other.assignedUser != null)
				return false;
		} else if (!assignedUser.equals(other.assignedUser))
			return false;
		return true;
	}

	
	
}