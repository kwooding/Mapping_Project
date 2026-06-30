package com.nearby.locationservice.handler;

import com.nearby.locationservice.service.LocationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocationWebSocketHandler extends TextWebSocketHandler{

  private final LocationService locationservice;
  private final ObjectMapper objectMapper;
  private static final Logger log = LoggerFactory.getLogger(LocationWebSocketHandler.class);

  private final Map<String, Set<WebSocketSession>> subscribers = new ConcurrentHashMap<>();

  public LocationWebSocketHandler(LocationService locationservice, ObjectMapper objectMapper){
    this.locationservice = locationservice;
    this.objectMapper = objectMapper;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session){
    log.info("{} has connected ", session.getId());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
      String text = message.getPayload();
      JsonNode node = objectMapper.readTree(text);
      String type = node.get("type").asText();
      if ("subscribe".equals(type)){
        String targetUserId = node.get("targetUserId").asText();
        subscribers.computeIfAbsent(targetUserId, key -> ConcurrentHashMap.newKeySet())
                    .add(session);
      }else if ("update".equals(type)){
        String userId = node.get("userId").asText();
        double latitude = node.get("latitude").asDouble();
        double longitude = node.get("longitude").asDouble();

        locationservice.updateLocation(userId,latitude,longitude);
        broadcastToSubscribers(userId,text);
      }else{
        log.error("{} is an Unknown message type", type);
      }


  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
    for (Set<WebSocketSession> sessionSet : subscribers.values()){
       sessionSet.remove(session);
    }
  }

  private void broadcastToSubscribers(String userId, String jsonPayload) throws Exception{
      Set<WebSocketSession> watchers = subscribers.get(userId);

      if (watchers == null){
        log.info("There is no one else in this session");
        return;
      }

      for (WebSocketSession w : watchers){
        if (w.isOpen()){
          w.sendMessage(new TextMessage(jsonPayload));
        }
      }
  }
}
