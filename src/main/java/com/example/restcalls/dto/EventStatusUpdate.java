package com.example.restcalls.dto;

public class EventStatusUpdate {

    private String eventId;
    private boolean live;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
}
