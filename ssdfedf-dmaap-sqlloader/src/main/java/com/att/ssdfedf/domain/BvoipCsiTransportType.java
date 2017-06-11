package com.att.ssdfedf.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name ="CP_DATALOAD_IPFR_QUALIFY")

public class BvoipCsiTransportType implements Serializable{
	
	    
	    private static final long serialVersionUID = 1L;

		@Id
		@Column(name = "IPFR_QUALIFY_ID")
	    private Long publishedEventId;
	    
	    @Column(name = "CP_DATA_LOAD_ID")
	    private Long cpDataLoadId;

		public Long getPublishedEventId() {
			return publishedEventId;
		}

		public void setPublishedEventId(Long publishedEventId) {
			this.publishedEventId = publishedEventId;
		}

		public Long getCpDataLoadId() {
			return cpDataLoadId;
		}

		public void setCpDataLoadId(Long cpDataLoadId) {
			this.cpDataLoadId = cpDataLoadId;
		}

		
	    
	    
	}



