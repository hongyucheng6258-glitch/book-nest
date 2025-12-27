package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class RatingTableDef extends TableDef {

    /**
     * 评分表 实体类

 @author BookApp Developer
 @since 1.0.0
     */
    public static final RatingTableDef RATING = new RatingTableDef();

    /**
     * 评分ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 书籍ID
     */
    public final QueryColumn BOOK_ID = new QueryColumn(this, "book_id");

    /**
     * 评分（1-5）
     */
    public final QueryColumn RATING_ = new QueryColumn(this, "rating");

    /**
     * 用户ID
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    public final QueryColumn BOOK_NAME = new QueryColumn(this, "book_name");

    public final QueryColumn USER_NAME = new QueryColumn(this, "user_name");

    /**
     * 创建时间
     */
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, BOOK_ID, RATING_, USER_ID, BOOK_NAME, USER_NAME, CREATED_AT, UPDATED_AT};

    public RatingTableDef() {
        super("", "rating");
    }

    private RatingTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public RatingTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new RatingTableDef("", "rating", alias));
    }

}
