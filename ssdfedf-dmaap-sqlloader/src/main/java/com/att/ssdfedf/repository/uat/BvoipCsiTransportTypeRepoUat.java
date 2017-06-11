package com.lokesh.lokeshedf.repository.uat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;



import com.lokesh.lokeshedf.domain.BvoipCsiTransportType;

public interface BvoipCsiTransportTypeRepoUat extends PagingAndSortingRepository<BvoipCsiTransportType, Long>  {
	/*@Query("SELECT COUNT(domain) FROM BvoipCsiTransportType domain WHERE domain.publishedEventId = :publishedEventId and domain.cpDataLoadId = :cpDataLoadId")
    Long getRowCount(@Param("publishedEventId") Long publishedEventId, @Param("cpDataLoadId") Long cpDataLoadId);*/
	@Query("SELECT COUNT(domain) FROM BvoipCsiTransportType domain WHERE domain.cpDataLoadId = :cpDataLoadId")
	Long getRowCount(@Param("cpDataLoadId") Long cpDataLoadId);
}
