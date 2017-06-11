package com.lokesh.lokeshlokesh.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.lokesh.lokeshlokesh.UAT1RepositoryConfig;
import com.lokesh.lokeshlokesh.domain.BvoipCsiTransportType;
import com.lokesh.lokeshlokesh.domain.CpDataLoad;
import com.lokesh.lokeshlokesh.domain.PublishingEvent;
import com.lokesh.lokeshlokesh.domain.PublishingEventTableCount;
import com.lokesh.lokeshlokesh.repository.st.BvoipCsiTransportTypeRepo;
import com.lokesh.lokeshlokesh.repository.st.ST1CpDataLoadRepository;
import com.lokesh.lokeshlokesh.repository.st.ST1PublishingEventRepository;
import com.lokesh.lokeshlokesh.repository.st.ST1PublishingEventTableCountRepo;
import com.lokesh.lokeshlokesh.repository.st.STVishnuTestRepository;
import com.lokesh.lokeshlokesh.repository.uat.BvoipCsiTransportTypeRepoUat;
import com.lokesh.lokeshlokesh.repository.uat.UAT1CpDataLoadRepository;
import com.lokesh.lokeshlokesh.repository.uat.UAT1PublishingEventRepository;
import com.lokesh.lokeshlokesh.repository.uat.UAT1PublishingEventTableCountRepo;
import com.lokesh.lokeshlokesh.repository.uat.UAT1VishnuTestRepository;
import com.lokesh.lokeshlokesh.util.SqlLoaderConstants;
import com.lokesh.lokeshlokesh.util.SqlLoaderUtil;


