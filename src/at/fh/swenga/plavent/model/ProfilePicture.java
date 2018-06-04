package at.fh.swenga.plavent.model;
 
import java.util.Date;
 
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
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
 
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "pic")
	private byte[] pic;

 
	/**
	 * 
	 */
	public ProfilePicture() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param created
	 * @param pic
	 */
	public ProfilePicture(int id, String name, String type, Date created, byte[] pic) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.created = created;
		this.pic = pic;
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
 
	
}