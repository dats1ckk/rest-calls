package com.example.restcalls.dto;

public class MockApiResponse {

    private String eventId;
    private String currentScore;

    public MockApiResponse() {
    }

    public MockApiResponse(String eventId, String currentScore) {
        this.eventId = eventId;
        this.currentScore = currentScore;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(String currentScore) {
        this.currentScore = currentScore;
    }
}
