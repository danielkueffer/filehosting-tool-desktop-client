package com.danielkueffer.filehosting.desktop.repository.pojos;

/**
 * The user POJO
 * 
 * @author dkueffer
 * 
 */
public class User {

	private int id;

	private String email;
	private String displayname;
	private String lastLogin;

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
	 * @return the displayname
	 */
	public String getDisplayname() {
		return displayname;
	}

	/**
	 * @param displayname
	 *            the displayname to set
	 */
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	/**
	 * @return the lastLogin
	 */
	public String getLastLogin() {
		return lastLogin;
	}

	/**
	 * @param lastLogin
	 *            the lastLogin to set
	 */
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
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
}
