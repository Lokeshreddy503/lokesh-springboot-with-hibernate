package com.lokesh.lokeshedf.repository.uat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lokesh.lokeshedf.domain.CpDataLoad;

/**
 * @author Vishnu Kondam
 *
 */

@Repository
public interface UAT1CpDataLoadRepository extends PagingAndSortingRepository<CpDataLoad, Long> {

	CpDataLoad findById(Long id);

	CpDataLoad findFirstByOrderByIdDesc();
    
    @Query("SELECT COUNT(cdl) FROM CpDataLoad cdl")
    Long cpDataLoadCount();
    
    CpDataLoad save(CpDataLoad cpDataLoad);


}
