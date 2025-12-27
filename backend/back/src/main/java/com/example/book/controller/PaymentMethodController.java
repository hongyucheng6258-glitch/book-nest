package com.example.book.controller;

import com.example.book.common.BusinessException;
import com.example.book.common.Result;
import com.example.book.entity.PaymentMethod;
import com.example.book.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付方式表 Controller
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/payment-method")
@CrossOrigin
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    /**
     * 获取用户支付方式列表
     *
     * @param userId 用户ID
     * @return 支付方式列表
     */
    @GetMapping("/list")
    public Result<List<PaymentMethod>> getPaymentMethodList(@RequestParam Integer userId) {
        try {
            List<PaymentMethod> paymentMethodList = paymentMethodService.getPaymentMethodListByUserId(userId);
            return new Result<>(true, paymentMethodList, "查询成功");
        } catch (Exception e) {
            throw new BusinessException("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取支付方式详情
     *
     * @param id 支付方式ID
     * @return 支付方式详情
     */
    @GetMapping("/{id}")
    public Result<PaymentMethod> getPaymentMethodById(@PathVariable Integer id) {
        try {
            PaymentMethod paymentMethod = paymentMethodService.getPaymentMethodById(id);
            return new Result<>(true, paymentMethod, "查询成功");
        } catch (Exception e) {
            throw new BusinessException("查询失败: " + e.getMessage());
        }
    }

    /**
     * 添加支付方式
     *
     * @param paymentMethod 支付方式信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<PaymentMethod> addPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        try {
            PaymentMethod newPaymentMethod = paymentMethodService.addPaymentMethod(paymentMethod);
            return new Result<>(true, newPaymentMethod, "添加成功");
        } catch (Exception e) {
            throw new BusinessException("添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新支付方式
     *
     * @param id            支付方式ID
     * @param paymentMethod 支付方式信息
     * @return 操作结果
     */
    @PutMapping("/update/{id}")
    public Result<Integer> updatePaymentMethod(@PathVariable Integer id, @RequestBody PaymentMethod paymentMethod) {
        try {
            paymentMethod.setId(id);
            int result = paymentMethodService.updatePaymentMethod(paymentMethod);
            return new Result<>(true, result, "更新成功");
        } catch (Exception e) {
            throw new BusinessException("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除支付方式
     *
     * @param id 支付方式ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Integer> deletePaymentMethod(@PathVariable Integer id) {
        try {
            int result = paymentMethodService.deletePaymentMethod(id);
            return new Result<>(true, result, "删除成功");
        } catch (Exception e) {
            throw new BusinessException("删除失败: " + e.getMessage());
        }
    }

    /**
     * 设置默认支付方式
     *
     * @param userId           用户ID
     * @param paymentMethodId 支付方式ID
     * @return 操作结果
     */
    @PutMapping("/set-default")
    public Result<Integer> setDefaultPaymentMethod(
            @RequestParam Integer userId,
            @RequestParam Integer paymentMethodId
    ) {
        try {
            int result = paymentMethodService.setDefaultPaymentMethod(userId, paymentMethodId);
            return new Result<>(true, result, "设置成功");
        } catch (Exception e) {
            throw new BusinessException("设置失败: " + e.getMessage());
        }
    }
}