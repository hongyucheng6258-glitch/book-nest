package com.example.book.controller;

import com.example.book.common.BusinessException;
import com.example.book.common.Result;
import com.example.book.entity.Favorite;
import com.example.book.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏表 Controller
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/favorite")
@CrossOrigin
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 获取用户收藏列表
     *
     * @param userId 用户ID
     * @return 收藏列表
     */
    @GetMapping("/list")
    public Result<List<Favorite>> getFavoriteList(@RequestParam Integer userId) {
        try {
            List<Favorite> favoriteList = favoriteService.getFavoriteListByUserId(userId);
            return new Result<>(true, favoriteList, "查询成功");
        } catch (Exception e) {
            throw new BusinessException("查询失败: " + e.getMessage());
        }
    }

    /**
     * 添加收藏
     *
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Integer> addFavorite(
            @RequestParam Integer userId,
            @RequestParam Integer bookId
    ) {
        try {
            int result = favoriteService.addFavorite(userId, bookId);
            return new Result<>(true, result, "收藏成功");
        } catch (Exception e) {
            throw new BusinessException("收藏失败: " + e.getMessage());
        }
    }

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 操作结果
     */
    @DeleteMapping("/remove")
    public Result<Integer> removeFavorite(
            @RequestParam Integer userId,
            @RequestParam Integer bookId
    ) {
        try {
            int result = favoriteService.removeFavorite(userId, bookId);
            return new Result<>(true, result, "取消收藏成功");
        } catch (Exception e) {
            throw new BusinessException("取消收藏失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否已收藏
     *
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 检查结果
     */
    @GetMapping("/check")
    public Result<Boolean> checkFavorite(
            @RequestParam Integer userId,
            @RequestParam Integer bookId
    ) {
        try {
            boolean isFavorited = favoriteService.isFavorited(userId, bookId);
            return new Result<>(true, isFavorited, "检查成功");
        } catch (Exception e) {
            throw new BusinessException("检查失败: " + e.getMessage());
        }
    }
}