package com.gym.controller.common;

import com.gym.common.Result;
import com.gym.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/common/upload")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> data = fileService.uploadImage(file);
        return Result.success("上传成功", data);
    }

    @PostMapping("/video")
    public Result<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        Map<String, String> data = fileService.uploadVideo(file);
        return Result.success("上传成功", data);
    }

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        Map<String, String> data = fileService.upload(file);
        return Result.success("上传成功", data);
    }
}
