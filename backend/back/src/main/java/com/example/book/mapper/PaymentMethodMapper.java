package com.example.book.mapper;

import com.example.book.entity.PaymentMethod;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 支付方式表 Mapper 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface PaymentMethodMapper {
    // 根据用户ID查询支付方式列表
    List<PaymentMethod> selectByUserId(Integer userId);

    // 根据ID查询支付方式
    PaymentMethod selectById(Integer id);

    // 插入支付方式
    int insert(PaymentMethod paymentMethod);

    // 更新支付方式
    int update(PaymentMethod paymentMethod);

    // 删除支付方式
    int deleteById(Integer id);

    // 设置默认支付方式
    int setDefaultPaymentMethod(Integer userId, Integer id);

    // 取消默认支付方式
    int cancelDefaultPaymentMethod(Integer userId);
}