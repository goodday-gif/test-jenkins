package com.gym.service;

import com.gym.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FileService {

    @Value("${gym.upload.path}")
    private String uploadPath;

    @Value("${gym.upload.image-max-size}")
    private long imageMaxSize;

    @Value("${gym.upload.video-max-size}")
    private long videoMaxSize;

    private static final List<String> IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final List<String> VIDEO_TYPES = Arrays.asList("mp4", "avi", "mov", "wmv");

    public Map<String, String> uploadImage(MultipartFile file) {
        validateFile(file, IMAGE_TYPES, imageMaxSize);
        return storeFile(file, "images");
    }

    public Map<String, String> uploadVideo(MultipartFile file) {
        validateFile(file, VIDEO_TYPES, videoMaxSize);
        return storeFile(file, "videos");
    }

    public Map<String, String> upload(MultipartFile file) {
        // 通用上传，合并所有允许的类型
        List<String> allTypes = new java.util.ArrayList<>(IMAGE_TYPES);
        allTypes.addAll(VIDEO_TYPES);
        long maxSize = videoMaxSize; // 使用较大的限制
        validateFile(file, allTypes, maxSize);

        String ext = getExtension(file.getOriginalFilename());
        String subDir = IMAGE_TYPES.contains(ext) ? "images" : "videos";
        return storeFile(file, subDir);
    }

    private void validateFile(MultipartFile file, List<String> allowedTypes, long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        String ext = getExtension(file.getOriginalFilename());
        if (!allowedTypes.contains(ext)) {
            throw new BusinessException("不支持的文件类型: " + ext + "，允许: " + String.join(",", allowedTypes));
        }
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小超过限制，最大允许: " + (maxSize / 1024 / 1024) + "MB");
        }
    }

    private Map<String, String> storeFile(MultipartFile file, String subDir) {
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String ext = getExtension(file.getOriginalFilename());
        String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;

        String relativePath = subDir + "/" + dateDir;
        File dir = new File(uploadPath + relativePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, newFileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }

        Map<String, String> result = new HashMap<>();
        result.put("url", "/upload/" + relativePath + "/" + newFileName);
        result.put("name", file.getOriginalFilename());
        return result;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
