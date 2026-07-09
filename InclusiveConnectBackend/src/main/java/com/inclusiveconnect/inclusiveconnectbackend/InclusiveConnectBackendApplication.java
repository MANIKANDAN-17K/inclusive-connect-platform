package com.inclusiveconnect.inclusiveconnectbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InclusiveConnectBackendApplication {

    public static void main(String[] args) {
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
        SpringApplication.run(InclusiveConnectBackendApplication.class, args);
    }

}
