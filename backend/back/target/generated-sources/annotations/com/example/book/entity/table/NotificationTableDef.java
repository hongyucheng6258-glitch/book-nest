package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class NotificationTableDef extends TableDef {

    /**
     * 通知实体类

 @author BookApp Developer
 @since 1.0.0
     */
    public static final NotificationTableDef NOTIFICATION = new NotificationTableDef();

    /**
     * 通知ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 通知类型（0: 订单通知）
     */
    public final QueryColumn TYPE = new QueryColumn(this, "type");

    /**
     * 状态（0: 未读, 1: 已读）
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 用户ID
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 通知内容
     */
    public final QueryColumn CONTENT = new QueryColumn(this, "content");

    /**
     * 订单ID
     */
    public final QueryColumn ORDER_ID = new QueryColumn(this, "order_id");

    /**
     * 创建时间
     */
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, TYPE, STATUS, USER_ID, CONTENT, ORDER_ID, CREATED_AT};

    public NotificationTableDef() {
        super("", "notification");
    }

    private NotificationTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public NotificationTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new NotificationTableDef("", "notification", alias));
    }

}
