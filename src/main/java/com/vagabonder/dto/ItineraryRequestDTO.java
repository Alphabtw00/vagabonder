package com.vagabonder.dto;

import com.vagabonder.enums.Budget;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class ItineraryRequestDTO {
    private String name;
    private String destination;
    private Budget budget;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int travelers;
}
