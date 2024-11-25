package com.vagabonder.api.feignClient;

import com.vagabonder.api.dto.GeminiRequestDTO;
import com.vagabonder.api.dto.GeminiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geminiClient", url = "${gemini.api.url}")
public interface GeminiClient {
    @PostMapping
    GeminiResponse generateItinerary(
            @RequestBody GeminiRequestDTO request,
            @RequestParam("key") String apiKey
    );
}