package com.example.book.service;

import com.example.book.entity.PaymentMethod;

import java.util.List;

/**
 * 支付方式表 Service 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface PaymentMethodService {
    // 根据用户ID查询支付方式列表
    List<PaymentMethod> getPaymentMethodListByUserId(Integer userId);

    // 根据ID查询支付方式
    PaymentMethod getPaymentMethodById(Integer id);

    // 添加支付方式
    PaymentMethod addPaymentMethod(PaymentMethod paymentMethod);

    // 更新支付方式
    int updatePaymentMethod(PaymentMethod paymentMethod);

    // 删除支付方式
    int deletePaymentMethod(Integer id);

    // 设置默认支付方式
    int setDefaultPaymentMethod(Integer userId, Integer paymentMethodId);
}