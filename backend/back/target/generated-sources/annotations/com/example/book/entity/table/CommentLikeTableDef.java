package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class CommentLikeTableDef extends TableDef {

    /**
     * 评论点赞表 实体类

 @author BookApp Developer
 @since 1.0.0
     */
    public static final CommentLikeTableDef COMMENT_LIKE = new CommentLikeTableDef();

    /**
     * 点赞ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 用户ID
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 评论ID
     */
    public final QueryColumn COMMENT_ID = new QueryColumn(this, "comment_id");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USER_ID, COMMENT_ID, CREATED_AT};

    public CommentLikeTableDef() {
        super("", "comment_like");
    }

    private CommentLikeTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public CommentLikeTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new CommentLikeTableDef("", "comment_like", alias));
    }

}
