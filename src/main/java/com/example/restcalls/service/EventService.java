package com.example.restcalls.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private final Map<String, Boolean> eventStatuses = new ConcurrentHashMap<>();

    public void updateEventStatus(String eventId, boolean isLive) {
        logger.info("Event {} status updated to {}", eventId, isLive ? "LIVE" : "NOT LIVE");
        eventStatuses.put(eventId, isLive);
    }

    public Map<String, Boolean> getLiveEvents() {
        // Logging at INFO level for now, consider DEBUG if too verbose for frequent calls
        logger.info("Fetching all live events. Current eventStatuses size: {}", eventStatuses.size());
        return eventStatuses.entrySet().stream()
                .filter(Map.Entry::getValue)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean isEventLive(String eventId) {
        boolean isLive = eventStatuses.getOrDefault(eventId, false);
        // Logging at INFO level for now, consider DEBUG if too verbose for frequent calls
        logger.info("Checking liveness for event {}: {}", eventId, isLive ? "LIVE" : "NOT LIVE");
        return isLive;
    }
}
