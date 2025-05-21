package com.example.restcalls.startup;

import com.example.restcalls.service.EventService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final EventService eventService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing mock startup events...");

        String event1Id = "mockEventSTARTUP001";
        eventService.updateEventStatus(event1Id, true);
        logger.info("Mock event [{}] initialized as LIVE.", event1Id);

        String event2Id = "mockEventSTARTUP002";
        eventService.updateEventStatus(event2Id, true);
        logger.info("Mock event [{}] initialized as LIVE.", event2Id);
        
        // Add a non-live event for variety if desired
        String event3Id = "mockEventSTARTUP003";
        eventService.updateEventStatus(event3Id, false);
        logger.info("Mock event [{}] initialized as NOT LIVE.", event3Id);

        logger.info("Mock startup event initialization complete.");
    }
}
