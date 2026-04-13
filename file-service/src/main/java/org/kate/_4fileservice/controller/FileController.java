package org.kate._4fileservice.controller;

import org.kate._4fileservice.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v4/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    // 1. GET THE SECURE LINK (Requires JWT)
    @GetMapping("/get-link/{fileName}")
    public ResponseEntity<String> getDownloadLink(@PathVariable String fileName, java.security.Principal principal){
        String currentUsername = principal.getName();

        String link = fileStorageService.generateDownloadLink(fileName, currentUsername);
        return ResponseEntity.ok(link);
    }

    // 2. ACTUAL DOWNLOAD (Public endpoint, verified by token)
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam String file,
            @RequestParam String username,
            @RequestParam long expiry,
            @RequestParam String token,
            java.security.Principal principal) {

        boolean isValid = fileStorageService.verifyLink(file, username, expiry, token);

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Resource resource = fileStorageService.loadFileAsResource(file, username);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Generic binary stream
                .body(resource);
    }

    // 3. UPLOAD FILE
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("file_name") String customName,
            @AuthenticationPrincipal Object principal) {

        String username = getUsernameFromPrincipal(principal);

        if (username.equals("unknown")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("Error", "Invalid User Token", null));
        }

        String uploadPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + username;

        try {
            File userFolder = new File(uploadPath);
            if (!userFolder.exists()) {
                userFolder.mkdirs();
            }

            Path destination = Paths.get(uploadPath).resolve(customName + ".png");
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok(new ApiResponse<>("Success", "File saved successfully in folder", customName + ".png"));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed", "Failed to upload file", null));
        }
    }

    // 4. LIST ALL FILES
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<String>>> getAllFiles(@AuthenticationPrincipal Object principal) {
        String username = getUsernameFromPrincipal(principal);
        File userFolder = new File(System.getProperty("user.dir") + File.separator + "uploads" + File.separator + username);

        List<String> fileList = new ArrayList<>();
        if (userFolder.exists() && userFolder.isDirectory()) {
            String[] files = userFolder.list();
            if (files != null) {
                fileList = Arrays.asList(files);
            }
        }

        return ResponseEntity.ok(new ApiResponse<>("Success", "Files retrieved for " + username, fileList));
    }

    // 5. DELETE FILE
    @DeleteMapping("/{fileName}")
    public ResponseEntity<ApiResponse<String>> deleteFile(
            @PathVariable String fileName,
            @AuthenticationPrincipal Object principal) {

        String username = getUsernameFromPrincipal(principal);
        String uploadPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + username;
        File fileToDelete = new File(uploadPath + File.separator + fileName + ".png");

        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                return ResponseEntity.ok(new ApiResponse<>("Success", "File deleted successfully", fileName));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>("Failed", "Could not delete file", null));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Failed", "File not found", null));
    }

    // HELPER: Extract username from JWT
    private String getUsernameFromPrincipal(Object principal) {
        if (principal instanceof org.springframework.security.oauth2.jwt.Jwt) {
            return ((org.springframework.security.oauth2.jwt.Jwt) principal).getClaimAsString("sub");
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        }
        return "unknown";
    }
}