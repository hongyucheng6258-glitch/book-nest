package com.example.book.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 * 书籍表 表定义层。
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public class BookTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 书籍表
     */
    public static final BookTableDef BOOK = new BookTableDef();

    /**
     * 书籍ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 书名
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 价格
     */
    public final QueryColumn PRICE = new QueryColumn(this, "price");

    /**
     * 作者
     */
    public final QueryColumn AUTHOR = new QueryColumn(this, "author");

    /**
     * 出版社
     */
    public final QueryColumn PUBLISHER = new QueryColumn(this, "publisher");

    /**
     * 所属分类ID（关联category表）
     */
    public final QueryColumn CATEGORY_ID = new QueryColumn(this, "category_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, PUBLISHER, AUTHOR, PRICE, CATEGORY_ID};

    public BookTableDef() {
        super("", "book");
    }

    private BookTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BookTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BookTableDef("", "book", alias));
    }

}