@Service
public class CPCSpringQuartzService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DataSource dataSource;
	@Autowired
	private UAT1RepositoryConfig uat1RepositoryConfig;
	
	@Autowired
	private DataSource uat1DataSource;
	@Value("${spring.application.env}")
	private String application_env;
	
	@Value("${spring.release.st1}")
	private String st1_release;
	
	@Value("${spring.release.uat1}")
	private String uat1_release;
	
	@Value("${spring.datasource.password}")
	private String st1_db_pwd;
	
	@Value("${spring.uat1.datasource.password}")
	private String uat1_db_pwd;
	
	@Autowired(required=true)
	private ST1PublishingEventRepository st1PublishingEventRepository;
	
	@Autowired(required=true)
	private UAT1PublishingEventRepository uat1PublishingEventRepository;
	
	@Autowired(required=true)
	private ST1CpDataLoadRepository st1CpDataLoadRepository;
	
	@Autowired(required=true)
	private UAT1CpDataLoadRepository uat1CpDataLoadRepository;
	
	@Autowired(required=true)
	private ST1PublishingEventTableCountRepo st1PublishingEventTableCountRepo;
	
	@Autowired(required=true)
	private UAT1PublishingEventTableCountRepo uat1PublishingEventTableCountRepo;
	
	@Autowired(required=true)
	private STVishnuTestRepository stVishnuTestRepository;
	
	@Autowired(required=true)
	private UAT1VishnuTestRepository uat1VishnuTestRepository;
	
	@Autowired(required=true)
	BvoipCsiTransportTypeRepo stBvoipCsiTransportTypeRepo;
	
	@Autowired(required=true)
	private BvoipCsiTransportTypeRepoUat uatBvoipCsiTransportTypeRepo;
	
	
	
	
	Process prc;
	int totalLinesInFile = 0;
	
    @Scheduled(cron = "*/10 * * * * *")
    public void configureAutoRunJob() {
        log.error("::::::::::::::::::::::::::::: Cron Scheduler Invoked."+this.application_env +":::::::::::::: Date"+new Date());        
        
      if("TEST".equalsIgnoreCase(this.application_env)) {
        	log.error("::::::::::::::::::::::::::::: Creating new thread.");
        	loadTestData("TEST");
	        log.error("::::::::::::::::::::::::::::: Creating new thread ended.");
        }
    }
    private synchronized void loadTestData(String appEnv) {
    /*private  void loadTestData(String appEnv) {*/
    	log.info("************************************************************************** entered into synchronized block.");
    	String event_id = "";
    	String relese_db = "";
    	
    	try {
	    	SqlLoaderUtil sqlLoaderUtil = new SqlLoaderUtil();
	        File[] files = sqlLoaderUtil.getFilesFromDirectoryInSortOrder(SqlLoaderConstants.DMAAP_SOURCE_DIR_TEST);
	        
	    	for(File file : files) {
	        	log.info("::::::::::::::::::::::::::::: sorted file names : "+file.getName());
	        	String fileName = file.getName();
	        	String dir = file.getAbsolutePath();
	        	//if(file.length() > 16 && file.getName().endsWith("TransportType.txt") ) {
	        	if(fileName.length() > 16) {
	            	String pubEventId = fileName.substring(0,8);
	            	//String pubEventId = "1234567890";
	            	log.info("::::::::::::::::::::::::::::: eventId : "+pubEventId);
	            	event_id = pubEventId;
	            	String releaseName = fileName.substring(9,13);
	            	//String releaseName = "1702";
	            	log.info("::::::::::::::::::::::::::::: releaseName : "+releaseName);
	            	log.info("::::::::::::::::::::::::::::: appenv : "+appEnv);
	            	totalLinesInFile= sqlLoaderUtil.getLinesFromFile(dir);
        			log.info("::::::::::::::::::::::::::::: Total liines in file : "+totalLinesInFile);
	            	if(!isLoadDataInProgress(appEnv)) {
	            		
	            		if(this.st1_release.equals(releaseName)) {
	            			
	            			
	            			if(validEventIdToProcess("ST1", pubEventId)) {
	            				
	            				String currentZipFileName = fileName.substring(0, (fileName.length()-4));
		            			String processingDir = SqlLoaderConstants.FILE_PROCESSING_DIR_TEST+"/"+currentZipFileName;
	            				//Lokesh
	            				//String processingDir = SqlLoaderConstants.FILE_PROCESSING_DIR_TEST;
	            				String sourceFolder = SqlLoaderConstants.DMAAP_SOURCE_DIR_TEST;
	            				
		            			log.info("::::::::::::::::::::::::::::: file name with path : "+SqlLoaderConstants.DMAAP_SOURCE_DIR_TEST+"/"+fileName);
		            			log.info("::::::::::::::::::::::::::::: processing dir : "+processingDir);
		            			 
		            			
		            			
		            			sqlLoaderUtil.moveFiletoDiffFolder(sourceFolder, processingDir);
			            		//Lokesh
			            		log.info("::::::::::::::::::::::::::::: Feed File Successfully MOved");
			            		relese_db = "ST1";
			            		
		            			
		            			Long cpDataLoadId = createCpDataLoad(relese_db);
		            			log.info("::::::::::::::::::::::::::::: cpDataLoadDomain cp_dataload_id : "+cpDataLoadId);
		            			pubEventId = (pubEventId+cpDataLoadId).toString();
		            			log.info("::::::::::::::::::::::::::::: PublishEventId updated by appending cpDataLoadId :" +pubEventId);
		            			if(cpDataLoadId == null) {
			            			throw new NullPointerException();
			            		}
	            				
			            		
			            		
			            		
			            		
			            		
			            		
			            		
			            		  log.info("::::::::::::::::::::::::::::: Before createPublishingEvent : "+relese_db + "; " + pubEventId  + "; " +  processingDir  + "; " +  sqlLoaderUtil);

			            		Long pubEventIdDomain = createPublishingEvent(relese_db, pubEventId, processingDir, sqlLoaderUtil);
			            		
			            		log.info("::::::::::::::::::::::::::::: pubEventDomain publishing_event_id : "+pubEventIdDomain);
	            				
			            		if(pubEventIdDomain == null) {
			            			throw new NullPointerException();
			            		}
		            			
			            		String ctlFilesDestDir = SqlLoaderConstants.DMAAP_CONTROL_FILE_DIR+"/"+currentZipFileName+"/"+relese_db;
			            		log.info("::::::::::::::::::::::::::::: ctlFilesDestDir : "+ctlFilesDestDir);
			            		log.info("::::::::::::::::::::::::::::: ctlFilesTemplatesDir : "+SqlLoaderConstants.DMAAP_CONTROL_FILE_TEMPLATE_DIR);
			            		sqlLoaderUtil.moveFiletoDiffFolder(SqlLoaderConstants.DMAAP_CONTROL_FILE_TEMPLATE_DIR, ctlFilesDestDir);
			            		log.info("::::::::::::::::::::::::::::: control files copied into : "+ctlFilesDestDir);
			            		
			            		
			            		String badFileDir = SqlLoaderConstants.BAD_FILE_DIR_TEST+"/"+currentZipFileName+"/"+relese_db;
			            		String discardFileDir = SqlLoaderConstants.DISCARD_FILE_DIR_TEST+"/"+currentZipFileName+"/"+relese_db;
			            		sqlLoaderUtil.makeFolder(badFileDir);
			            		sqlLoaderUtil.makeFolder(discardFileDir);
			            		
			            		for(File fl : sqlLoaderUtil.getFilesFromDirectory(ctlFilesDestDir)) {
			            			log.info("::::::::::::::::::::::::::::: modifying control file : "+ctlFilesDestDir+"/"+fl.getName());
			            			sqlLoaderUtil.updateControlFile(ctlFilesDestDir+"/"+fl.getName(), pubEventIdDomain.toString(), processingDir, badFileDir, discardFileDir, cpDataLoadId.toString(),event_id);
			            		}	
			            		 log.info(":::::::::::::::::::::::::::::calling loadST1Data ... ");

			            		
			            		loadST1Data(relese_db, pubEventIdDomain, cpDataLoadId, processingDir, ctlFilesDestDir, sqlLoaderUtil,fileName);
			            		 log.info("::::::::::::::::::::::::::::: calling Updatestatus in pubevent table ... ");

			            		updateStatusInPubEventTable(relese_db, pubEventIdDomain.toString(), SqlLoaderConstants.CPC_FILE_TO_STAGE_TABLE_END_STATUS);
		            			
			            		String stgToTgtST1Status = moveDataStageToTargetST1(cpDataLoadId);
			            		log.info("****************************************** ST1 DB stgToTgtST1Status :"+stgToTgtST1Status+"  for publishing_event_id : "+pubEventIdDomain);
			            		
			            		//if("SUCCESS".equalsIgnoreCase(stgToTgtST1Status)) {
			            		//String stgToTgtST1Status ="0";
			            			if("0".equalsIgnoreCase(stgToTgtST1Status)) {
				            		relese_db = "UAT1";
				            	 cpDataLoadId = createCpDataLoad(relese_db);
				            		log.info("::::::::::::::::::::::::::::: cpDataLoadDomain cp_dataload_id : "+cpDataLoadId);
				            		
				            		if(cpDataLoadId == null) {
				            			throw new NullPointerException();
				            		}
				            		pubEventId = (pubEventId+cpDataLoadId).toString();
				            		
				            	 pubEventIdDomain = createPublishingEvent(relese_db, pubEventId, processingDir, sqlLoaderUtil);
				            		log.info("::::::::::::::::::::::::::::: pubEventDomain publishing_event_id : "+pubEventIdDomain);
		            				
				            		if(pubEventIdDomain == null) {
				            			throw new NullPointerException();
				            		}
				            		
				            	 ctlFilesDestDir = SqlLoaderConstants.DMAAP_CONTROL_FILE_DIR+"/"+currentZipFileName+"/"+relese_db;
				            		log.info("::::::::::::::::::::::::::::: ctlFilesDestDir : "+ctlFilesDestDir);
				            		log.info("::::::::::::::::::::::::::::: ctlFilesTemplatesDir : "+SqlLoaderConstants.DMAAP_CONTROL_FILE_TEMPLATE_DIR);
				            		sqlLoaderUtil.moveFiletoDiffFolder(SqlLoaderConstants.DMAAP_CONTROL_FILE_TEMPLATE_DIR, ctlFilesDestDir);
				            		log.info("::::::::::::::::::::::::::::: control files copied into : "+ctlFilesDestDir);
				            		
				            		
				            		 badFileDir = SqlLoaderConstants.BAD_FILE_DIR_TEST+"/"+currentZipFileName+"/"+relese_db;
				            		 discardFileDir = SqlLoaderConstants.DISCARD_FILE_DIR_TEST+"/"+currentZipFileName+"/"+relese_db;
				            		sqlLoaderUtil.makeFolder(badFileDir);
				            		sqlLoaderUtil.makeFolder(discardFileDir);
				            		
				            		for(File fl : sqlLoaderUtil.getFilesFromDirectory(ctlFilesDestDir)) {
				            			log.info("::::::::::::::::::::::::::::: modifying control file : "+ctlFilesDestDir+"/"+fl.getName());
				            			sqlLoaderUtil.updateControlFile(ctlFilesDestDir+"/"+fl.getName(), pubEventIdDomain.toString(), processingDir, badFileDir, discardFileDir, cpDataLoadId.toString(),event_id);
				            		}
				            		
			            			loadUAT1Data(relese_db, pubEventIdDomain, cpDataLoadId, processingDir, ctlFilesDestDir, sqlLoaderUtil);
			            			
			            			updateStatusInPubEventTable(relese_db, pubEventIdDomain.toString(), SqlLoaderConstants.CPC_FILE_TO_STAGE_TABLE_END_STATUS);
				            		
			            			//String stgToTgtUAT1Status = moveDataStageToTargetUAT1(cpDataLoadId, pubEventIdDomain);
			            			String stgToTgtUAT1Status = moveDataStageToTargetUAT1(cpDataLoadId);
				            		log.info("****************************************** UAT1 DB stgToTgtUAT1Status :"+stgToTgtUAT1Status+"  for publishing_event_id : "+pubEventIdDomain);
			            			
				            		sqlLoaderUtil.moveZipFiletoDiffFolder(fileName, SqlLoaderConstants.DMAAP_SOURCE_DIR_TEST, SqlLoaderConstants.DMAAP_ARCHIVE_DIR_TEST);
				            		log.info("****************************************** Loading into UAT1 DB is completed. Moving the ZIP file into Archive Dir : "+fileName);
			            		}
			            		
	            			}else {
	            				log.info("::::::::::::::::::::::::::::: eventId not in Seq for file : "+fileName+" in release :"+this.st1_release);
	            			}
	            		}
	            		
	            		
	            		
	            		
	            		
	                    
	                }else {
	                	log.error("::::::::::::::::::::::::::::: load data InProgress for file : "+fileName);
	                	if(isLoadDataError(releaseName)) {
	                		log.error("::::::::::::::::::::::::::::: load data Error for file : "+fileName);
	                		//send error email if any load failed in ST1 
	                	}
	                }
	        	
	        	}
	        	
	        }
    	}catch (Exception e) {
    		log.error("************** Error loading files from CPC");
    		e.printStackTrace();
    		log.error("************** Updating error code 730 in Publishing_Event table for event_id :"+event_id);
    		updateStatusInPubEventTable(relese_db, event_id, SqlLoaderConstants.CPC_FILE_TO_STAGE_TABLE_ERROR_STATUS);
    		log.error("************** Updating error code 730 in Publishing_Event table completed for event_id :"+event_id);
    		
    		
    	}
    	
    	
    	
    	
    	
    	
    	
    }
    private boolean isLoadDataError(String releaseName) {
    	boolean isError = false;
    	int statusId = 0;
        
    	if(this.st1_release.equals(releaseName)) {
    		Long st1PubEventCount = st1PublishingEventRepository.pubEventCount();
	        PublishingEvent st1PublishingEvent = st1PublishingEventRepository.findFirstByOrderByIdDesc();        
	        log.error("::::::::::::::::::::::::::::: pubEventCount in ST1 DB: "+st1PubEventCount.longValue());
	        
	        if(st1PublishingEvent != null && st1PublishingEvent.getStatusId() != null) {
	        	statusId = st1PublishingEvent.getStatusId().intValue();
		        
	        	if(statusId == 700 || statusId == 710 || statusId == 720 || statusId == 730) {
		    		isError = true;
		    	}
	        }
	        
	        Long uat1PubEventCount = uat1PublishingEventRepository.pubEventCount();
	        PublishingEvent uat1PublishingEvent = uat1PublishingEventRepository.findFirstByOrderByIdDesc();        
	        log.error("::::::::::::::::::::::::::::: pubEventCount in UAT1 DB: "+uat1PubEventCount.longValue());
	        
	        if(uat1PublishingEvent != null && uat1PublishingEvent.getStatusId() != null) {
	        	statusId = uat1PublishingEvent.getStatusId().intValue();
		        
	        	if(statusId == 700 || statusId == 710 || statusId == 720 || statusId == 730) {
		    		isError = true;
		    	}
	        }
    	}
    	
    	return isError;
    }
    private boolean isLoadDataInProgress(String appEnv) {
    	boolean inProgressTask = false;
        
    	if("TEST".equalsIgnoreCase(appEnv)) {
    		log.info("::::::::::::::::::::::::::::: in isLoadDataInProgress.");
	    	PublishingEvent st1PublishingEvent = st1PublishingEventRepository.findFirstByOrderByIdDesc();
	        PublishingEvent uat1PublishingEvent = uat1PublishingEventRepository.findFirstByOrderByIdDesc();
	        
	        if(st1PublishingEvent != null && st1PublishingEvent.getStatusId() != null && st1PublishingEvent.getCreatedUserId().equalsIgnoreCase(SqlLoaderConstants.CPC_PUB_EVENT_CREATED_USER_ID) && st1PublishingEvent.getStatusId().intValue() != 1111 ) {
	        	inProgressTask = true;
	        }else if(uat1PublishingEvent != null && uat1PublishingEvent.getStatusId() != null && uat1PublishingEvent.getCreatedUserId().equalsIgnoreCase(SqlLoaderConstants.CPC_PUB_EVENT_CREATED_USER_ID) && uat1PublishingEvent.getStatusId().intValue() != 1111) {
	        	inProgressTask = true;
	        }
    	}
    	
    	log.info(":::::::::::::::::::::::::::::inProgressTask "+inProgressTask);
    	return inProgressTask;
    	
    }
    private boolean validEventIdToProcess(String release_env, String pubEventId) {
    	log.info("::::::::::::::::::::::::::::: in validEventIdToProcess.");
    	boolean isValidEventId = false;
    	boolean isValidEventIdST1 = false;
    	boolean isValidEventIdUAT1 = false;
    	//int curEventDate = Integer.valueOf(pubEventId.substring(0, 8));
    	//int curSeqNumber = Integer.valueOf(pubEventId.substring(8,2));
    	//log.info("::::::::::::::::::::::::::::: in validEventIdToProcess curEventDate :"+curEventDate+"   ::::::::::: curSeqNumber "+curSeqNumber);
    	log.info("::::::::::::::::::::::::::::: in validEventIdToProcess release_env:"+release_env+"   ::::::::::: pubEventId "+pubEventId);
    	
    	if("ST1".equalsIgnoreCase(release_env)) {
    		log.info("::::::::::::::::::::::::::::: in validEventIdToProcess validating event id for ST1.");
    	//	PublishingEvent st1PublishingEvent = st1PublishingEventRepository.findFirstByOrderByIdDesc();
	      //  PublishingEvent uat1PublishingEvent = uat1PublishingEventRepository.findFirstByOrderByIdDesc();
	        PublishingEvent st1PublishingEvent = st1PublishingEventRepository.findById(Long.parseLong(pubEventId));
	        PublishingEvent uat1PublishingEvent = uat1PublishingEventRepository.findById(Long.parseLong(pubEventId));
	        if(st1PublishingEvent == null ) {
	        	log.info("::::::::::::::::::::::::::::: in validEventIdToProcess publishing event value is null in ST :");
	        	//isValidEventIdST1 = (curSeqNumber == 1) ? true : false;
	        	isValidEventIdST1 =  true;
	        }else if(st1PublishingEvent != null && st1PublishingEvent.getId() != null) {
	        	log.info("::::::::::::::::::::::::::::: in validEventIdToProcess publishing event value in ST is not  null :");
	        	/*int dbEventDate = Integer.valueOf(st1PublishingEvent.getId().toString().substring(0, 8));
	        	int dbSeqNumber = Integer.valueOf(st1PublishingEvent.getId().toString().substring(8));
	        	log.info("::::::::::::::::::::::::::::: in validEventIdToProcess ST1 dbEventDate :"+dbEventDate+"   ::::::::::: dbSeqNumber "+dbSeqNumber);
	        	
	        	if(curEventDate == dbEventDate) {
	        		isValidEventIdST1 = (curSeqNumber == (dbSeqNumber+1)) ? true : false;
	        	}else if(curEventDate > dbEventDate) {
	        		isValidEventIdST1 = (curSeqNumber == 1) ? true : false;
	        	}*/
	        	isValidEventIdST1 =  false;
	        	
	        	
	        }
			
				        
	        if(uat1PublishingEvent == null) {
	        	log.info("::::::::::::::::::::::::::::: in validEventIdToProcess publishing event value is null in UAT:");
	        	isValidEventIdUAT1 =  true;
	        }else if(uat1PublishingEvent != null && uat1PublishingEvent.getId() != null) {
	        	log.info("::::::::::::::::::::::::::::: in validEventIdToProcess publishing event value in UAT is not  null :");
	        	
	        	/*int dbEventDate = Integer.valueOf(uat1PublishingEvent.getId().toString().substring(0, 8));
	        	int dbSeqNumber = Integer.valueOf(uat1PublishingEvent.getId().toString().substring(8));
	        	log.info("::::::::::::::::::::::::::::: in validEventIdToProcess ST1 dbEventDate :"+dbEventDate+"   ::::::::::: dbSeqNumber "+dbSeqNumber);
	        	
	        	if(curEventDate == dbEventDate) {
	        		isValidEventIdUAT1 = (curSeqNumber == (dbSeqNumber+1)) ? true : false;
	        	}else if(curEventDate > dbEventDate) {
	        		isValidEventIdUAT1 = (curSeqNumber == 1) ? true : false;
	        	}*/
	        	isValidEventIdUAT1 =  false;
	        }
	        
	        log.info("::::::::::::::::::::::::::::: in validEventIdToProcess isValidEventIdST1 :"+isValidEventIdST1+":::::::::::: isValidEventIdUAT1 : "+isValidEventIdUAT1);
	        
	       isValidEventId = (isValidEventIdST1 && isValidEventIdUAT1) ? true : false;
	       
    	}
    	
    	
    	
    	return isValidEventId;
    	//return isValidEventIdST1;
    }
    private Long createCpDataLoad(String releaseDB) {
		CpDataLoad cpDataLoad = new CpDataLoad();
		cpDataLoad.setFileName(SqlLoaderConstants.CPC_DATALOAD_FILE_NAME);//cpc
		cpDataLoad.setCreatedDate(new Date());
		cpDataLoad.setCreatedUserId(SqlLoaderConstants.CPC_DATALOAD_CREATED_USER_ID);
		
		CpDataLoad cpDataLoadDomain = null;
		if("ST1".equalsIgnoreCase(releaseDB)) {
			cpDataLoadDomain = st1CpDataLoadRepository.save(cpDataLoad);
		}else if("UAT1".equalsIgnoreCase(releaseDB)) {
			cpDataLoadDomain = uat1CpDataLoadRepository.save(cpDataLoad);
		}
		
		return (cpDataLoadDomain != null) ? cpDataLoadDomain.getId() : null;
    }
    private Long createPublishingEvent(String releaseDB, String pubEventId, String processingDir, SqlLoaderUtil sqlLoaderUtil) {
    	/*log.info("::::::::::::::::::::::::::::: Within createPublishingEvent : "+processingDir+"/"+pubEventId+"_PUBLISHING_EVENT.txt");
    	String lineText = sqlLoaderUtil.getLineFromFile(2, processingDir+"/"+pubEventId+"_PUBLISHING_EVENT.txt");
    	
    	log.info("::::::::::::::::::::::::::::: in createPublishingEvent data received in publishing_event.txt file :"+lineText);
    	String[] items = lineText.split("\\|");
    	 log.info("::::::::::::::::::::::::::::: After splitting ...  :"+items[0] + " ; " + items[2]);*/
        //lokesh
    	SimpleDateFormat originalFormlokesher = new SimpleDateFormat ("yyyyMMdd");
    	SimpleDateFormat sdf1 = new SimpleDateFormat ("MM/dd/yyyy");
    	String pubEventDate = pubEventId.substring(0, 8);
    	 ParsePosition pos = new ParsePosition(0);
    	 Date dateFromString = originalFormlokesher.parse(pubEventDate, pos);
    	 String pubEveDate = sdf1.format(dateFromString);
    	 
    	log.info(":::::::::::::::::::::::::::::" + pubEventDate);
    	/*if((items[0] == null) || ("".equals(items[0])) || (!pubEventId.equalsIgnoreCase(items[0]))) {
    		throw new NullPointerException();
    		 

    	}*/
    	if((pubEventId == null) || ("".equals(pubEventId))) {
    		throw new NullPointerException();
    	}
    	log.info("::::::::::::::::::::::::::::: pubEventid : "+ pubEventId +"pubeventDate "+pubEventDate);
    	
    	PublishingEvent pubEvent = new PublishingEvent();
    	pubEvent.setId(new Long(pubEventId));
    	pubEvent.setStatusId(SqlLoaderConstants.CPC_FILE_TO_STAGE_TABLE_START_STATUS);
    	try{
    		//SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
    		pubEvent.setPublishingEventDate(sdf1.parse(pubEveDate));
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	 log.info("::::::::::::::::::::::::::::: **********");
       
    	pubEvent.setPublishingEventName(pubEventId);
    	pubEvent.setComments("Inserting a record from Java Code.");
    	
    	try{
    		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    		String dateTime = sdf.format(Calendar.getInstance().getTime());
    		pubEvent.setFileToStgTableStartDate(sdf.parse(dateTime));
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	pubEvent.setCreatedUserId(SqlLoaderConstants.CPC_PUB_EVENT_CREATED_USER_ID);
    	
    	PublishingEvent pubEventDomain = null;
    	if("ST1".equalsIgnoreCase(releaseDB)) {
    		pubEventDomain = st1PublishingEventRepository.save(pubEvent);
    	}else if("UAT1".equalsIgnoreCase(releaseDB)) {
    		pubEventDomain = uat1PublishingEventRepository.save(pubEvent);
    	}
    	 log.info(":::::::::::::::::::::::::::::before returning" + pubEventDomain);
     	
		
		return (pubEventDomain != null) ? pubEventDomain.getId() : null;
    }
    
    private void loadST1Data(String relese_db, Long pubEventId, Long cpDataLoadId, String processingDir, String ctlFilesDestDir, SqlLoaderUtil sqlLoaderUtil,String fileName) throws FileNotFoundException {
    	
    	
    	Runtime rt;
        //Process prc;
        String cmd = "";
      // int totalLinesInFile= sqlLoaderUtil.getLinesFromFile(processingDir);
        //int totalLinesInFile= 2;
        log.info(":::::::::::::::::::::::::::: in loadST1Data table row count is null :" +totalLinesInFile);
        
        
    	/*String pubEvntTblCntDataFileNameWithPath = processingDir+"/"+pubEventId+"_PUBLISHING_EVENT_COUNT.txt";
		log.info(":::::::::::::::::::::::::::: in loadST1Data ctlFileNameWithDir :"+pubEvntTblCntDataFileNameWithPath);
		
		File pubEvntTblCntDataFile = new File(pubEvntTblCntDataFileNameWithPath);*/
		//if(pubEvntTblCntDataFile.exists() && !pubEvntTblCntDataFile.isDirectory()) {
		if(totalLinesInFile != 0 ) {
			String tableName = fileName.substring(21, (fileName.length()-4));
			log.info(":::::::::::::::::::::::::::: tableName :"+tableName);
			//for(int i=2; i<=SqlLoaderConstants.PUB_EVENT_COUNT_FILE_LINE_COUNT; i++) {
				/*String lineText = sqlLoaderUtil.getLineFromFile(i, processingDir+"/"+pubEventId+"_PUBLISHING_EVENT_COUNT.txt");
		    	log.info("::::::::::::::::::::::::::::: in loadST1Data data received in PUBLISHING_EVENT_COUNT.txt file :"+lineText);
		    	String[] items = lineText.split("\\|");
		    	
		    	if((items[0] == null) || ("".equals(items[0])) || (!pubEventId.toString().equalsIgnoreCase(items[0]))) {
		    		log.info(":::::::::::::::::::::::::::: in loadST1Data published event id is null :"+items[0]);
		    		throw new NullPointerException();
		    	}else if((items[1] == null) || ("".equals(items[1]))) {
		    		log.info(":::::::::::::::::::::::::::: in loadST1Data table name is null :"+items[1]);
		    		throw new NullPointerException();
		    	}else if((items[2] == null) || ("".equals(items[2]))) {
		    		log.info(":::::::::::::::::::::::::::: in loadST1Data table row count is null :"+items[2]);
		    		throw new NullPointerException();
		    	}
		    	*/
        log.info(":::::::::::::::::::::::::::: in loadST1Data published event id is null :"+pubEventId);
        log.info(":::::::::::::::::::::::::::: in loadST1Data table row count is null :"+totalLinesInFile);
		    	PublishingEventTableCount pubEventTableCount = new PublishingEventTableCount();
		    	pubEventTableCount.setPubEventId(pubEventId);
		    	//pubEventTableCount.setTableName(pubEventId+tableName);
		    	pubEventTableCount.setTableName(pubEventId+"EDF_TransportType");
		    	pubEventTableCount.setTableRowCount(new Long(totalLinesInFile)-1);
		    	pubEventTableCount.setModUserId(SqlLoaderConstants.CPC_PUB_EVENT_CREATED_USER_ID);
		    	pubEventTableCount.setCreatedUserId(SqlLoaderConstants.CPC_PUB_EVENT_CREATED_USER_ID);
		    	
		    	log.info("::::::::::::::::::::::::::::: in loadST1Data Inserting a record into PUBLISHING_EVENT_TABLECOUNT :"+pubEventTableCount.toString());
		    	
		    	st1PublishingEventTableCountRepo.save(pubEventTableCount);
		    	log.info("::::::::::::::::::::::::::::: in loadST1Data Inserting a record into PUBLISHING_EVENT_TABLECOUNT completed :"+pubEventTableCount.toString());
			//}
			
			/*Long pubEventTblCntCount = st1PublishingEventTableCountRepo.pubEventTableCntCount(pubEventId);
			if((SqlLoaderConstants.PUB_EVENT_COUNT_FILE_LINE_COUNT-1) == pubEventTblCntCount.intValue()) {
				
			}*/
			
			 log.info(":::::::::::::::::::::::::::: in loadST1Data Iterating thru files "+ctlFilesDestDir);
	        
	        
	    	for(File file : sqlLoaderUtil.getFilesFromDirectory(ctlFilesDestDir)) {
	    		log.info(":::::::::::::::::::::::::::: in for loop Iterating thru files "+file);
	    		String fileNameWithoutExtn = file.getName().substring(0, (file.getName().length()-4));
	    		log.info(":::::::::::::::::::::::::::: in loadST1Data fileNameWithoutExtn :"+fileNameWithoutExtn);
	    		
		    		String ctlFileNameWithPath = ctlFilesDestDir+"/"+file.getName();
		    		String logFileNameWithPath = SqlLoaderConstants.LOG_FILE_DIR_TEST+"/"+fileNameWithoutExtn+".log";
		    		log.info(":::::::::::::::::::::::::::: in loadST1Data ctlFileNameWithPath :"+ctlFileNameWithPath);
		    		log.info(":::::::::::::::::::::::::::: in loadST1Data logFileNameWithPath :"+logFileNameWithPath);
		    		
		    		//cmd ="sqlldr sce/"+this.st1_db_pwd+"@"+SqlLoaderConstants.TNS_ENTRY_ST1+" control="+ctlFileNameWithPath+" log="+logFileNameWithPath;
		    		//cmd ="sqlldr userid=sce/"+this.st1_db_pwd+"@\"(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=zld01394.vci.lokesh.com)(PORT= 1524)))(CONNECT_DATA=(SERVER=DEDICATED)(SID=d1c1d808)))\" control="+ctlFileNameWithPath+" log="+logFileNameWithPath;
		    		//cmd ="sqlldr userid=sce/"+this.st1_db_pwd+"@" +"\\'(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=zld01394.vci.lokesh.com)(PORT= 1524)))(CONNECT_DATA=(SERVER=DEDICATED)(SID=d1c1d808)))\\'" + " control="+ctlFileNameWithPath+" log="+logFileNameWithPath;
		    		  //cmd = "sqlldr userid=sce/"+this.st1_db_pwd+"@" +"\\\"(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=zld01394.vci.lokesh.com)(PORT= 1524)))(CONNECT_DATA=(SERVER=DEDICATED)(SID=d1c1d808)))\\\"" +" control="+ctlFileNameWithPath+" log="+logFileNameWithPath; 
		    		//cmd = "sqlldr userid=sce/"+this.st1_db_pwd+"@" +"\\\"(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=zld01394.vci.lokesh.com)(PORT="
		    			//	+ "1524)))(CONNECT_DATA=(SERVER=DEDICATED)(SID=d1c1d808)))\\\"" +" control="+ctlFileNameWithPath+" log="+logFileNameWithPath; 
		    		
		    		 cmd = "sqlldr userid=SCE/"+this.st1_db_pwd+"@" +"\\\"(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=zld01394.vci.lokesh.com)(PORT= 1524)))(CONNECT_DATA=(SERVER=DEDICATED)(SID=d1c1d808)))\\\"" +" control="+ctlFileNameWithPath+" log="+logFileNameWithPath; 
		    		// cmd = "sqlldr userid=SCE/"+this.st1_db_pwd+"@" +"\\\"(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=zlt05505.vci.lokesh.com)(PORT= 1524)))(CONNECT_DATA=(SERVER=DEDICATED)(SID=t1c3d598)))\\\"" +" control="+ctlFileNameWithPath+" log="+logFileNameWithPath; 
		    		log.info("::::::::::::::::::::::::::::::::::::SQL Loader Command :"+cmd);
		            rt = Runtime.getRuntime();
		            try {
		            	updateStatusPubEventTableCount(relese_db, SqlLoaderConstants.CPC_FILE_TO_STAGE_TABLE_START_STATUS, pubEventId, fileNameWithoutExtn);
		            	log.info("::::::::::::::Process Start Timestamp :"+new Date());
		            	Process prc = rt.exec(cmd);
		            	//Thread.sleep(1000);
		                log.info("::::::::::::::Process End Timestamp :"+new Date());
		                
		           //     prc.destroy();
		               boolean isDataLoaded = isAllFlatFileDataLoaded(relese_db, pubEventId, cpDataLoadId, fileNameWithoutExtn);
		               log.info("::::::::::::::isDataLoaded :"+isDataLoaded);
			            if(isDataLoaded) {
			            	
			            	prc.destroy();
			            }
		            }catch(Exception exception) {
		                exception.printStackTrace();
		            }
	    		}
	    	}
		else {
			throw new FileNotFoundException();
		}
    }
    private void loadUAT1Data(String relese_db, Long pubEventId, Long cpDataLoadId, String processingDir, String ctlFilesDestDir, SqlLoaderUtil sqlLoaderUtil) throws FileNotFoundException {
    	Runtime rt;
     //   Process prc;
        String cmd = "";
        log.info(":::::::::::::::::::::::::::: in loadST1Data table row count is null :" +totalLinesInFile);
        
        /*String pubEvntTblCntDataFileNameWithPath = processingDir+"/"+pubEventId+"_PUBLISHING_EVENT_COUNT.txt";
		log.info(":::::::::::::::::::::::::::: in loadST1Data ctlFileNameWithDir :"+pubEvntTblCntDataFileNameWithPath);
		
		File pubEvntTblCntDataFile = new File(pubEvntTblCntDataFileNameWithPath);
		if(pubEvntTblCntDataFile.exists() && !pubEvntTblCntDataFile.isDirectory()) {*/
        if(totalLinesInFile != 0 ) {
		
			/*for(int i=2; i<=SqlLoaderConstants.PUB_EVENT_COUNT_FILE_LINE_COUNT; i++) {
				String lineText = sqlLoaderUtil.getLineFromFile(i, processingDir+"/"+pubEventId+"_PUBLISHING_EVENT_COUNT.txt");
		    	log.info("::::::::::::::::::::::::::::: in loadST1Data data received in PUBLISHING_EVENT_COUNT.txt file :"+lineText);
		    	String[] items = lineText.split("\\|");
		    	
		    	if((items[0] == null) || ("".equals(items[0])) || (!pubEventId.toString().equalsIgnoreCase(items[0]))) {
		    		log.error(":::::::::::::::::::::::::::: in loadST1Data published event id is null :"+items[0]);
		    		throw new NullPointerException();
		    	}else if((items[1] == null) || ("".equals(items[1]))) {
		    		log.error(":::::::::::::::::::::::::::: in loadST1Data table name is null :"+items[1]);
		    		throw new NullPointerException();
		    	}else if((items[2] == null) || ("".equals(items[2]))) {
		    		log.error(":::::::::::::::::::::::::::: in loadST1Data table row count is null :"+items[2]);
		    		throw new NullPointerException();
		    	}
		    	
		    	PublishingEventTableCount pubEventTableCount = new PublishingEventTableCount();
		    	pubEventTableCount.setPubEventId(new Long(items[0]));
		    	pubEventTableCount.setTableName(items[1]);
		    	pubEventTableCount.setTableRowCount(new Long(items[2]));
		    	pubEventTableCount.setModUserId(SqlLoaderConstants.CPC_PUB_EVENT_CREATED_USER_ID);
		    	pubEventTableCount.setCreatedUserId(SqlLoaderConstants.CPC_PUB_EVENT_CREATED_USER_ID);
		    	
		    	log.info("::::::::::::::::::::::::::::: in loadST1Data Inserting a record into PUBLISHING_EVENT_TABLECOUNT :"+pubEventTableCount.toString());
		    	uat1PublishingEventTableCountRepo.save(pubEventTableCount);
		    	log.info("::::::::::::::::::::::::::::: in loadST1Data Inserting a record into PUBLISHING_EVENT_TABLECOUNT completed :"+pubEventTableCount.toString());
			}*/
			
		/*	Long pubEventTblCntCount = uat1PublishingEventTableCountRepo.pubEventTableCntCount(pubEventId);
			if((SqlLoaderConstants.PUB_EVENT_COUNT_FILE_LINE_COUNT-1) == pubEventTblCntCount.intValue()) {
				
			}
	        */
        	PublishingEventTableCount pubEventTableCount = new PublishingEventTableCount();
	    	pubEventTableCount.setPubEventId(pubEventId);
	    	pubEventTableCount.setTableName(pubEventId+"EDF_TransportType");
	    	pubEventTableCount.setTableRowCount(new Long(totalLinesInFile-1));
	    	pubEventTableCount.setModUserId(SqlLoaderConstants.CPC_PUB_EVENT_CREATED_USER_ID);
	    	pubEventTableCount.setCreatedUserId(SqlLoaderConstants.CPC_PUB_EVENT_CREATED_USER_ID);
	    	log.info("::::::::::::::::::::::::::::: in loadST1Data Inserting a record into PUBLISHING_EVENT_TABLECOUNT :"+pubEventTableCount.toString());
	    	uat1PublishingEventTableCountRepo.save(pubEventTableCount);
	    	log.info("::::::::::::::::::::::::::::: in loadST1Data Inserting a record into PUBLISHING_EVENT_TABLECOUNT completed :"+pubEventTableCount.toString());
	        
	    	for(File file : sqlLoaderUtil.getFilesFromDirectory(ctlFilesDestDir)) {
	    		String fileNameWithoutExtn = file.getName().substring(0, (file.getName().length()-4));
	    		log.info(":::::::::::::::::::::::::::: in loadUAT1Data fileNameWithoutExtn :"+fileNameWithoutExtn);
	    		//if(!"TS_PC_PUBLISHING_EVENT_COUNT".equalsIgnoreCase(fileNameWithoutExtn)) {
		    		String ctlFileNameWithPath = ctlFilesDestDir+"/"+file.getName();
		    		String logFileNameWithPath = SqlLoaderConstants.LOG_FILE_DIR_TEST+"/"+fileNameWithoutExtn+".log";
		    		log.info(":::::::::::::::::::::::::::: in loadUAT1Data ctlFileNameWithPath :"+ctlFileNameWithPath);
		    		log.info(":::::::::::::::::::::::::::: in loadUAT1Data logFileNameWithPath :"+logFileNameWithPath);
		    		
		    		//cmd ="sqlldr sce/"+this.uat1_db_pwd+"@"+SqlLoaderConstants.TNS_ENTRY_UAT1+" control="+ctlFileNameWithPath+" log="+logFileNameWithPath;
		    		 cmd = "sqlldr userid=SCE/"+this.uat1_db_pwd+"@" +"\\\"(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=ZLT05504.vci.lokesh.com)(PORT= 1524)))(CONNECT_DATA=(SERVER=DEDICATED)(SID=t1c3d603)))\\\"" +" control="+ctlFileNameWithPath+" log="+logFileNameWithPath; 
		    		log.info("::::::::::::::::::::::::::::::::::::SQL Loader Command :"+cmd);
		            rt = Runtime.getRuntime();
		            try {
		            	updateStatusPubEventTableCount(relese_db, SqlLoaderConstants.CPC_FILE_TO_STAGE_TABLE_START_STATUS, pubEventId, fileNameWithoutExtn);
		            	log.info("::::::::::::::Process Start Timestamp :"+new Date());
		                prc = rt.exec(cmd);
		                log.info("::::::::::::::Process End Timestamp :"+new Date());
		            }catch(Exception exception) {
		                exception.printStackTrace();
		            }
		            
		         Boolean isDataLoaded = isAllFlatFileDataLoadedToUAT1(relese_db, pubEventId, cpDataLoadId, fileNameWithoutExtn);
		            if(isDataLoaded) {
		            	
		            	prc.destroy();
		            }
	    		//}
	    	}
		}else {
			throw new FileNotFoundException();
		}
    }
    private void updateStatusInPubEventTable(String release_db, String event_id, Long status_id) {
    	if(event_id != null && !"".equals(event_id) ) {
    		if("ST1".equalsIgnoreCase(release_db)) {
    			PublishingEvent st1PublishingEvent = st1PublishingEventRepository.findById(new Long(event_id));
    			st1PublishingEvent.setStatusId(status_id);
    			st1PublishingEventRepository.save(st1PublishingEvent);    			
    		}else if("UAT1".equalsIgnoreCase(release_db)) {
    			PublishingEvent uat1PublishingEvent = uat1PublishingEventRepository.findById(new Long(event_id));
    			uat1PublishingEvent.setStatusId(status_id);
    			uat1PublishingEventRepository.save(uat1PublishingEvent);    			
    		}
    	}
    }
    private String moveDataStageToTargetST1(Long cpDataLoadId) {
    	log.info("::::::::::::::::::::::::::::::::::::: In moveDataStageToTargetST1");
    	String loadingTargetTablesStatus = "FAIL";
    	Connection connection = null;
        CallableStatement statement = null;
        
        try {
            connection = dataSource.getConnection();
           // statement = connection.prepareCall("{call PKG_CPC_EIS_STG_TGT_LOAD.PROC_CPC_EIS_STG_TGT_LOAD(?,?,?)}");
            statement = connection.prepareCall("{call IPFR_STG_TGT_LOAD_PRC(?,?)}");
            //IPFR_STG_TGT_LOAD_PRC(IN_CP_DATA_LOAD_ID IN VARCHAR2, OUT_RETUN_STATUS OUT VARCHAR2)


            statement.setLong(1, cpDataLoadId);
           // statement.setLong(2, pubEventIdDomain);
            //statement.registerOutParameter(3, java.sql.Types.VARCHAR);
            statement.registerOutParameter(2, java.sql.Types.VARCHAR);
            log.info("::::::::::::::::::::::::::::::::::::: In moveDataStageToTargetST1" +cpDataLoadId+" "+java.sql.Types.VARCHAR);
            statement.execute();
            
           // loadingTargetTablesStatus = statement.getString(3); //status
            
            loadingTargetTablesStatus = statement.getString(2); //status
            log.info("::::::::::::::::::::::::::::::::::::: In moveDataStageToTargetST1  loadingTargetTablesStatus : "+loadingTargetTablesStatus);
            statement.close();
            connection.close();
        } catch (SQLException e) {
        	log.error(e.getLocalizedMessage(), e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.error(":::::::::::::::::::::::::::::::::::::Unable to close statement", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error(":::::::::::::::::::::::::::::::::::::::::Unable to close connection", e);
                }
            }
        }
        
        return loadingTargetTablesStatus;
    }
    
  /*  private String moveDataStageToTargetUAT1(Long cpDataLoadId, Long pubEventIdDomain) {*/
    private String moveDataStageToTargetUAT1(Long cpDataLoadId) {
    	String loadingTargetTablesStatus = "FAIL";
    	Connection connection = null;
        CallableStatement statement = null;
        
        try {
          connection = uat1RepositoryConfig.uat1DataSource().getConnection();
           // connection = uat1DataSource.getConnection();
            
          
            log.info("comnection detaials"+connection);
           // statement = connection.prepareCall("{call PKG_CPC_EIS_STG_TGT_LOAD.PROC_CPC_EIS_STG_TGT_LOAD(?,?,?)}");
            statement = connection.prepareCall("{call IPFR_STG_TGT_LOAD_PRC(?,?)}");
            statement.setLong(1, cpDataLoadId);
          //  statement.setLong(2, pubEventIdDomain);
          //  statement.registerOutParameter(3, java.sql.Types.VARCHAR);
            statement.registerOutParameter(2, java.sql.Types.VARCHAR);
            statement.execute();
           
            
          //  loadingTargetTablesStatus = statement.getString(3); //status
            loadingTargetTablesStatus = statement.getString(2);
            log.info("Target PL/Sql executed"+connection);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.error(":::::::::::::::::::::::::::::::::::::Unable to close statement", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error(":::::::::::::::::::::::::::::::::::::::::Unable to close connection", e);
                }
            }
        }
        
        return loadingTargetTablesStatus;
    }
    private void updateStatusPubEventTableCount(String release_db, Long status, Long pubEventId, String fileNameWithoutExtn) {
    	String dataFileName = pubEventId.toString()+fileNameWithoutExtn;
    	log.info("::::::::::::::::::::::::::::::::::::Updating CP_PUBLISHING_EVENT_TABLECOUNT with the status :"+status+" for event id : "+pubEventId+" and file name : "+dataFileName);    	
    	
    	if("ST1".equalsIgnoreCase(release_db)) {
	    	PublishingEventTableCount pubEventTableCount = st1PublishingEventTableCountRepo.findPubEventTableCntForTable(pubEventId, dataFileName);
			if(pubEventTableCount != null && pubEventTableCount.getTableRowCount() != null) {
				pubEventTableCount.setStatusId(status);
				log.info("::::::::::::::::::::::::::::::::::::Updating CP_PUBLISHING_EVENT_TABLECOUNT : "+pubEventTableCount.toString());
				st1PublishingEventTableCountRepo.save(pubEventTableCount);
			}
    	}else if("UAT1".equalsIgnoreCase(release_db)) {
    		PublishingEventTableCount pubEventTableCount = uat1PublishingEventTableCountRepo.findPubEventTableCntForTable(pubEventId, dataFileName);
			if(pubEventTableCount != null && pubEventTableCount.getTableRowCount() != null) {
				pubEventTableCount.setStatusId(status);
				log.info("::::::::::::::::::::::::::::::::::::Updating CP_PUBLISHING_EVENT_TABLECOUNT : "+pubEventTableCount.toString());
				uat1PublishingEventTableCountRepo.save(pubEventTableCount);
			}
    	}
    }
   /* private boolean isAllFlatFileDataLoaded(String relese_db, Long pubEventId, Long cpDataLoadId, String fileNameWithoutExtn) {
    	boolean isDataLoaded = false;
    	String dataFileName = pubEventId.toString()+fileNameWithoutExtn.substring(5)+".txt";
		
		PublishingEventTableCount pubEventTableCount = st1PublishingEventTableCountRepo.findPubEventTableCntForTable(pubEventId, dataFileName);
		if(pubEventTableCount != null && pubEventTableCount.getTableRowCount() != null) {
	        int recCountInPubEvntTblCntTable = pubEventTableCount.getTableRowCount().intValue();
	        log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoaded recCountInPubEvntTblCntTable :"+recCountInPubEvntTblCntTable);
			
	        if("VISHNU_TEST".equalsIgnoreCase(fileNameWithoutExtn)) {
	        	
	        	//Long stageTblCount = stVishnuTestRepository.getRowCount(pubEventId, cpDataLoadId);
	        	Long stageTblCount = stVishnuTestRepository.getRowCount();
	        	
	        	log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoaded stageTblCount before for loop :"+stageTblCount);
		        for(int i=recCountInPubEvntTblCntTable; i>stageTblCount; ) {
		        //	stageTblCount = stVishnuTestRepository.getRowCount(pubEventId, cpDataLoadId);
		        	stageTblCount = stVishnuTestRepository.getRowCount();
		        	log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoaded stageTblCount in for loop: "+stageTblCount);
		        }
		        log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoaded stageTblCount after for loop: "+stageTblCount);
		        
		        if(recCountInPubEvntTblCntTable == stageTblCount) {
		        	updateStatusPubEventTableCount(relese_db, SqlLoaderConstants.CPC_FILE_TO_STAGE_TABLE_END_STATUS, pubEventId, fileNameWithoutExtn);
		        	isDataLoaded = true;
		        }
	        }
	        
		}
		
		return isDataLoaded;
    }*/                                                                                                   //control file name 
    private boolean isAllFlatFileDataLoaded(String relese_db, Long pubEventId, Long cpDataLoadId, String fileNameWithoutExtn) {
    	boolean isDataLoaded = false;
    	String dataFileName = pubEventId.toString()+fileNameWithoutExtn;
		log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoaded dataFileName :"+dataFileName);
			
		PublishingEventTableCount pubEventTableCount = st1PublishingEventTableCountRepo.findPubEventTableCntForTable(pubEventId, dataFileName);
		log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoaded pubEventTableCount :"+pubEventTableCount);
		
		if(pubEventTableCount != null && pubEventTableCount.getTableRowCount() != null) {
	        int recCountInPubEvntTblCntTable = pubEventTableCount.getTableRowCount().intValue();
	        log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoaded recCountInPubEvntTblCntTable :"+recCountInPubEvntTblCntTable);
			
	        if("EDF_TransportType".equalsIgnoreCase(fileNameWithoutExtn)) {
	        	
	        	//Long stageTblCount = stBvoipCsiTransportTypeRepo.getRowCount(pubEventId, cpDataLoadId);
	        	//Long stageTblCount = stVishnuTestRepository.getRowCount();
	        	Long stageTblCount = stBvoipCsiTransportTypeRepo.getRowCount(cpDataLoadId);
	        	
	        	log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoaded stageTblCount before for loop :"+stageTblCount);
		        for(int i=recCountInPubEvntTblCntTable; i>stageTblCount; ) {
		        	stageTblCount = stBvoipCsiTransportTypeRepo.getRowCount(cpDataLoadId);
		        	//stageTblCount = stBvoipCsiTransportTypeRepo.getRowCount(pubEventId, cpDataLoadId);
		        	//stageTblCount = stVishnuTestRepository.getRowCount();
		        	log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoaded stageTblCount in for loop: "+stageTblCount);
		        }
		        log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoaded stageTblCount after for loop: "+stageTblCount);
		        
		        if(recCountInPubEvntTblCntTable == stageTblCount) {
					log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoaded calling updateStatusPubEventTableCount :");
		
		        	updateStatusPubEventTableCount(relese_db, SqlLoaderConstants.CPC_FILE_TO_STAGE_TABLE_END_STATUS, pubEventId, fileNameWithoutExtn);
		        	isDataLoaded = true;
		        }
	        }
	        
		}
		
		return isDataLoaded;
    }
 private boolean isAllFlatFileDataLoadedToUAT1(String relese_db, Long pubEventId, Long cpDataLoadId, String fileNameWithoutExtn) {
	 boolean isDataLoaded = false;
    	//String dataFileName = pubEventId.toString()+fileNameWithoutExtn.substring(5)+".txt";
	 String dataFileName = pubEventId.toString()+fileNameWithoutExtn;
    	log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoadedToUAT1 dataFileName :"+dataFileName);
		PublishingEventTableCount pubEventTableCount = uat1PublishingEventTableCountRepo.findPubEventTableCntForTable(pubEventId, dataFileName);
		if(pubEventTableCount != null && pubEventTableCount.getTableRowCount() != null) {
	        int recCountInPubEvntTblCntTable = pubEventTableCount.getTableRowCount().intValue();
	        log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoadedToUAT1ToUAT1 recCountInPubEvntTblCntTable :"+recCountInPubEvntTblCntTable);
			
	        if("EDF_TransportType".equalsIgnoreCase(fileNameWithoutExtn)) {
	        	
	        	//Long stageTblCount = uatBvoipCsiTransportTypeRepo.getRowCount(pubEventId, cpDataLoadId);
	        	//Long stageTblCount = uat1VishnuTestRepository.getRowCount();
	        	Long stageTblCount = uatBvoipCsiTransportTypeRepo.getRowCount(cpDataLoadId);
	        	
	        	log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoadedToUAT1 stageTblCount before for loop :"+stageTblCount);
		        for(int i=recCountInPubEvntTblCntTable; i>stageTblCount; ) {
		        	//stageTblCount = uatBvoipCsiTransportTypeRepo.getRowCount(pubEventId, cpDataLoadId);
		        	//stageTblCount = uat1VishnuTestRepository.getRowCount();
		        	stageTblCount = uatBvoipCsiTransportTypeRepo.getRowCount(cpDataLoadId);
		        	log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoadedToUAT1 stageTblCount in for loop: "+stageTblCount);
		        }
		        log.info(":::::::::::::::::::::::::::: in isAllFlatFileDataLoadedToUAT1 stageTblCount after for loop: "+stageTblCount);
		        
		        if(recCountInPubEvntTblCntTable == stageTblCount) {
		        	updateStatusPubEventTableCount(relese_db, SqlLoaderConstants.CPC_FILE_TO_STAGE_TABLE_END_STATUS, pubEventId, fileNameWithoutExtn);
		        	 isDataLoaded = true;
		        }
	        }
	        
		}
		return isDataLoaded;
    }
  
    
    
}
    
    