package com.example.book.service.impl;

import com.example.book.entity.PaymentMethod;
import com.example.book.mapper.PaymentMethodMapper;
import com.example.book.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 支付方式表 Service 实现类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    @Autowired
    private PaymentMethodMapper paymentMethodMapper;

    /**
     * 根据用户ID查询支付方式列表
     */
    @Override
    public List<PaymentMethod> getPaymentMethodListByUserId(Integer userId) {
        return paymentMethodMapper.selectByUserId(userId);
    }

    /**
     * 根据ID查询支付方式
     */
    @Override
    public PaymentMethod getPaymentMethodById(Integer id) {
        return paymentMethodMapper.selectById(id);
    }

    /**
     * 添加支付方式
     */
    @Override
    @Transactional
    public PaymentMethod addPaymentMethod(PaymentMethod paymentMethod) {
        // 如果是默认支付方式，先取消其他默认支付方式
        if (paymentMethod.getIsDefault() == 1) {
            paymentMethodMapper.cancelDefaultPaymentMethod(paymentMethod.getUserId());
        }
        // 插入支付方式
        paymentMethodMapper.insert(paymentMethod);
        return paymentMethod;
    }

    /**
     * 更新支付方式
     */
    @Override
    @Transactional
    public int updatePaymentMethod(PaymentMethod paymentMethod) {
        // 如果是默认支付方式，先取消其他默认支付方式
        if (paymentMethod.getIsDefault() == 1) {
            paymentMethodMapper.cancelDefaultPaymentMethod(paymentMethod.getUserId());
        }
        // 更新支付方式
        return paymentMethodMapper.update(paymentMethod);
    }

    /**
     * 删除支付方式
     */
    @Override
    public int deletePaymentMethod(Integer id) {
        return paymentMethodMapper.deleteById(id);
    }

    /**
     * 设置默认支付方式
     */
    @Override
    @Transactional
    public int setDefaultPaymentMethod(Integer userId, Integer paymentMethodId) {
        // 先取消所有默认支付方式
        paymentMethodMapper.cancelDefaultPaymentMethod(userId);
        // 设置当前支付方式为默认支付方式
        return paymentMethodMapper.setDefaultPaymentMethod(userId, paymentMethodId);
    }
}