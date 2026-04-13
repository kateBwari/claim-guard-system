package org.kate._4fileservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
@Slf4j
@Service
public class FileStorageService {
    private final String secretKey = "ThisIsASecretKeyThatIsExactlySixtyFourCharactersLongToFixTheError";

    @Value("${file.sharing.secret}")
    private String sharingSecret;

    private final Path root = Paths.get("uploads");

    public void save(MultipartFile file, String username) {
        // Your existing save logic here
    }

    public String generateDownloadLink(String fileName, String username) {

        // Set expiration: Current time + 30 minutes
        long expiryTime = System.currentTimeMillis() + (30L * 60 * 1000); // 1,800,000

        // Create the raw string (Must match the Verifier exactly)
        String dataToSign = fileName + ":" + username + ":" + expiryTime;
        log.info("Generating hash using details {}, {}, {}", fileName, username, expiryTime);
        try {
            // Generate Hex signature using our helper
            String signature = hmacSha256(dataToSign, secretKey);
            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);

            // Construct final URL (Using your laptop's Hotspot IP)
            return "http://192.168.107.199:8085/files/download?file=" + fileName +
                    "&username=" + username +
                    "&expiry=" + expiryTime +
                    "&token=" + signature;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate secure download link", e);
        }
    }

    /**
     * 2. VERIFY THE LINK
     * This is what prevents the 401 Unauthorized error.
     */
    public boolean verifyLink(String file, String username, long expiry, String token) {
        // Check if the link has expired
        if (System.currentTimeMillis() > expiry) {
            return false;
        }

        // Re-generate the signature using the SAME format as the generator
        String dataToVerify = file + ":" + username + ":" + expiry;
        String expectedSignature = hmacSha256(dataToVerify, secretKey);
        System.out.println("Token from URL: " + token);
        System.out.println("Expected Token: " + expectedSignature);

        // Compare the token from the URL with the one we just generated
        return expectedSignature.equals(token);
    }

    /**
     * 3. LOAD FILE AS RESOURCE
     * Fetches the actual file from the 'uploads/{username}' folder.
     */
    public Resource loadFileAsResource(String fileName, String username) {
        try {
            // Construct path: user.dir/uploads/username/filename
            String uploadPath = System.getProperty("user.dir") + File.separator + "uploads"
                    + File.separator + username;

            Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error loading file: " + e.getMessage());
        }
    }

    /**
     * 4. HMAC HELPER (HEX ENCODING)
     * This ensures the token format is consistent.
     */
    private String hmacSha256(String data, String key) {
        try {
            log.info("Hashing data {}, {}", data, key);
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to Hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC signature", e);
        }
    }
}