package com.inclusiveconnect.inclusiveconnectbackend.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    static {
        try {
            java.io.File file = new java.io.File(".env");
            if (file.exists()) {
                java.nio.file.Files.lines(file.toPath())
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .forEach(line -> {
                            int index = line.indexOf('=');
                            if (index > 0) {
                                String key = line.substring(0, index).trim();
                                String value = line.substring(index + 1).trim();
                                System.setProperty(key, value);
                            }
                        });
            }
        } catch (Exception e) {
            // Ignore error loading .env
        }
    }

    @Value("${CLOUDINARY_URL:}")
    private String cloudinaryUrl;

    @Bean
    public Cloudinary cloudinary() {
        String url = cloudinaryUrl;
        if (url == null || url.isEmpty()) {
            url = System.getProperty("CLOUDINARY_URL");
        }
        if (url == null || url.isEmpty()) {
            url = System.getenv("CLOUDINARY_URL");
        }
        if (url == null || url.isEmpty()) {
            // Provide a dummy URL for local development/testing instances
            url = "cloudinary://1234567890:abcdefghijklmnopqrstuvw@dummycloud";
        }
        return new Cloudinary(url);
    }
}
