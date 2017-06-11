package com.lokesh.ssdfedf.util;



public class SqlLoaderConstants {   
    
	public static final String DMAAP_SOURCE_DIR_TEST = "C:/lokesh/dataload";
	public static final String FILE_PROCESSING_DIR_TEST = "C:/lokesh/process";
	public static final String LOG_FILE_DIR_TEST = "C:/lokesh/log";
	public static final String BAD_FILE_DIR_TEST = "C:/lokesh/bad";
	public static final String DMAAP_ARCHIVE_DIR_TEST = "C:/lokesh/archive";
	public static final String DISCARD_FILE_DIR_TEST = "C:/lokesh/discard";
	
	public static final String DMAAP_CONTROL_FILE_TEMPLATE_DIR = "C:/lokesh/control/templates";
	public static final String DMAAP_CONTROL_FILE_DIR = "C:/lokesh/control";
	
	
	public static final String TNS_ENTRY_ST1 = "ssdfst1";
	public static final String TNS_ENTRY_UAT1 = "ssdfuat1";
	
	/*public static final String DMAAP_SOURCE_DIR_TEST = "/opt/app/apache/DMaaP/demosimplesub/received";
	public static final String FILE_PROCESSING_DIR_TEST = "/opt/app/apache/DMaaP/ssdf-cpc/process";
	public static final String LOG_FILE_DIR_TEST = "/opt/app/apache/DMaaP/ssdf-cpc/log";
	public static final String BAD_FILE_DIR_TEST = "/opt/app/apache/DMaaP/ssdf-cpc/bad";
	public static final String DMAAP_ARCHIVE_DIR_TEST = "/opt/app/apache/DMaaP/ssdf-cpc/archive";
	public static final String DISCARD_FILE_DIR_TEST = "/opt/app/apache/DMaaP/ssdf-cpc/discard";
	
	public static final String DMAAP_CONTROL_FILE_TEMPLATE_DIR = "/opt/app/apache/DMaaP/ssdf-cpc/control/templates";
	public static final String DMAAP_CONTROL_FILE_DIR = "/opt/app/apache/DMaaP/ssdf-cpc/control";
	
	public static final String TNS_ENTRY_ST1 = "ssdfst1.sbc.com";
	public static final String TNS_ENTRY_UAT1 = "ssdfuat1.sbc.com";*/
	
	
	public static final String CPC_DATALOAD_FILE_NAME = "EDF";
	public static final String CPC_DATALOAD_CREATED_USER_ID = "lm953s";
	public static final String CPC_PUB_EVENT_CREATED_USER_ID = "lm953s";
	
	public static final Long CPC_FILE_TO_STAGE_TABLE_START_STATUS = new Long(1100);
	public static final Long CPC_FILE_TO_STAGE_TABLE_END_STATUS = new Long(1101);
	public static final Long CPC_FILE_TO_STAGE_TABLE_ERROR_STATUS = new Long(730);
	
	
	
	
	public static final int PUB_EVENT_COUNT_FILE_LINE_COUNT = 2;
}