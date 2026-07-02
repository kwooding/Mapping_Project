package com.nearby.locationservice.controller;

import com.nearby.locationservice.model.LocationUpdate;
import com.nearby.locationservice.service.LocationService;
import com.nearby.locationservice.model.LocationHistoryResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationController{
  private final LocationService locationService;

  public LocationController(LocationService locationService){
    this.locationService = locationService;
  }

  @PostMapping("/update")
  public void updateLocation(@RequestBody LocationUpdate update){
    locationService.updateLocation(update.userId(),update.latitude(),update.longitude());
  }


  @GetMapping("/nearby")
  public List<String> findNearby(@RequestParam String userId, @RequestParam double radiusKm){
    return locationService.findNearby(userId, radiusKm);
  }

  @GetMapping("/history")
  public List<LocationHistoryResponse> getHistory(
      @RequestParam String userId,
      @RequestParam(defaultValue = "60") int sinceMinutes){
        return locationService.getHistory(userId,sinceMinutes);
      }
}
