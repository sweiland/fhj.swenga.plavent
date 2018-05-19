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
@Table(name = "Status")
public class HappeningStatus implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int statusID;

	@Column(nullable = false, unique = true, length = 64)
	private String statusName;

	@Column(nullable = true, length = 512)
	private String description;

	@Version
	long version;

	public HappeningStatus() {
	}

	public HappeningStatus(String statusName, String description) {
		super();
		this.statusName = statusName;
		this.description = description;
	}

	public int getStatusID() {
		return statusID;
	}

	public void setStatusID(int statusID) {
		this.statusID = statusID;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "HappeningStatus [statusID=" + statusID + ", statusName=" + statusName + ", description=" + description
				+ "]";
	}
}
