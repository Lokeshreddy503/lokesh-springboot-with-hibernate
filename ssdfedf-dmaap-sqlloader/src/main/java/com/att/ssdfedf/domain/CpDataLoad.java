package com.att.ssdfedf.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "CP_DATALOAD")
public class CpDataLoad implements Serializable {
    
    private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "CP_DATA_LOAD_ID")
    @SequenceGenerator(name="CP_DATA_LOAD_ID", sequenceName="CP_DATA_LOAD_ID", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CP_DATA_LOAD_ID")
    private Long id;
    
    @Column(name = "FILE_NAME")
    private String fileName;
    
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	
    
    
}
