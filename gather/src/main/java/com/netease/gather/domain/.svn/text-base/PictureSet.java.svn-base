package com.netease.gather.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureSet {
	private Long autoid;
	private String url;
	private String title;
	private int setid;
	private String source;
	private String groupid;
	private String jobid;
	private String summary;
	private String author;
	private Date ptime;
	private int uploadnum;

	private int atLeast;//必要最少图片

	public Long getAutoid() {
		return autoid;
	}

	public void setAutoid(Long autoid) {
		this.autoid = autoid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSetid() {
		return setid;
	}

	public void setSetid(int setid) {
		this.setid = setid;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getPtime() {
		return ptime;
	}

	public void setPtime(Date ptime) {
		this.ptime = ptime;
	}

	public int getUploadnum() {
		return uploadnum;
	}

	public void setUploadnum(int uploadnum) {
		this.uploadnum = uploadnum;
	}

	public int getAtLeast() {
		return atLeast;
	}

	public void setAtLeast(int atLeast) {
		this.atLeast = atLeast;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("title:").append(title).append(",url:").append(url).append(",groupid:").append(groupid).append(",jobid:").append(jobid);
		if (ptime != null) {
			sb.append(",ptime:").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ptime));
		}
		return sb.toString();
	}

}
