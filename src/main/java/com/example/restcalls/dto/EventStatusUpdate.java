package com.example.restcalls.dto;

import lombok.Data;

@Data
public class EventStatusUpdate {

    private String eventId;
    private boolean live;

}
