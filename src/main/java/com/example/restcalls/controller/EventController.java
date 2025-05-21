package com.example.restcalls.controller;

import com.example.restcalls.dto.EventStatusUpdate;
import com.example.restcalls.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.restcalls.dto.MockApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/events")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Mock API endpoint
    @GetMapping("/mockapi/event/{eventId}")
    public ResponseEntity<MockApiResponse> getMockEventData(@PathVariable String eventId) {
        logger.info("Mock API endpoint /mockapi/event/{} called.", eventId);

        if (eventId == null || eventId.isEmpty()) {
            logger.warn("Mock API called with null or empty eventId.");
            return ResponseEntity.badRequest().build();
        }

        // Simulate fetching data for the eventId
        // For now, returning a random score
        Random random = new Random();
        String randomScore = random.nextInt(5) + ":" + random.nextInt(5);
        MockApiResponse response = new MockApiResponse(eventId, randomScore);

        logger.info("Returning mock response for eventId {}: Score {}", eventId, response.getCurrentScore());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/status")
    public ResponseEntity<Void> updateEventStatus(@RequestBody EventStatusUpdate eventStatusUpdate) {
        if (eventStatusUpdate == null) {
            logger.warn("Received null event status update in POST /events/status.");
            return ResponseEntity.badRequest().build();
        }

        String eventId = eventStatusUpdate.getEventId();
        boolean isLive = eventStatusUpdate.isLive();
        logger.info("Received request to update event status: eventId='{}', new status='{}'", eventId, isLive ? "LIVE" : "NOT LIVE");

        if (eventId == null || eventId.isEmpty()) {
            logger.warn("Received event status update with null or empty eventId. Request: {}", eventStatusUpdate);
            return ResponseEntity.badRequest().build();
        }

        eventService.updateEventStatus(eventId, isLive);
        logger.info("Event status updated successfully for eventId: {}. New status: {}", eventId, isLive ? "LIVE" : "NOT LIVE");
        return ResponseEntity.ok().build();
    }
}
