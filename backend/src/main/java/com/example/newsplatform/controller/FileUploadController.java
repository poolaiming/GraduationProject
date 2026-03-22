package com.example.newsplatform.controller;

import com.example.newsplatform.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final Set<String> ALLOWED_VIDEO_EXT = Set.of(".mp4", ".webm", ".ogg");

    @Value("${upload.path:uploads/}")
    private String uploadPath;

    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片文件");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("图片大小不能超过 5MB");
        }

        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf(".")).toLowerCase()
                : ".jpg";
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            log.info("创建上传目录: {} (成功: {})", dir.getAbsolutePath(), created);
        }
        File dest = new File(dir, filename);
        log.info("准备保存文件到: {}", dest.getAbsolutePath());

        try {
            file.transferTo(dest);
            log.info("文件上传成功: {}", dest.getAbsolutePath());
        } catch (IOException e) {
            log.error("文件保存失败，目标路径: {}, 错误信息: {}", dest.getAbsolutePath(), e.getMessage(), e);
            throw new RuntimeException("文件上传失败，请重试");
        }

        return ApiResponse.success("/uploads/" + filename);
    }

    @PostMapping("/video")
    public ApiResponse<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf(".")).toLowerCase()
                : "";
        if (!ALLOWED_VIDEO_EXT.contains(ext)) {
            throw new IllegalArgumentException("仅支持 mp4/webm/ogg 视频格式");
        }
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new IllegalArgumentException("视频大小不能超过 50MB");
        }

        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            log.info("创建上传目录: {} (成功: {})", dir.getAbsolutePath(), created);
        }
        File dest = new File(dir, filename);
        log.info("准备保存视频到: {}", dest.getAbsolutePath());

        try {
            file.transferTo(dest);
            log.info("视频上传成功: {}", dest.getAbsolutePath());
        } catch (IOException e) {
            log.error("视频保存失败，目标路径: {}, 错误信息: {}", dest.getAbsolutePath(), e.getMessage(), e);
            throw new RuntimeException("视频上传失败，请重试");
        }

        return ApiResponse.success("/uploads/" + filename);
    }
}
