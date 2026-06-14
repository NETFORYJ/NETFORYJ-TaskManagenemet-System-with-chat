package com.yjnet.CollabFlow.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Service
public class FileStorageService {
    
    private final String uploadDir;
    private final long MAX_CHAT_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private final long MAX_TRANSFER_FILE_SIZE = 300 * 1024 * 1024; // 300MB
    
    public FileStorageService() {
        this.uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        System.out.println("Upload directory: " + uploadDir);
    }
    
    public String saveFile(MultipartFile file, String prefix) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String filename = System.currentTimeMillis() + "_" + prefix + extension;
        File destination = new File(uploadDir + File.separator + filename);
        file.transferTo(destination);
        
        return "/uploads/" + filename;
    }
    
    public boolean validateFileSize(MultipartFile file, boolean isForChat) {
        long maxSize = isForChat ? MAX_CHAT_FILE_SIZE : MAX_TRANSFER_FILE_SIZE;
        return file.getSize() <= maxSize;
    }
    
    public File getFile(String fileUrl) {
        String filePath = uploadDir + fileUrl.replace("/uploads/", File.separator);
        return new File(filePath);
    }
    
    public void deleteFile(String fileUrl) {
        File file = getFile(fileUrl);
        if (file.exists()) {
            file.delete();
        }
    }
}