package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class OrderItemTableDef extends TableDef {

    /**
     * 订单详情表 实体类。

 @author BookApp Developer
 @since 1.0.0
     */
    public static final OrderItemTableDef ORDER_ITEM = new OrderItemTableDef();

    /**
     * 订单详情ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 单价
     */
    public final QueryColumn PRICE = new QueryColumn(this, "price");

    /**
     * 书籍ID
     */
    public final QueryColumn BOOK_ID = new QueryColumn(this, "book_id");

    /**
     * 订单ID
     */
    public final QueryColumn ORDER_ID = new QueryColumn(this, "order_id");

    /**
     * 数量
     */
    public final QueryColumn QUANTITY = new QueryColumn(this, "quantity");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, PRICE, BOOK_ID, ORDER_ID, QUANTITY};

    public OrderItemTableDef() {
        super("", "order_item");
    }

    private OrderItemTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public OrderItemTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new OrderItemTableDef("", "order_item", alias));
    }

}
