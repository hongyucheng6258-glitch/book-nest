package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class PaymentMethodTableDef extends TableDef {

    /**
     * 支付方式表 实体类

 @author BookApp Developer
 @since 1.0.0
     */
    public static final PaymentMethodTableDef PAYMENT_METHOD = new PaymentMethodTableDef();

    /**
     * 支付方式ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * CVV码
     */
    public final QueryColumn CVV = new QueryColumn(this, "cvv");

    /**
     * 持卡人姓名
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 用户ID
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 卡类型 1-信用卡 2-借记卡
     */
    public final QueryColumn CARD_TYPE = new QueryColumn(this, "card_type");

    /**
     * 创建时间
     */
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");

    /**
     * 是否默认支付方式 0-否 1-是
     */
    public final QueryColumn IS_DEFAULT = new QueryColumn(this, "is_default");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATED_AT = new QueryColumn(this, "updated_at");

    /**
     * 卡号
     */
    public final QueryColumn CARD_NUMBER = new QueryColumn(this, "card_number");

    /**
     * 到期年份
     */
    public final QueryColumn EXPIRED_YEAR = new QueryColumn(this, "expired_year");

    /**
     * 到期月份
     */
    public final QueryColumn EXPIRED_MONTH = new QueryColumn(this, "expired_month");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CVV, NAME, USER_ID, CARD_TYPE, CREATED_AT, IS_DEFAULT, UPDATED_AT, CARD_NUMBER, EXPIRED_YEAR, EXPIRED_MONTH};

    public PaymentMethodTableDef() {
        super("", "payment_method");
    }

    private PaymentMethodTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public PaymentMethodTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new PaymentMethodTableDef("", "payment_method", alias));
    }

}
