package com.nearby.locationservice.service;

import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;


@Service
public class LocationService{
  private static final String GEO_KEY = "user-locations";
  private final RedisTemplate<String,String> redis;
  
  public LocationService(RedisTemplate<String, String> redis){
    this.redis = redis;
  }

  public void updateLocation(String userId,double lat, double lng){
    // Setting up call for current point by user ID given in longitude:latitude order
    redis.opsForGeo().add(GEO_KEY, new Point(lng,lat), userId);
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



}
