package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class FriendTableDef extends TableDef {

    /**
     * 好友关系表 实体类

 @author BookApp Developer
 @since 1.0.0
     */
    public static final FriendTableDef FRIEND = new FriendTableDef();

    /**
     * 好友关系ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 关系状态：1-正常，2-已删除
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn AVATAR1 = new QueryColumn(this, "avatar1");

    public final QueryColumn AVATAR2 = new QueryColumn(this, "avatar2");

    /**
     * 用户1ID
     */
    public final QueryColumn USER1_ID = new QueryColumn(this, "user1_id");

    /**
     * 用户2ID
     */
    public final QueryColumn USER2_ID = new QueryColumn(this, "user2_id");

    /**
     * 创建时间
     */
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATED_AT = new QueryColumn(this, "updated_at");

    public final QueryColumn USERNAME1 = new QueryColumn(this, "username1");

    public final QueryColumn USERNAME2 = new QueryColumn(this, "username2");

    public final QueryColumn FRIEND_USERNAME = new QueryColumn(this, "friend_username");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, STATUS, AVATAR1, AVATAR2, USER1_ID, USER2_ID, CREATED_AT, UPDATED_AT, USERNAME1, USERNAME2, FRIEND_USERNAME};

    public FriendTableDef() {
        super("", "friend");
    }

    private FriendTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FriendTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FriendTableDef("", "friend", alias));
    }

}
