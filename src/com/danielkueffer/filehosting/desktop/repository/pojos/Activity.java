package com.danielkueffer.filehosting.desktop.repository.pojos;

import java.util.Date;

/**
 * The activity POJO
 * 
 * @author dkueffer
 * 
 */
public class Activity {

	private Date date;

	private String file;
	private String homeFolder;
	private String action;

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @return the homeFolder
	 */
	public String getHomeFolder() {
		return homeFolder;
	}

	/**
	 * @param homeFolder
	 *            the homeFolder to set
	 */
	public void setHomeFolder(String homeFolder) {
		this.homeFolder = homeFolder;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Activity [date=" + date + ", file=" + file + ", homeFolder="
				+ homeFolder + ", action=" + action + "]";
	}
}