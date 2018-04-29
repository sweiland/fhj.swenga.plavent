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
@Table(name = "HappeningCategory")
public class HappeningCategory implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int categoryID;
	
	@Column(nullable = false, unique=true, length = 64)
	private String categoryName;

	@Column(nullable = true, length = 512)
	private String description;
	
	@Version
	long version;
	
	public HappeningCategory() {
		// TODO Auto-generated constructor stub
	}
	
	public HappeningCategory(String categoryName, String description) {
		super();
		this.categoryName = categoryName;
		this.description = description;
	}



	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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
		result = prime * result + categoryID;
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
		HappeningCategory other = (HappeningCategory) obj;
		if (categoryID != other.categoryID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HappeningCategory [categoryID=" + categoryID + ", categoryName=" + categoryName + ", description="
				+ description + "]";
	}
	
	
}
