package com.lokesh.ssdfedf.repository.uat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lokesh.ssdfedf.domain.PublishingEvent;

/**
 * @author Vishnu Kondam
 *
 */

@Repository
public interface UAT1PublishingEventRepository extends PagingAndSortingRepository<PublishingEvent, Long> {

    PublishingEvent findById(Long id);

    PublishingEvent findFirstByOrderByIdDesc();
    
    @Query("SELECT COUNT(cpe) FROM PublishingEvent cpe")
    Long pubEventCount();
    
    PublishingEvent save(PublishingEvent publishingEvent);


}
