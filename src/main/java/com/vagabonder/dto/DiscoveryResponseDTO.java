package com.vagabonder.dto;

import com.vagabonder.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DiscoveryResponseDTO {
    private List<User> similarUsers;
    private String nextPageToken;
}
