package com.danielkueffer.filehosting.desktop.repository.pojos;

/**
 * The user POJO
 * 
 * @author dkueffer
 * 
 */
public class User {

	private int id;

	private String username;
	private String email;
	private String displayName;
	private String language;

	private long diskQuota;
	private long usedDiskSpace;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the diskQuota
	 */
	public long getDiskQuota() {
		return diskQuota;
	}

	/**
	 * @param diskQuota
	 *            the diskQuota to set
	 */
	public void setDiskQuota(long diskQuota) {
		this.diskQuota = diskQuota;
	}

	/**
	 * @return the usedDiskSpace
	 */
	public long getUsedDiskSpace() {
		return usedDiskSpace;
	}

	/**
	 * @param usedDiskSpace
	 *            the usedDiskSpace to set
	 */
	public void setUsedDiskSpace(long usedDiskSpace) {
		this.usedDiskSpace = usedDiskSpace;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (diskQuota ^ (diskQuota >>> 32));
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result
				+ (int) (usedDiskSpace ^ (usedDiskSpace >>> 32));
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (diskQuota != other.diskQuota)
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id != other.id)
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (usedDiskSpace != other.usedDiskSpace)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email
				+ ", displayName=" + displayName + ", language=" + language
				+ ", diskQuota=" + diskQuota + ", usedDiskSpace="
				+ usedDiskSpace + "]";
	}
}
