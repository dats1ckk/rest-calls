package com.example.restcalls.controller;

import com.example.restcalls.dto.EventStatusUpdate;
import com.example.restcalls.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @Test
    void testUpdateEventStatus_Success() throws Exception {
        EventStatusUpdate update = new EventStatusUpdate();
        update.setEventId("evt1");
        update.setLive(true);

        mockMvc.perform(post("/events/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        verify(eventService, times(1)).updateEventStatus("evt1", true);
    }

    @Test
    void testUpdateEventStatus_BadRequest_NullEventId() throws Exception {
        EventStatusUpdate update = new EventStatusUpdate();
        update.setLive(true); // EventId is null

        mockMvc.perform(post("/events/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).updateEventStatus(null, true);
    }

    @Test
    void testUpdateEventStatus_BadRequest_EmptyEventId() throws Exception {
        EventStatusUpdate update = new EventStatusUpdate();
        update.setEventId("");
        update.setLive(true);

        mockMvc.perform(post("/events/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).updateEventStatus("", true);
    }

    @Test
    void testUpdateEventStatus_BadRequest_NullBody() throws Exception {
        mockMvc.perform(post("/events/status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).updateEventStatus(null, false);
    }
}
