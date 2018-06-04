package at.fh.swenga.plavent.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * @author Alexander Hoedl:
 * 
 *         Model holds global application settings
 */

@Entity
@Table(name = "AppProperty")
public class ApplicationProperty implements Serializable {


	@Id
	@Column(nullable = false, length = 128)
	private String token;
	
	@Column(nullable = false)
	private boolean value;
	
	@Column(nullable = true, length = 512)
	private String description;
	
	@Version
	long version;
	
	public ApplicationProperty() {
	}

	public ApplicationProperty(String token, boolean value, String description) {
		super();
		this.token = token;
		this.value = value;
		this.description = description;
	}



	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		ApplicationProperty other = (ApplicationProperty) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ApplicationProperty [token=" + token + ", value=" + value + ", description=" + description + "]";
	}
	
	
	
}
