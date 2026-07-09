package com.inclusiveconnect.inclusiveconnectbackend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.inclusiveconnect.inclusiveconnectbackend.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryFileStorageService implements FileStorageService {

    private final Cloudinary cloudinary;

    public CloudinaryFileStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file");
        }

        Map<?, ?> params = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto");

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        String publicId = extractPublicId(fileUrl);
        if (publicId == null) {
            return;
        }

        String resourceType = fileUrl.contains("/raw/") ? "raw" : "image";

        Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));
        String deleteResult = (String) result.get("result");
        if (!"ok".equals(deleteResult) && !"not_found".equals(deleteResult)) {
            throw new IOException("Failed to delete file from Cloudinary: " + deleteResult);
        }
    }

    private String extractPublicId(String url) {
        int uploadIdx = url.indexOf("/upload/");
        if (uploadIdx == -1) {
            return null;
        }

        String pathAfterUpload = url.substring(uploadIdx + 8);

        if (pathAfterUpload.startsWith("v")) {
            int firstSlash = pathAfterUpload.indexOf('/');
            if (firstSlash != -1) {
                pathAfterUpload = pathAfterUpload.substring(firstSlash + 1);
            }
        }

        int lastDot = pathAfterUpload.lastIndexOf('.');
        if (lastDot != -1) {
            pathAfterUpload = pathAfterUpload.substring(0, lastDot);
        }

        return pathAfterUpload;
    }
}
