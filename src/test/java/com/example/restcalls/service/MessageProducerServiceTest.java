package com.example.restcalls.service;

import com.example.restcalls.dto.MockApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageProducerServiceTest {

    @Test
    void testSendMessage_Success() throws JsonProcessingException {
        ObjectMapper realMapper = new ObjectMapper(); // Use real ObjectMapper for success case
        MessageProducerService producerService = new MessageProducerService(realMapper);
        MockApiResponse payload = new MockApiResponse("evt1", "1:0");

        // Call the method, ensure no exception is thrown
        producerService.sendMessage("test-topic", payload);
        // Further verification would require a logging test framework to capture SLF4J logs.
        // For now, we're testing that it runs without error and JSON conversion works.
    }

    @Test
    void testSendMessage_NullPayload() throws JsonProcessingException {
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        MessageProducerService producerService = new MessageProducerService(mockMapper);

        producerService.sendMessage("test-topic", null);

        // Verify that ObjectMapper was not called for null payload
        verify(mockMapper, never()).writeValueAsString(any());
        // Further verification for logging "Attempted to send a null payload..."
    }

    @Test
    void testSendMessage_JsonProcessingException() throws Exception {
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        MessageProducerService producerService = new MessageProducerService(mockMapper);
        MockApiResponse payload = new MockApiResponse("evt1", "1:0");

        // Configure mock ObjectMapper to throw JsonProcessingException
        when(mockMapper.writeValueAsString(payload)).thenThrow(new JsonProcessingException("Test Exception") {});

        // Call the method and assert that it handles the exception gracefully
        // (i.e., logs an error and does not throw the exception upwards)
        producerService.sendMessage("test-topic", payload);

        // Verify that writeValueAsString was called
        verify(mockMapper, times(1)).writeValueAsString(payload);
        // Further verification would be to check if an error was logged.
        // This would typically involve a custom Logback appender or a library.
    }
}
