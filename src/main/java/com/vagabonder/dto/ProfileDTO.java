package com.vagabonder.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vagabonder.enums.FriendshipStatus;
import com.vagabonder.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private UUID userId;
    private String email;
    private String fullName;
    private String profilePhotoPath;
    private String coverPhotoPath;
    private List<String> travelMemoryPaths; //todo add dates for images added and sort feature on dates in frontend
    private List<String> memoriesToDelete;
    @JsonIgnore
    private MultipartFile profilePhoto;
    @JsonIgnore
    private MultipartFile coverPhoto;
    @JsonIgnore
    private List<MultipartFile> newTravelMemories;
    private Integer age;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String residence;
    private String languages;
    private String ethnicity;
    private String religion;
    private String occupation;
    private String bio;
    private String placesTravelled;
    private String bestTravelStory;
    private List<String> travelMemories;
    private List<String> futureItineraries;
    private FriendshipStatus friendshipStatus;
}
