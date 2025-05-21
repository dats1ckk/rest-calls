package com.example.restcalls.scheduler;

import com.example.restcalls.dto.MockApiResponse;
import com.example.restcalls.service.EventService;
import com.example.restcalls.service.MessageProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class EventScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EventScheduler.class);

    private final EventService eventService;
    private final RestTemplate restTemplate;
    private final MessageProducerService messageProducerService;

    public EventScheduler(EventService eventService, RestTemplate restTemplate, MessageProducerService messageProducerService) {
        this.eventService = eventService;
        this.restTemplate = restTemplate;
        this.messageProducerService = messageProducerService;
    }

    @Scheduled(fixedRate = 10000)
    public void fetchLiveEventData() {
        logger.info("Starting scheduled event processing run...");
        Map<String, Boolean> liveEvents = eventService.getLiveEvents();
        logger.info("Found {} live events to process in this run.", liveEvents.size());

        for (Map.Entry<String, Boolean> entry : liveEvents.entrySet()) {
            String eventId = entry.getKey();
            // The getLiveEvents() method already filters for live events,
            // but an explicit check here is a good safeguard.
            if (Boolean.TRUE.equals(entry.getValue())) {
                logger.info("Processing live event: {}", eventId);
                String url = "http://localhost:8080/events/mockapi/event/" + eventId;
                try {
                    MockApiResponse response = restTemplate.getForObject(url, MockApiResponse.class);
                    if (response != null) {
                        logger.info("Successfully fetched data for event {}: Score {}", response.getEventId(), response.getCurrentScore());
                        messageProducerService.sendMessage("live_event_updates", response);
                    } else {
                        logger.warn("Received null response from mock API for event: {}. URL: {}", eventId, url);
                    }
                } catch (RestClientException e) {
                    logger.error("Error calling mock API for event {}. URL: {}. Error: {}", eventId, url, e.getMessage());
                }
            } else {
                // This case should ideally not be reached if getLiveEvents() is accurate.
                logger.warn("Event {} was in the processing list but is not marked as live. Skipping.", eventId);
            }
        }
        logger.info("Finished scheduled event processing run.");
    }
}
