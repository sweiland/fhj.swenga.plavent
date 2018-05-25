package at.fh.swenga.plavent.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Min;

@Entity
@Table(name = "HappeningTask")
public class HappeningTask implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int taskId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "happeningId", nullable = false)
	private Happening happening;

	@Column(nullable = false, length = 128)
	private String topic;

	@Column(nullable = true, length = 512)
	private String description;

	@Column(nullable = false, precision = 2)
	@Min(value = 0L, message = "The duration must be positive") // Nach oben hin offen, task koennta ja mehr als einen
																// Tag dauern...
	private double durationInHour;

	@ManyToOne(optional = true)
	@JoinColumn(name = "responsibleUser", nullable = true)
	private User responsibleUser;

	@Version
	long version;

	public HappeningTask() {
	}

	public HappeningTask(Happening happening, String topic, String description, double durationInHour,
			User responsibleUser) {
		super();
		this.happening = happening;
		this.topic = topic;
		this.description = description;

		this.durationInHour = durationInHour;
		this.responsibleUser = responsibleUser;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + taskId;
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
		HappeningTask other = (HappeningTask) obj;
		if (taskId != other.taskId)
			return false;
		return true;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public Happening getHappening() {
		return happening;
	}

	public void setHappening(Happening happening) {
		this.happening = happening;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getDurationInHour() {
		return durationInHour;
	}

	public void setDurationInHour(double durationInHour) {
		this.durationInHour = durationInHour;
	}

	public User getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(User responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	@Override
	public String toString() {
		return "Task [taskId=" + taskId + ", happeningID=" + happening.getHappeningId() + ", topic=" + topic
				+ ", description=" + description + ", durationInHour=" + durationInHour + ", responsibleUser="
				+ responsibleUser.getUsername() + "]";
	}

}
