package com.nearby.locationservice.model;

import java.time.Instant;

public record LocationHistoryResponse(
    String userId,
    double latitude,
    double longitude,
    Instant recordedAt
    ){}
