	package at.fh.swenga.plavent.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "Happening")
public class Happening implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int happeningId;

	@Column(nullable = false, length = 64)
	private String happeningName;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date start;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date end;

	@Column(nullable = true, length = 512)
	private String description;

	@Column(nullable = false, length = 128)
	private String location;

	/*
	 * cascade = CascadeType.PERSIST: Changes the default setting for this
	 * relationship, so the EntityManager saves unmanaged related object
	 * automatically
	 */
	@ManyToOne(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
	private HappeningCategory category;

	/*
	 * cascade = CascadeType.PERSIST: Changes the default setting for this
	 * relationship, so the EntityManager saves unmanaged related object
	 * automatically
	 */
	@ManyToOne(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
	private HappeningStatus happeningStatus;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "username")
	private User happeningHost;

	/*
	 * cascade = CascadeType.PERSIST: Changes the default setting for this
	 * relationship, so the EntityManager saves unmanaged related object
	 * automatically
	 */
	@ManyToMany(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
	private List<User> guestList;
	
	
	/*
	 * https://stackoverflow.com/questions/24675340/org-hibernate-loader-multiplebagfetchexception-cannot-simultaneously-fetch-mult/24676806
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "happening")
	@Fetch(value = FetchMode.SUBSELECT)
	private List<HappeningTask> taskList;

	@Version
	long version;

	public Happening() {
		// TODO Auto-generated constructor stub
	}

	public Happening(String happeningName, Date start, Date end, String description, String location,
			HappeningCategory category, HappeningStatus happeningStatus, User happeningHost, List<User> guestList, List<HappeningTask> tasks) {
		super();
		this.happeningName = happeningName;
		this.start = start;
		this.end = end;
		this.description = description;
		this.location = location;
		this.category = category;
		this.happeningStatus = happeningStatus;
		this.happeningHost = happeningHost;
		this.guestList = guestList;
		this.taskList = tasks;
	}

	public int getHappeningId() {
		return happeningId;
	}

	public void setHappeningId(int happeningId) {
		this.happeningId = happeningId;
	}

	public String getHappeningName() {
		return happeningName;
	}

	public void setHappeningName(String happeningName) {
		this.happeningName = happeningName;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + happeningId;
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
		Happening other = (Happening) obj;
		if (happeningId != other.happeningId)
			return false;
		return true;
	}

	public HappeningCategory getCategory() {
		return category;
	}

	public void setCategory(HappeningCategory category) {
		this.category = category;
	}

	public HappeningStatus getHappeningStatus() {
		return happeningStatus;
	}

	public void setHappeningStatus(HappeningStatus happeningStatus) {
		this.happeningStatus = happeningStatus;
	}

	public User getHappeningHost() {
		return happeningHost;
	}

	public void setHappeningHost(User happeningHost) {
		this.happeningHost = happeningHost;
	}

	public List<User> getGuestList() {
		return guestList;
	}

	public void setGuestList(List<User> guestList) {
		this.guestList = guestList;
	}

	public void addGuestToList(User user) {
		guestList.add(user);
	}

	public boolean removeFromList(String username) {
		return guestList.remove(new User(username, null, null, null, null));
	}
	
	public boolean removeFromList(User user) {
		return guestList.remove(user);
	}
	
	public List<HappeningTask> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<HappeningTask> taskList) {
		this.taskList = taskList;
	}
	
	public void addHappeningTask(HappeningTask task) {
		taskList.add(task);
	}

	public boolean removeHappeningTaskFromList(int taskId) {
		return taskList.remove(new HappeningTask(taskId,null,null,null,0,null));
	}
	
	public boolean removeHappeningTaskFromList(HappeningTask task) {
		return taskList.remove(task);
	}

	@Override
	public String toString() {
		return "Happening [happeningId=" + happeningId + ", happeningName=" + happeningName + ", start=" + start
				+ ", end=" + end + ", description=" + description + ", location=" + location + ", category=" + category
				+ ", happeningStatus=" + happeningStatus.getStatusName() + ", happeningHost="
				+ happeningHost.getUsername() + ", guestListSize=" + guestList.size() + ", version=" + version + "]";
	}
}
