package com.example.book.controller;

import com.example.book.common.BusinessException;
import com.example.book.common.Result;
import com.example.book.entity.UserInfo;
import com.example.book.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户详情表 Controller。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserInfoController {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);
    
    @Autowired
    private UserInfoService userInfoService;
    
    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    @GetMapping("/info")
    public Result<UserInfo> getUserInfo(Integer userId) {
        try {
            logger.info("获取用户详情请求，userId: {}", userId);
            UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
            
            // 修复头像URL，将http://localhost:8088替换为相对路径/
            if (userInfo != null && userInfo.getAvatar() != null && userInfo.getAvatar().startsWith("http://localhost:8088")) {
                userInfo.setAvatar(userInfo.getAvatar().replace("http://localhost:8088", ""));
            }
            
            logger.info("获取用户详情成功，结果: {}", userInfo);
            return new Result<>(true, userInfo, "查询成功");
        } catch (Exception e) {
            logger.error("获取用户详情失败，userId: {}, 错误: {}", userId, e.getMessage(), e);
            throw new BusinessException("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户详情
     *
     * @param userInfo 用户详情
     * @return 操作结果
     */
    @PutMapping("/info")
    public Result<Integer> updateUserInfo(@RequestBody UserInfo userInfo) {
        try {
            logger.info("更新用户详情请求，userInfo: {}", userInfo);
            int result = userInfoService.saveOrUpdateUserInfo(userInfo);
            logger.info("更新用户详情成功，影响行数: {}", result);
            return new Result<>(true, result, "更新成功");
        } catch (Exception e) {
            logger.error("更新用户详情失败，userInfo: {}, 错误: {}", userInfo, e.getMessage(), e);
            throw new BusinessException("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传头像
     *
     * @param file 上传的文件
     * @param userId 用户ID
     * @return 上传结果
     */
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam Integer userId) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                throw new BusinessException("上传文件不能为空");
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BusinessException("只允许上传图片文件");
            }
            
            // 检查文件大小（最大2MB）
            long size = file.getSize();
            if (size > 2 * 1024 * 1024) {
                throw new BusinessException("文件大小不能超过2MB");
            }
            
            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存文件到指定目录
            String uploadDir = System.getProperty("user.dir") + File.separator + "upload" + File.separator + "avatar";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            File dest = new File(dir, filename);
            file.transferTo(dest);
            
            // 生成文件URL，使用相对路径，避免硬编码域名
            String fileUrl = "/upload/avatar/" + filename;
            
            // 更新用户头像
            UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setUserId(userId);
            }
            userInfo.setAvatar(fileUrl);
            userInfoService.saveOrUpdateUserInfo(userInfo);
            
            // 返回结果
            Map<String, String> data = new HashMap<>();
            data.put("avatar", fileUrl);
            return new Result<>(true, data, "上传成功");
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("上传失败: " + e.getMessage());
        }
    }
}