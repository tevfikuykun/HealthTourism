package com.healthtourism.socialmediaservice.controller;
import com.healthtourism.socialmediaservice.service.SocialMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/social")
@CrossOrigin(origins = "*")
public class SocialMediaController {
    @Autowired
    private SocialMediaService service;

    @PostMapping("/share/facebook")
    public ResponseEntity<Map<String, Object>> shareToFacebook(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.shareToFacebook(request.get("url"), request.get("message")));
    }

    @PostMapping("/share/instagram")
    public ResponseEntity<Map<String, Object>> shareToInstagram(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.shareToInstagram(request.get("imageUrl"), request.get("caption")));
    }

    @PostMapping("/share/twitter")
    public ResponseEntity<Map<String, Object>> shareToTwitter(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.shareToTwitter(request.get("message"), request.get("url")));
    }

    @PostMapping("/login/{provider}")
    public ResponseEntity<Map<String, Object>> socialLogin(@PathVariable String provider, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(service.socialLogin(provider, request.get("token")));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSocialStats(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) userId = 1L;
        return ResponseEntity.ok(service.getSocialStats(userId));
    }
}

