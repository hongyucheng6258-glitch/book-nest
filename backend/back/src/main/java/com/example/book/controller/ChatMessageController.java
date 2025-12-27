package com.example.book.controller;

import com.example.book.common.Result;
import com.example.book.entity.ChatMessage;
import com.example.book.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天消息控制器
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {
    
    @Autowired
    private ChatMessageService chatMessageService;
    
    /**
     * 发送聊天消息
     * @param chatMessage 聊天消息对象
     * @return 响应结果
     */
    @PostMapping("/send")
    public Result<?> sendMessage(@RequestBody ChatMessage chatMessage) {
        boolean result = chatMessageService.sendMessage(chatMessage);
        if (result) {
            return new Result<>(true, "发送消息成功");
        } else {
            return new Result<>(false, "发送消息失败");
        }
    }
    
    /**
     * 获取聊天历史记录
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @param limit 消息数量限制
     * @return 响应结果
     */
    @GetMapping("/history")
    public Result<?> getChatHistory(
            @RequestParam Integer userId1,
            @RequestParam Integer userId2,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        List<ChatMessage> messages = chatMessageService.getChatHistory(userId1, userId2, limit);
        return new Result<>(messages);
    }
    
    /**
     * 获取用户的未读消息数量
     * @param toUserId 接收者ID
     * @return 响应结果
     */
    @GetMapping("/unread/count/{toUserId}")
    public Result<?> getUnreadCount(@PathVariable Integer toUserId) {
        int count = chatMessageService.getUnreadCount(toUserId);
        return new Result<>(count);
    }
    
    /**
     * 标记消息为已读
     * @param fromUserId 发送者ID
     * @param toUserId 接收者ID
     * @return 响应结果
     */
    @PostMapping("/read")
    public Result<?> markAsRead(@RequestParam Integer fromUserId, @RequestParam Integer toUserId) {
        boolean result = chatMessageService.markAsRead(fromUserId, toUserId);
        if (result) {
            return new Result<>(true, "标记已读成功");
        } else {
            return new Result<>(false, "标记已读失败");
        }
    }
    
    /**
     * 获取用户的最近聊天记录
     * @param userId 用户ID
     * @param limit 消息数量限制
     * @return 响应结果
     */
    @GetMapping("/recent/{userId}")
    public Result<?> getRecentMessages(
            @PathVariable Integer userId,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        List<ChatMessage> messages = chatMessageService.getRecentMessages(userId, limit);
        return new Result<>(messages);
    }
    
    /**
     * 删除聊天消息
     * @param messageId 消息ID
     * @return 响应结果
     */
    @DeleteMapping("/delete/{messageId}")
    public Result<?> deleteMessage(@PathVariable Integer messageId) {
        boolean result = chatMessageService.deleteMessage(messageId);
        if (result) {
            return new Result<>(true, "删除消息成功");
        } else {
            return new Result<>(false, "删除消息失败");
        }
    }
    
    /**
     * 删除两个用户之间的所有聊天消息
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return 响应结果
     */
    @DeleteMapping("/delete")
    public Result<?> deleteConversation(
            @RequestParam Integer userId1,
            @RequestParam Integer userId2) {
        boolean result = chatMessageService.deleteConversation(userId1, userId2);
        if (result) {
            return new Result<>(true, "删除对话成功");
        } else {
            return new Result<>(false, "删除对话失败");
        }
    }
}