package com.kml.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserAvatarController {

    private final String UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads", "avatars").toString();

    @PostMapping("/me/avatar")
    public ResponseEntity<?> uploadAvatar(Authentication authentication, @RequestParam("file") MultipartFile file) {
        if (authentication == null) return ResponseEntity.status(401).build();
        if (file.isEmpty()) return ResponseEntity.badRequest().body("File cannot be empty");

        try {
            // Create target folder if it doesn't exist
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Validate file extension safely
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }
            if (!extension.matches("\\.(jpg|jpeg|png|webp)$")) {
                return ResponseEntity.badRequest().body("Only JPG, PNG, and WEBP images are allowed");
            }

            // Generate clean unique filename to avoid duplicates/collisions
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Path targetPath = Paths.get(UPLOAD_DIR, uniqueFilename);

            // Save file payload to server system storage (with explicit overwrite option)
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Return target accessible URL string mapping
            String avatarUrl = "/uploads/avatars/" + uniqueFilename;
            return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Internal Server Error",
                "message", "File upload failed"
            ));
        }
    }
}
