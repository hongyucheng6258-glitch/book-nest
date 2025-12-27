package com.example.book.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 支付方式表 实体类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("payment_method")
public class PaymentMethod implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 支付方式ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 持卡人姓名
     */
    private String name;

    /**
     * 卡号
     */
    private String cardNumber;

    /**
     * 卡类型 1-信用卡 2-借记卡
     */
    private Integer cardType;

    /**
     * 到期月份
     */
    private Integer expiredMonth;

    /**
     * 到期年份
     */
    private Integer expiredYear;

    /**
     * CVV码
     */
    private String cvv;

    /**
     * 是否默认支付方式 0-否 1-是
     */
    private Integer isDefault;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}