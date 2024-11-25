package com.vagabonder.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItineraryUpdateRequestDTO {
    private String name;
    private String activities;
}
