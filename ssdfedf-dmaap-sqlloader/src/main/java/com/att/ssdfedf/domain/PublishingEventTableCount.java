package com.att.ssdfedf.domain;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;



@Entity
@Table(name = "CP_PUBLISHING_EVENT_TABLECOUNT")
public class PublishingEventTableCount implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /*@EmbeddedId
    private PublishingEventTableCountId id;*/
    
    @Id
    @Column(name = "PUBLISHING_EVENT_TABLECOUNT_ID")
    @SequenceGenerator(name="PUBLISHING_EVENT_TABLECOUNT_ID", sequenceName="PUBLISHING_EVENT_TABLECOUNT_ID", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PUBLISHING_EVENT_TABLECOUNT_ID")
    private Long id;
    
    @Column(name = "PUBLISHED_EVENT_ID")
    private Long pubEventId;
   
   @Column(name = "STATUS_ID")
    private Long statusId;
    
    @Column(name = "TABLE_NAME")
    private String tableName;
    
    @Column(name = "TABLE_ROW_COUNT")
    private Long tableRowCount;
    
    @Column(name = "MOD_USER_ID")
    private String modUserId;
    
    @Column(name = "MOD_DATE")
    private Date modDate;
    
    @Column(name = "CREATED_USER_ID")
    private String createdUserId;
    
    @Column(name = "CREATED_DATE")
    private Date createdDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPubEventId() {
		return pubEventId;
	}

	public void setPubEventId(Long pubEventId) {
		this.pubEventId = pubEventId;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Long getTableRowCount() {
		return tableRowCount;
	}

	public void setTableRowCount(Long tableRowCount) {
		this.tableRowCount = tableRowCount;
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

	public String getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "PublishingEventTableCount [id=" + id + ", pubEventId="
				+ pubEventId + ", statusId=" + statusId + ", tableName="
				+ tableName + ", tableRowCount=" + tableRowCount
				+ ", modUserId=" + modUserId + ", modDate=" + modDate
				+ ", createdUserId=" + createdUserId + ", createdDate="
				+ createdDate + "]";
	}

	

    /*public PublishingEventTableCount(){}
    
    public PublishingEventTableCount(Long pubEventTabelCountId, Long pubEventId){
        this.id = new PublishingEventTableCountId(pubEventTabelCountId, pubEventId);
    }

	public PublishingEventTableCountId getId() {
		return id;
	}

	public void setId(PublishingEventTableCountId id) {
		this.id = id;
	}*/

	

	
    
    
}
