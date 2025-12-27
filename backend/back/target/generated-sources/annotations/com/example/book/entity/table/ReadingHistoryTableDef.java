package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class ReadingHistoryTableDef extends TableDef {

    /**
     * 阅读历史记录 实体类

 @author BookApp Developer
 @since 1.0.0
     */
    public static final ReadingHistoryTableDef READING_HISTORY = new ReadingHistoryTableDef();

    /**
     * 阅读记录ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 书籍ID
     */
    public final QueryColumn BOOK_ID = new QueryColumn(this, "book_id");

    /**
     * 阅读时间
     */
    public final QueryColumn READ_AT = new QueryColumn(this, "read_at");

    /**
     * 用户ID
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 最后阅读页码
     */
    public final QueryColumn LAST_PAGE = new QueryColumn(this, "last_page");

    /**
     * 阅读进度（百分比）
     */
    public final QueryColumn READING_PROGRESS = new QueryColumn(this, "reading_progress");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, BOOK_ID, READ_AT, USER_ID, LAST_PAGE, READING_PROGRESS};

    public ReadingHistoryTableDef() {
        super("", "reading_history");
    }

    private ReadingHistoryTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public ReadingHistoryTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new ReadingHistoryTableDef("", "reading_history", alias));
    }

}
