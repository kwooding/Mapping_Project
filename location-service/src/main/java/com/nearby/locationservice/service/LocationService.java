//LocationService.java 
package com.nearby.locationservice.service;

import org.springframework.data.geo.*;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import com.nearby.locationservice.model.LocationHistory;
import com.nearby.locationservice.model.LocationHistoryResponse;
import com.nearby.locationservice.repository.LocationHistoryRepository;
import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;


@Service
public class LocationService{
  private static final String GEO_KEY = "user-locations";
  private final RedisTemplate<String,String> redis;
  private final LocationHistoryRepository historyRepository;
  // Creating our location distance calculator using pre defined model 
  private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

  public LocationService(RedisTemplate<String, String> redis, LocationHistoryRepository historyRepository){
    this.redis = redis;
    this.historyRepository = historyRepository;
  }

  public void updateLocation(String userId,double lat, double lng){
    // Setting up call for current point by user ID given in longitude:latitude order
    redis.opsForGeo().add(GEO_KEY, new org.springframework.data.geo.Point(lng,lat), userId);

    Point p = makePoint(lat,lng);

    LocationHistory user = new LocationHistory(userId,p,Instant.now());

    historyRepository.save(user);
  }

  public List<String> findNearby(String userId, double radiusKm){
    Distance radius = new Distance(radiusKm, Metrics.KILOMETERS);
    GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                              redis.opsForGeo().search(GEO_KEY, GeoReference.fromMember(userId),radius);
    
    List<String> idList = new ArrayList<>();
    for (var result : results ){
      String userString = result.getContent().getName();
      idList.add(userString);
    }
    return idList;
  }

  //Using the built in SRID for GPS coordinates for the points of each user
  
  private Point makePoint(double lat,double lng){
    Point point = geometryFactory.createPoint(new Coordinate(lng,lat));
    point.setSRID(4326);
    return point;
  }
  

  public List<LocationHistoryResponse> getHistory(String userId, int sinceMinutes){
    Instant since = Instant.now().minus(Duration.ofMinutes(sinceMinutes));

    List<LocationHistory> historyList = historyRepository.findByUserIdAndRecordedAtAfterOrderByRecordedAtAsc(userId,since);
    List<LocationHistoryResponse> responses = new ArrayList<>();
    for ( LocationHistory entity : historyList){
        LocationHistoryResponse response = new LocationHistoryResponse(
            entity.getUserId(),
            entity.getLocation().getY(),
            entity.getLocation().getX(),
            entity.getRecordedAt()
            );

        responses.add(response);
    }
    return responses; 
  }


}
