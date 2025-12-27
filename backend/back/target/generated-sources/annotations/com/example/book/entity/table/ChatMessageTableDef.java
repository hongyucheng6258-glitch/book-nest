package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class ChatMessageTableDef extends TableDef {

    /**
     * 聊天消息表 实体类

 @author BookApp Developer
 @since 1.0.0
     */
    public static final ChatMessageTableDef CHAT_MESSAGE = new ChatMessageTableDef();

    /**
     * 消息ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 是否已读：0-未读，1-已读
     */
    public final QueryColumn IS_READ = new QueryColumn(this, "is_read");

    /**
     * 消息内容
     */
    public final QueryColumn CONTENT = new QueryColumn(this, "content");

    /**
     * 接收者用户ID
     */
    public final QueryColumn TO_USER_ID = new QueryColumn(this, "to_user_id");

    /**
     * 创建时间
     */
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, IS_READ, CONTENT, TO_USER_ID, CREATED_AT, FROM_USER_ID, TO_USERNAME, FROM_USERNAME};

    public ChatMessageTableDef() {
        super("", "chat_message");
    }

    private ChatMessageTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public ChatMessageTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new ChatMessageTableDef("", "chat_message", alias));
    }

}
