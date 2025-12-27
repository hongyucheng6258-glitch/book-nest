package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class BookTableDef extends TableDef {

    /**
     * 书籍表 实体类。

 @author BookApp Developer
 @since 1.0.0
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
     * 库存数量
     */
    public final QueryColumn STOCK = new QueryColumn(this, "stock");

    /**
     * 作者
     */
    public final QueryColumn AUTHOR = new QueryColumn(this, "author");

    /**
     * 书籍图片URL
     */
    public final QueryColumn IMAGE_URL = new QueryColumn(this, "image_url");

    /**
     * 出版社
     */
    public final QueryColumn PUBLISHER = new QueryColumn(this, "publisher");

    /**
     * 已卖数量
     */
    public final QueryColumn SOLD_COUNT = new QueryColumn(this, "sold_count");

    /**
     * 所属分类ID（关联category表）
     */
    public final QueryColumn CATEGORY_ID = new QueryColumn(this, "category_id");

    /**
     * 书籍详情描述
     */
    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, NAME, PRICE, STOCK, AUTHOR, IMAGE_URL, PUBLISHER, SOLD_COUNT, CATEGORY_ID, DESCRIPTION};

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
