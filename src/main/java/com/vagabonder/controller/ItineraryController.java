package com.vagabonder.controller;


import com.vagabonder.dto.ItineraryRequestDTO;
import com.vagabonder.dto.ItineraryUpdateRequestDTO;
import com.vagabonder.entity.Itinerary;
import com.vagabonder.entity.User;
import com.vagabonder.enums.ItineraryStatus;
import com.vagabonder.service.ItineraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/itinerary")
@Tag(name = "Itinerary", description = "Endpoints for itinerary customization")
public class ItineraryController {
    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }


    @GetMapping("/past")
    public ResponseEntity<List<Itinerary>> getRandomCompletedItineraries(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(itineraryService.getRandomCompletedItinerariesByLimit(user.getId(), 3));
    }


    @GetMapping("/future")
    public ResponseEntity<List<Itinerary>> getRandomFutureItineraries(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(itineraryService.getRandomFutureItinerariesByLimit(user.getId(),3));
    }

    @GetMapping("/{itineraryId}")
    public ResponseEntity<Itinerary> getItinerary(@PathVariable UUID itineraryId){
        return ResponseEntity.ok(itineraryService.getItinerary(itineraryId));
    }


    @PutMapping("/update/{id}")
    public Itinerary updateItinerary(@AuthenticationPrincipal User currentUser,
                                     @Parameter(
                                             description = "Id of the user thats being updated",
                                             required = true
                                     ) @PathVariable UUID id, @RequestBody ItineraryUpdateRequestDTO itineraryUpdateRequestDTO) { //todo make dates also updatable and handle dates in past not possible
        return itineraryService.updateItinerary(id,currentUser.getId(), itineraryUpdateRequestDTO);
    }


    @PostMapping("/create")
    @Operation(
            summary = "Create new itinerary",
            description = "Creates a new itinerary for current user from incoming ItineraryRequestDTO object. "
    )
    public ResponseEntity<Itinerary> createItinerary(@AuthenticationPrincipal User currentUser, @RequestBody ItineraryRequestDTO itineraryRequest) {
        Itinerary createdItinerary = itineraryService.createItinerary(itineraryRequest, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItinerary);
    }


    // Update the status of an itinerary
    @PutMapping("/update-status/{id}")
    public void updateItineraryStatus(@PathVariable UUID id, @RequestParam ItineraryStatus status) {
        itineraryService.updateItineraryStatus(id, status);
    }

}
