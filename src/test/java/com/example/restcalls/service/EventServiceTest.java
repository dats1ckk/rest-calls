package com.example.restcalls.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EventServiceTest {

    @Test
    void testUpdateAndRetrieveEventStatus() {
        EventService eventService = new EventService();

        // Initially, no events
        assertTrue(eventService.getLiveEvents().isEmpty());
        assertFalse(eventService.isEventLive("event1"));

        // Add event1 as live
        eventService.updateEventStatus("event1", true);
        assertTrue(eventService.isEventLive("event1"));
        assertEquals(1, eventService.getLiveEvents().size());
        assertTrue(eventService.getLiveEvents().containsKey("event1"));

        // Add event2 as live
        eventService.updateEventStatus("event2", true);
        assertTrue(eventService.isEventLive("event2"));
        assertEquals(2, eventService.getLiveEvents().size());
        assertTrue(eventService.getLiveEvents().containsKey("event1"));
        assertTrue(eventService.getLiveEvents().containsKey("event2"));

        // Update event1 to not live
        eventService.updateEventStatus("event1", false);
        assertFalse(eventService.isEventLive("event1"));
        assertEquals(1, eventService.getLiveEvents().size());
        assertFalse(eventService.getLiveEvents().containsKey("event1"));
        assertTrue(eventService.getLiveEvents().containsKey("event2"));


        // Update event2 to not live
        eventService.updateEventStatus("event2", false);
        assertFalse(eventService.isEventLive("event2"));
        assertTrue(eventService.getLiveEvents().isEmpty());

        // Check non-existent event
        assertFalse(eventService.isEventLive("event3"));
    }
}
