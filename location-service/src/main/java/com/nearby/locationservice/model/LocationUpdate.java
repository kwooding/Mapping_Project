package com.nearby.locationservice.model;

public record LocationUpdate(
    String userId,
    double latitude,
    double longitude
    ){}
