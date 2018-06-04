package at.fh.swenga.plavent.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Happening")
public class Happening implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int happeningId;

	@Column(nullable = false, length = 64)
	@Size(min = 2, max = 64, message = "Happeningname must be between 2 and 64 characters")
	@NotBlank
	private String happeningName;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	// @FutureOrPresent //Not working, so we handle it in the controller...
	private Calendar start;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar end;

	@Column(nullable = true, length = 512)
	@Size(min = 0, max = 128, message = "Description must be between 0 and 128 characters")
	private String description;

	@Column(nullable = false, length = 128)
	@Size(min = 2, max = 128, message = "Location must be between 2 and 128 characters")
	private String location;

	/**
	 * Load status with eager because there are just two entries (ACITVE and
	 * DELETED)
	 */
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "categoryId")
	private HappeningCategory category;

	/**
	 * Load status with eager because there are just two entries (ACITVE and
	 * DELETED)
	 */
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "statusId")
	private HappeningStatus happeningStatus;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "username")
	private User happeningHost;

	/**
	 * Load guests lazy - we don't want to have the whole db in memory
	 */
	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinTable(name = "Guestlist", joinColumns = { @JoinColumn(name = "happeningId") }, inverseJoinColumns = {
			@JoinColumn(name = "username") })
	private List<User> guestList;

	/**
	 * Load tasks lazy - we don't want to have the whole db in memory
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "happening")
	private List<HappeningTask> taskList;

	@Version
	long version;

	public Happening() {
	}

	public Happening(String happeningName, Calendar start, Calendar end, String description, String location,
			HappeningCategory category, HappeningStatus happeningStatus, User happeningHost, List<User> guestList,
			List<HappeningTask> tasks) {
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

	public Calendar getStart() {
		return start;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public Calendar getEnd() {
		return end;
	}

	public void setEnd(Calendar end) {
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
		return guestList.remove(new User(username, null, null, null,null, null));
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

	public String getHappeningInfos(String tabChar, String newLineChar) {
		StringBuilder builder = new StringBuilder();

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm");
		User host = getHappeningHost();
		String hostName = host.getFirstname() + " " + host.getLastname();

		builder.append("Happening:").append(tabChar).append(getHappeningName()).append(newLineChar);
		builder.append("Start:").append(tabChar).append(format.format(getStart().getTime())).append(newLineChar);
		builder.append("End:").append(tabChar).append(format.format(getEnd().getTime())).append(newLineChar);
		builder.append("Location:").append(tabChar).append(getLocation()).append(newLineChar);
		builder.append("Host:").append(tabChar).append(hostName).append(newLineChar);

		return builder.toString();
	}
}
