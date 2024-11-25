package com.vagabonder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vagabonder.dto.ProfileDTO;
import com.vagabonder.entity.User;
import com.vagabonder.service.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }


    @GetMapping({"", "/{userId}"})
    public ResponseEntity<ProfileDTO> getProfile(@AuthenticationPrincipal User currentUser,
                                                 @PathVariable(required = false) UUID userId) {
        UUID targetUserId = userId == null ? currentUser.getId() : userId;
        return ResponseEntity.ok(profileService.getProfile(currentUser.getId(), targetUserId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileDTO> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestPart(value = "profileDto") String profileDtoJson,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            @RequestPart(value = "coverPhoto", required = false) MultipartFile coverPhoto,
            @RequestPart(value = "newTravelMemories", required = false) List<MultipartFile> newTravelMemories)
            throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProfileDTO profileDTO = mapper.readValue(profileDtoJson, ProfileDTO.class);

        profileDTO.setProfilePhoto(profilePhoto);
        profileDTO.setCoverPhoto(coverPhoto);
        profileDTO.setNewTravelMemories(newTravelMemories);

        return ResponseEntity.ok(profileService.updateProfile(currentUser.getId(), profileDTO));
    }
}
