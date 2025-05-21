package com.example.restcalls.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockApiResponse {

    private String eventId;
    private String currentScore;

}
