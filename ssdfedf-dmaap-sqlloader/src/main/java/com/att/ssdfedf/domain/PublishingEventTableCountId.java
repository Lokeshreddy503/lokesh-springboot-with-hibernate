package com.att.ssdfedf.domain;

import javax.persistence.*;

import java.io.Serializable;



@Embeddable
public class PublishingEventTableCountId implements Serializable {
    
    private static final long serialVersionUID = 1L;

	@Column(name = "PUBLISHED_EVENT_ID")
    private Long pubEventId;
    
    @Column(name = "PUBLISHING_EVENT_TABLECOUNT_ID")
    @SequenceGenerator(name="PUBLISHING_EVENT_TABLECOUNT_ID", sequenceName="PUBLISHING_EVENT_TABLECOUNT_ID", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PUBLISHING_EVENT_TABLECOUNT_ID")
    private Long pubEventTabelCountId;    
    
    public PublishingEventTableCountId() {
    }
 
    public PublishingEventTableCountId(Long pubEventTabelCountId, Long pubEventId) {
        this.pubEventTabelCountId = pubEventTabelCountId;
        this.pubEventId = pubEventId;
    }

	public Long getPubEventId() {
		return pubEventId;
	}

	public void setPubEventId(Long pubEventId) {
		this.pubEventId = pubEventId;
	}

	public Long getPubEventTabelCountId() {
		return pubEventTabelCountId;
	}

	public void setPubEventTabelCountId(Long pubEventTabelCountId) {
		this.pubEventTabelCountId = pubEventTabelCountId;
	}
	    
}
