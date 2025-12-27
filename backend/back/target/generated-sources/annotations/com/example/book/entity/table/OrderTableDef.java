package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class OrderTableDef extends TableDef {

    /**
     * 订单表 实体类。

 @author BookApp Developer
 @since 1.0.0
     */
    public static final OrderTableDef ORDER = new OrderTableDef();

    /**
     * 订单ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 订单状态（0: 待付款, 1: 待发货, 2: 已发货, 3: 已完成, 4: 已取消）
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 用户ID
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    public final QueryColumn USERNAME = new QueryColumn(this, "username");

    /**
     * 创建时间
     */
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATED_AT = new QueryColumn(this, "updated_at");

    /**
     * 总金额
     */
    public final QueryColumn TOTAL_PRICE = new QueryColumn(this, "total_price");

    /**
     * 用户订单序号（每个用户的订单号从1开始计算）
     */
    public final QueryColumn USER_ORDER_NUM = new QueryColumn(this, "user_order_num");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, STATUS, USER_ID, USERNAME, CREATED_AT, UPDATED_AT, TOTAL_PRICE, USER_ORDER_NUM};

    public OrderTableDef() {
        super("", "order");
    }

    private OrderTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public OrderTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new OrderTableDef("", "order", alias));
    }

}
