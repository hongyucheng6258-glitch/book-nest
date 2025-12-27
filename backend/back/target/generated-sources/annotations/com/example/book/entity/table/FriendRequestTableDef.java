package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class FriendRequestTableDef extends TableDef {

    /**
     * 好友请求表 实体类

 @author BookApp Developer
 @since 1.0.0
     */
    public static final FriendRequestTableDef FRIEND_REQUEST = new FriendRequestTableDef();

    /**
     * 请求ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 请求状态：1-待处理，2-已接受，3-已拒绝
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 接收者用户ID
     */
    public final QueryColumn TO_USER_ID = new QueryColumn(this, "to_user_id");

    /**
     * 创建时间
     */
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATED_AT = new QueryColumn(this, "updated_at");

    /**
     * 发送者用户ID
     */
    public final QueryColumn FROM_USER_ID = new QueryColumn(this, "from_user_id");

    public final QueryColumn TO_USERNAME = new QueryColumn(this, "to_username");

    public final QueryColumn FROM_USERNAME = new QueryColumn(this, "from_username");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, STATUS, TO_USER_ID, CREATED_AT, UPDATED_AT, FROM_USER_ID, TO_USERNAME, FROM_USERNAME};

    public FriendRequestTableDef() {
        super("", "friend_request");
    }

    private FriendRequestTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FriendRequestTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FriendRequestTableDef("", "friend_request", alias));
    }

}
