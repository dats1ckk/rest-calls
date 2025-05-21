package com.example.restcalls.service;

import com.example.restcalls.dto.MockApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MessageProducerService {

    private static final Logger logger = LoggerFactory.getLogger(MessageProducerService.class);
    private final ObjectMapper objectMapper;

    public void sendMessage(String topic, MockApiResponse payload) {
        if (payload == null) {
            logger.warn("Attempted to send a null payload to topic [{}]. Message not sent.", topic);
            return;
        }
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            // Using structured logging for better parseability if needed
            logger.info("Simulating message publishing. Topic: '{}', EventId: '{}', Score: '{}'",
                    topic, payload.getEventId(), payload.getCurrentScore());

            // In a real scenario with Kafka, implement retry logic here,
            // possibly using Spring Retry's @Retryable or KafkaTemplate's built-in retries.
            // For now, we are just logging the attempt.
            // Example conceptual retry:
            // int maxRetries = 3;
            // for (int attempt = 1; attempt <= maxRetries; attempt++) {
            //     try {
            //         // kafkaTemplate.send(topic, jsonPayload);
            //         logger.info("Attempt {} to send to topic [{}]: {}", attempt, topic, jsonPayload);
            //         break; // Exit loop if successful
            //     } catch (Exception e) {
            //         logger.error("Attempt {} failed to send to topic [{}]. Error: {}", attempt, topic, e.getMessage());
            //         if (attempt == maxRetries) {
            //             logger.error("All retry attempts failed for topic [{}]", topic);
            //             // Handle final failure (e.g., send to dead-letter queue)
            //         }
            //         // Thread.sleep(retryDelay); // Optional: wait before retrying
            //     }
            // }

        } catch (JsonProcessingException e) {
            logger.error("Error converting MockApiResponse with eventId '{}' to JSON for topic '{}'. Error: {}",
                         payload.getEventId(), topic, e.getMessage(), e);
            // Handle JSON conversion error, e.g., by not sending the message or sending a fallback
        }
    }
}
