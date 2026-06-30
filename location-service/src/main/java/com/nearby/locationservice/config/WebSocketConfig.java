package com.nearby.locationservice.config;

import com.nearby.locationservice.handler.LocationWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
  private final LocationWebSocketHandler handler;

  public WebSocketConfig(LocationWebSocketHandler handler){
    this.handler = handler;

  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
    registry.addHandler(handler, "/ws/location")
            .setAllowedOrigins("*");
  }
}
