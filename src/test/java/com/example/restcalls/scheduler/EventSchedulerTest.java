package com.example.restcalls.scheduler;

import com.example.restcalls.dto.MockApiResponse;
import com.example.restcalls.service.EventService;
import com.example.restcalls.service.MessageProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventSchedulerTest {

    @Mock
    private EventService eventService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MessageProducerService messageProducerService;

    @InjectMocks
    private EventScheduler eventScheduler;

    @Test
    void testFetchLiveEventData_NoLiveEvents() {
        when(eventService.getLiveEvents()).thenReturn(Collections.emptyMap());

        eventScheduler.fetchLiveEventData();

        verify(restTemplate, never()).getForObject(anyString(), any());
        verify(messageProducerService, never()).sendMessage(anyString(), any(MockApiResponse.class));
    }

    @Test
    void testFetchLiveEventData_OneLiveEvent_Success() {
        Map<String, Boolean> liveEvents = Collections.singletonMap("event1", true);
        when(eventService.getLiveEvents()).thenReturn(liveEvents);

        MockApiResponse apiResponse = new MockApiResponse("event1", "1:0");
        when(restTemplate.getForObject(eq("http://localhost:8080/events/mockapi/event/event1"), eq(MockApiResponse.class)))
                .thenReturn(apiResponse);

        eventScheduler.fetchLiveEventData();

        verify(restTemplate, times(1)).getForObject("http://localhost:8080/events/mockapi/event/event1", MockApiResponse.class);
        verify(messageProducerService, times(1)).sendMessage("live_event_updates", apiResponse);
    }

    @Test
    void testFetchLiveEventData_MultipleLiveEvents_OneSuccessOneNotLiveInMap() {
        Map<String, Boolean> liveEvents = new HashMap<>();
        liveEvents.put("event1", true);
        liveEvents.put("event2", false); // This event is in the map but marked as not live
        when(eventService.getLiveEvents()).thenReturn(liveEvents);

        MockApiResponse apiResponseEvent1 = new MockApiResponse("event1", "1:0");
        when(restTemplate.getForObject(eq("http://localhost:8080/events/mockapi/event/event1"), eq(MockApiResponse.class)))
                .thenReturn(apiResponseEvent1);
        // Note: EventService.getLiveEvents() should ideally only return truly live events.
        // This test handles the case where the scheduler's loop might re-check liveness.

        eventScheduler.fetchLiveEventData();

        verify(restTemplate, times(1)).getForObject("http://localhost:8080/events/mockapi/event/event1", MockApiResponse.class);
        verify(messageProducerService, times(1)).sendMessage("live_event_updates", apiResponseEvent1);
        // Ensure no calls for event2
        verify(restTemplate, never()).getForObject(eq("http://localhost:8080/events/mockapi/event/event2"), eq(MockApiResponse.class));
        verify(messageProducerService, never()).sendMessage(anyString(), argThat(response -> "event2".equals(response.getEventId())) );

    }


    @Test
    void testFetchLiveEventData_ApiCallFails() {
        Map<String, Boolean> liveEvents = Collections.singletonMap("event2", true);
        when(eventService.getLiveEvents()).thenReturn(liveEvents);

        when(restTemplate.getForObject(eq("http://localhost:8080/events/mockapi/event/event2"), eq(MockApiResponse.class)))
                .thenThrow(new RestClientException("API down"));

        eventScheduler.fetchLiveEventData();

        verify(restTemplate, times(1)).getForObject("http://localhost:8080/events/mockapi/event/event2", MockApiResponse.class);
        verify(messageProducerService, never()).sendMessage(anyString(), any(MockApiResponse.class));
        // Verification of error logging would require a logging test framework
    }

    @Test
    void testFetchLiveEventData_NullResponseFromApi() {
        Map<String, Boolean> liveEvents = Collections.singletonMap("event3", true);
        when(eventService.getLiveEvents()).thenReturn(liveEvents);

        when(restTemplate.getForObject(eq("http://localhost:8080/events/mockapi/event/event3"), eq(MockApiResponse.class)))
                .thenReturn(null); // Simulate API returning null

        eventScheduler.fetchLiveEventData();

        verify(restTemplate, times(1)).getForObject("http://localhost:8080/events/mockapi/event/event3", MockApiResponse.class);
        verify(messageProducerService, never()).sendMessage(anyString(), any(MockApiResponse.class));
        // Verification of warning logging for null response would require a logging test framework
    }
}
