package com.vagabonder.service;

import com.vagabonder.dto.ProfileDTO;
import com.vagabonder.entity.Itinerary;
import com.vagabonder.entity.User;
import com.vagabonder.enums.FriendshipStatus;
import com.vagabonder.exception.StorageException;
import com.vagabonder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProfileService {
    private final UserRepository userRepository;
    private final FriendRequestService friendRequestService;
    private final ItineraryService itineraryService;
    private final ImageStorageService imageStorageService;

    public ProfileService(UserRepository userRepository,
                          FriendRequestService friendRequestService,
                          ItineraryService itineraryService,
                          ImageStorageService imageStorageService) {
        this.userRepository = userRepository;
        this.friendRequestService = friendRequestService;
        this.itineraryService = itineraryService;
        this.imageStorageService = imageStorageService;
    }

    public ProfileDTO getProfile(UUID currentUserId, UUID profileUserId) {
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        User profileUser = userRepository.findById(profileUserId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        FriendshipStatus status = currentUserId.equals(profileUserId)
                ? FriendshipStatus.SELF
                : friendRequestService.getFriendshipStatus(currentUser, profileUser);

        return mapProfileDTOFromUser(profileUser, status);
    }

    @Transactional
    public ProfileDTO updateProfile(UUID userId, ProfileDTO profileDTO) {
        if (!userId.equals(profileDTO.getUserId())) {
            throw new IllegalArgumentException("Profile update not authorized for this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        try {
            // Handle memory deletions
            if (profileDTO.getMemoriesToDelete() != null && !profileDTO.getMemoriesToDelete().isEmpty()) {
                for (String path : profileDTO.getMemoriesToDelete()) {
                    try {
                        imageStorageService.deleteImage(path);
                        user.getTravelMemoryPaths().remove(path);
                    } catch (StorageException e) {
                        log.error("Failed to delete memory: {}", path, e);
                    }
                }
                userRepository.save(user);
            }

            // Handle new memories
            if (profileDTO.getNewTravelMemories() != null && !profileDTO.getNewTravelMemories().isEmpty()) {
                List<String> newPaths = imageStorageService.storeMemories(
                        profileDTO.getNewTravelMemories(),
                        userId
                );
                user.getTravelMemoryPaths().addAll(newPaths);
            }

            // Handle profile photo
            if (profileDTO.getProfilePhoto() != null && !profileDTO.getProfilePhoto().isEmpty()) {
                if (user.getProfilePhotoPath() != null) {
                    imageStorageService.deleteImage(user.getProfilePhotoPath());
                }
                String profilePhotoPath = imageStorageService.storeImage(
                        profileDTO.getProfilePhoto(),
                        "profile_" + userId
                );
                user.setProfilePhotoPath(profilePhotoPath);
            }

            // Handle cover photo
            if (profileDTO.getCoverPhoto() != null && !profileDTO.getCoverPhoto().isEmpty()) {
                if (user.getCoverPhotoPath() != null) {
                    imageStorageService.deleteImage(user.getCoverPhotoPath());
                }
                String coverPhotoPath = imageStorageService.storeImage(
                        profileDTO.getCoverPhoto(),
                        "cover_" + userId
                );
                user.setCoverPhotoPath(coverPhotoPath);
            }

            // Update other profile fields
            updateUserFields(user, profileDTO);

            User savedUser = userRepository.save(user);
            updateSecurityContextHolder(savedUser);
            return mapProfileDTOFromUser(savedUser, FriendshipStatus.SELF);

        } catch (Exception e) {
            log.error("Failed to update profile for user: {}", userId, e);
            throw new IllegalArgumentException("Failed to update profile", e);
        }
    }

    private void updateUserFields(User user, ProfileDTO profileDTO) {
        if (profileDTO.getEmail() != null && !profileDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmailEquals(profileDTO.getEmail())) {
                throw new IllegalArgumentException("Email already in use: " + profileDTO.getEmail());
            }
            user.setEmail(profileDTO.getEmail());
        }

        Optional.ofNullable(profileDTO.getFullName()).ifPresent(user::setFullName);
        Optional.ofNullable(profileDTO.getGender()).ifPresent(user::setGender);
        Optional.ofNullable(profileDTO.getDateOfBirth()).ifPresent(user::setDateOfBirth);
        Optional.ofNullable(profileDTO.getResidence()).ifPresent(user::setResidence);
        Optional.ofNullable(profileDTO.getLanguages()).ifPresent(user::setLanguages);
        Optional.ofNullable(profileDTO.getEthnicity()).ifPresent(user::setEthnicity);
        Optional.ofNullable(profileDTO.getReligion()).ifPresent(user::setReligion);
        Optional.ofNullable(profileDTO.getOccupation()).ifPresent(user::setOccupation);
        Optional.ofNullable(profileDTO.getBio()).ifPresent(user::setBio);
        Optional.ofNullable(profileDTO.getBestTravelStory()).ifPresent(user::setBestTravelStory);
        Optional.ofNullable(profileDTO.getPlacesTravelled()).ifPresent(user::setPlacesTravelled);
    }

    private ProfileDTO mapProfileDTOFromUser(User user, FriendshipStatus status) {
        List<String> randomizedMemories = new ArrayList<>(user.getTravelMemoryPaths());
        Collections.shuffle(randomizedMemories);
        return ProfileDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePhotoPath(user.getProfilePhotoPath())
                .coverPhotoPath(user.getCoverPhotoPath())
                .travelMemoryPaths(randomizedMemories)
                .age(user.getAge())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .residence(user.getResidence())
                .languages(user.getLanguages())
                .ethnicity(user.getEthnicity())
                .religion(user.getReligion())
                .occupation(user.getOccupation())
                .bio(user.getBio())
                .placesTravelled(user.getPlacesTravelled())
                .bestTravelStory(user.getBestTravelStory())
                .futureItineraries(itineraryService.getRandomFutureItinerariesByLimit(user.getId(), 3)
                        .stream()
                        .map(Itinerary::getName)
                        .collect(Collectors.toList()))
                .friendshipStatus(status)
                .build();
    }

    private void updateSecurityContextHolder(User savedUser) {
        var authentication = new UsernamePasswordAuthenticationToken(
                savedUser,
                savedUser.getPassword(),
                savedUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
