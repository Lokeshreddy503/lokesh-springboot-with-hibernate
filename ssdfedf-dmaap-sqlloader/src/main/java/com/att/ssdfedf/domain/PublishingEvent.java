package com.att.ssdfedf.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;



@Entity
@Table(name = "CP_PUBLISHING_EVENT")
public class PublishingEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "PUBLISHED_EVENT_ID")
    private Long id;
    
    @Column(name = "STATUS_ID")
    private Long statusId;
    
    @Column(name = "PUBLISHED_EVENT_DATE")
    private Date publishingEventDate;

    @Column(name = "PUBLISHED_EVENT_NAME")
    private String publishingEventName;
    
    @Column(name = "COMMENTS")
    private String comments;
    
    @Column(name = "FILETOSTAGTABLE_STARTEDTIME")
    private Date fileToStgTableStartDate;
    
    @Column(name = "FILETOSTAGETABLE_COMPLETEDTIME")
    private Date fileToStgTableCompletedDate;
    
    @Column(name = "MOD_USER_ID")
    private String modUserId;
    
    @Column(name = "MOD_DATE")
    private Date modDate;
    
    @Column(name = "CREATED_DATE")
    private Date createdDate;
    
    @Column(name = "CREATED_USER_ID")
    private String createdUserId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public Date getPublishingEventDate() {
		return publishingEventDate;
	}

	public void setPublishingEventDate(Date publishingEventDate) {
		this.publishingEventDate = publishingEventDate;
	}

	public String getPublishingEventName() {
		return publishingEventName;
	}

	public void setPublishingEventName(String publishingEventName) {
		this.publishingEventName = publishingEventName;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getFileToStgTableStartDate() {
		return fileToStgTableStartDate;
	}

	public void setFileToStgTableStartDate(Date fileToStgTableStartDate) {
		this.fileToStgTableStartDate = fileToStgTableStartDate;
	}

	public Date getFileToStgTableCompletedDate() {
		return fileToStgTableCompletedDate;
	}

	public void setFileToStgTableCompletedDate(Date fileToStgTableCompletedDate) {
		this.fileToStgTableCompletedDate = fileToStgTableCompletedDate;
	}

	public String getModUserId() {
		return modUserId;
	}

	public void setModUserId(String modUserId) {
		this.modUserId = modUserId;
	}

	public Date getModDate() {
		return modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}

	@Override
	public String toString() {
		return "PublishingEvent [id=" + id + ", statusId=" + statusId
				+ ", publishingEventDate=" + publishingEventDate
				+ ", publishingEventName=" + publishingEventName
				+ ", comments=" + comments + ", fileToStgTableStartDate="
				+ fileToStgTableStartDate + ", fileToStgTableCompletedDate="
				+ fileToStgTableCompletedDate + ", modUserId=" + modUserId
				+ ", modDate=" + modDate + ", createdDate=" + createdDate
				+ ", createdUserId=" + createdUserId + "]";
	}

	
    
    
}
