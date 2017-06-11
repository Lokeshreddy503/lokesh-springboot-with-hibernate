package com.lokesh.ssdfedf.repository.uat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lokesh.ssdfedf.domain.PublishingEventTableCount;

/**
 * @author Vishnu Kondam
 *
 */

@Repository
public interface UAT1PublishingEventTableCountRepo extends PagingAndSortingRepository<PublishingEventTableCount, Long> {

	PublishingEventTableCount findById(Long id);

	PublishingEventTableCount findFirstByOrderByIdDesc();
    
	//Long countById();
    
	@Query("SELECT COUNT(petc) FROM PublishingEventTableCount petc WHERE petc.pubEventId = :pubEventId")
    Long pubEventTableCntCount(@Param("pubEventId") Long pubEventId);
    
    @Query("SELECT petc FROM PublishingEventTableCount petc WHERE LOWER(petc.tableName) = LOWER(:tableName) and petc.pubEventId = :pubEventId")
    PublishingEventTableCount findPubEventTableCntForTable(@Param("pubEventId") Long pubEventId, @Param("tableName") String tableName);
    
    PublishingEventTableCount save(PublishingEventTableCount pubEventTableCount);


}
