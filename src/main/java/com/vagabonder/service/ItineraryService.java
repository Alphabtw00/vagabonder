package com.vagabonder.service;

import com.vagabonder.api.dto.GeminiRequestDTO;
import com.vagabonder.api.dto.GeminiResponse;
import com.vagabonder.api.feignClient.GeminiClient;
import com.vagabonder.dto.ItineraryRequestDTO;
import com.vagabonder.dto.ItineraryUpdateRequestDTO;
import com.vagabonder.entity.Itinerary;
import com.vagabonder.entity.User;
import com.vagabonder.enums.ItineraryStatus;
import com.vagabonder.exception.UnauthorizedException;
import com.vagabonder.repository.ItineraryRepository;
import com.vagabonder.repository.UserRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItineraryService {
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final GeminiClient geminiClient;
    private final UserRepository userRepository;
    private final ItineraryRepository itineraryRepository;

    public ItineraryService(GeminiClient geminiClient,
                            UserRepository userRepository,
                            ItineraryRepository itineraryRepository) {
        this.geminiClient = geminiClient;
        this.userRepository = userRepository;
        this.itineraryRepository = itineraryRepository;
    }

    public Itinerary createItinerary(ItineraryRequestDTO request, UUID currentUserId) {
        try {
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Current User not found"));

            GeminiRequestDTO geminiRequest = createGeminiRequest(request);
            GeminiResponse response = geminiClient.generateItinerary(geminiRequest, geminiApiKey);
            String generatedActivities = extractAndFormatActivities(response);

            return itineraryRepository.save(Itinerary.builder()
                    .name(request.getName())
                    .activities(generatedActivities)
                    .user(currentUser)
                    .fromDate(request.getFromDate())
                    .toDate(request.getToDate())
                    .status(ItineraryStatus.PLANNED)
                    .build());
        } catch (FeignException e) {
            log.error("Error calling Gemini API: {}", e.getMessage());
            throw new RuntimeException("Failed to generate itinerary: " + e.getMessage(), e);
        }
    }

    private GeminiRequestDTO createGeminiRequest(ItineraryRequestDTO request) {
        String prompt = buildPrompt(request);

        return GeminiRequestDTO.builder()
                .contents(List.of(
                        GeminiRequestDTO.Content.builder()
                                .parts(List.of(GeminiRequestDTO.Part.builder()
                                                .text(prompt)
                                                .build()))
                                .build()
                ))
                .build();
    }

    private String buildPrompt(ItineraryRequestDTO request) {
        long days = ChronoUnit.DAYS.between(request.getFromDate(), request.getToDate()) + 1;

        return String.format("""
            Create a detailed day-by-day itinerary for %d days in %s.
            
            Trip Details:
            - Dates: %s to %s
            - Number of Travelers: %d people
            - Budget Level: %s
            
            Please provide a structured itinerary with:
            1. Daily schedule with timing
            2. Activities and attractions
            3. Restaurant recommendations
            4. Transportation details
            5. Budget-appropriate suggestions
            
            Format the response as a clear day-by-day plan with bullet points.
            Focus on %s budget experiences and local highlights.
            Consider group size of %d people for all bookings and activities.
            """,
                days,
                request.getDestination(),
                request.getFromDate(),
                request.getToDate(),
                request.getTravelers(),
                request.getBudget(),
                request.getBudget(),
                request.getTravelers()
        );
    }

    private String extractAndFormatActivities(GeminiResponse response) {
        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
            throw new RuntimeException("Failed to generate itinerary from Gemini API");
        }

        return Optional.ofNullable(response.getCandidates().get(0))
                .map(GeminiResponse.Candidate::getContent)
                .map(content -> content.getParts().get(0))
                .map(GeminiResponse.Part::getText)
                .map(this::formatText)
                .orElseThrow(() -> new RuntimeException("No content in Gemini response"));
    }

    private String formatText(String text) {
        return text
                .replaceAll("##\\s+", "")
                .replaceAll("\\*\\*", "")
                .replaceAll("(?m)^\\s*[•*]\\s*", "• ")
                .replaceAll("\n{3,}", "\n\n")
                .trim();
    }

    public List<Itinerary> getRandomCompletedItinerariesByLimit(UUID userId, Integer limit) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Itinerary> completedItineraries = itineraryRepository.findByUserAndStatusOrderByToDate(currentUser, ItineraryStatus.COMPLETED);
        Random random = new Random();
        return completedItineraries.size() <= 3 ? completedItineraries :
                random.ints(0, completedItineraries.size()) //starts a stream between 0 and number of itineraries
                        .distinct() //no int is same
                        .limit(limit) //max allowed pictures to send
                        .mapToObj(completedItineraries::get) //gets the random int indices and map that to list.get(int)
                        .collect(Collectors.toList());
    }

    public List<Itinerary> getRandomFutureItinerariesByLimit(UUID userId, Integer limit) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Itinerary> futureItineraries = itineraryRepository.findByUserAndStatusOrderByFromDateDesc(user, ItineraryStatus.PLANNED);
        Random random = new Random();
        return futureItineraries.size() <= 3 ? futureItineraries :
                random.ints(0, futureItineraries.size())
                        .distinct()
                        .limit(limit)
                        .mapToObj(futureItineraries::get)
                        .collect(Collectors.toList());
    }

    public Itinerary updateItinerary(UUID id, UUID currentUserId, ItineraryUpdateRequestDTO updatedItineraryDTO) {
        return itineraryRepository.findById(id)
                .map(itinerary -> {
                    if (!itinerary.getUser().getId().equals(currentUserId)) {
                        throw new UnauthorizedException("Not authorized to update this itinerary");
                    }
                    itinerary.setName(updatedItineraryDTO.getName());
                    itinerary.setActivities(updatedItineraryDTO.getActivities());
                    return itineraryRepository.save(itinerary);
                }).orElseThrow(() -> new IllegalArgumentException("Itinerary not found"));
    }

    public Itinerary getItinerary(UUID itineraryId){
        return itineraryRepository.findById(itineraryId).orElseThrow(() -> new IllegalArgumentException("Itinerary not found"));
    }

    public void updateItineraryStatus(UUID id, ItineraryStatus status) {
        Itinerary itinerary = itineraryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Itinerary not found"));
        itinerary.setStatus(status);
        itineraryRepository.save(itinerary);
    }

    @Scheduled(cron = "0 0 0,12 * * *") // Runs at midnight and noon daily (0,12)th hour
    public void updateItineraryStatuses() {
        LocalDate today = LocalDate.now();

        //for ongoing itineraries
        List<Itinerary> plannedItineraries = itineraryRepository.findByStatus(ItineraryStatus.PLANNED);
        for (Itinerary itinerary : plannedItineraries) {
            if (itinerary.getFromDate() != null && itinerary.getFromDate().equals(today)) {
                itinerary.setStatus(ItineraryStatus.ONGOING);
                itineraryRepository.save(itinerary);
            }
        }

        //for completed itineraries
        List<Itinerary> ongoingItineraries = itineraryRepository.findByStatus(ItineraryStatus.ONGOING);
        for (Itinerary itinerary : ongoingItineraries) {
            if (itinerary.getToDate() != null && itinerary.getToDate().isBefore(today)) {
                itinerary.setStatus(ItineraryStatus.COMPLETED);
                itineraryRepository.save(itinerary);
            }
        }
    }


}