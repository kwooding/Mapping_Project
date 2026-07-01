package com.nearby.locationservice.model;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;
import java.time.Instant;

@Entity
@Table(name = "location_history")
public class LocationHistory{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String userId;

  @Column(columnDefinition = "geography(Point,4326)")
  private Point location;


  @Column(nullable = false)
  private Instant recordedAt;

  public LocationHistory(String userId, Point location, Instant recordedAt){
    this.userId = userId;
    this.location = location;
    this.recordedAt = recordedAt;
  }

  public Long getId(){
    return id;
  }
  public String getUserId(){
    return userId;
  }
  
  public Point getLocation(){
    return location;
  }
  public Instant getRecordedAt(){
    return recordedAt;
  }
}

