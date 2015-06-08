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
