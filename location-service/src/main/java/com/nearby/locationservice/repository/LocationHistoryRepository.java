package com.nearby.locationservice.repository;

import com.nearby.locationservice.model.LocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;


public interface LocationHistoryRepository extends JpaRepository<LocationHistory,Long> {
  
  //This use the SQL query Select time from Location Where time > since Sort BY ASC
  List<LocationHistory> findByUserIdAndRecordedAtAfterOrderByRecordedAtAsc(
      String userId, Instant since 
      );
}
