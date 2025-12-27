package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class CommentTableDef extends TableDef {

    /**
     * 评论表 实体类

 @author BookApp Developer
 @since 1.0.0
     */
    public static final CommentTableDef COMMENT = new CommentTableDef();

    /**
     * 评论ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 书籍ID
     */
    public final QueryColumn BOOK_ID = new QueryColumn(this, "book_id");

    /**
     * 用户ID
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 评论内容
     */
    public final QueryColumn CONTENT = new QueryColumn(this, "content");

    public final QueryColumn IS_LIKED = new QueryColumn(this, "is_liked");

    public final QueryColumn BOOK_NAME = new QueryColumn(this, "book_name");

    /**
     * 父评论ID，用于嵌套回复
     */
    public final QueryColumn PARENT_ID = new QueryColumn(this, "parent_id");

    public final QueryColumn USER_NAME = new QueryColumn(this, "user_name");

    /**
     * 创建时间
     */
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");

    /**
     * 点赞数量
     */
    public final QueryColumn LIKE_COUNT = new QueryColumn(this, "like_count");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATED_AT = new QueryColumn(this, "updated_at");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, BOOK_ID, USER_ID, CONTENT, IS_LIKED, BOOK_NAME, PARENT_ID, USER_NAME, CREATED_AT, LIKE_COUNT, UPDATED_AT};

    public CommentTableDef() {
        super("", "comment");
    }

    private CommentTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public CommentTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new CommentTableDef("", "comment", alias));
    }

}
