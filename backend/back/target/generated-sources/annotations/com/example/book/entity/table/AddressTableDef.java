package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class AddressTableDef extends TableDef {

    /**
     * 地址表 实体类

 @author BookApp Developer
 @since 1.0.0
     */
    public static final AddressTableDef ADDRESS = new AddressTableDef();

    /**
     * 地址ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 城市
     */
    public final QueryColumn CITY = new QueryColumn(this, "city");

    /**
     * 收货人姓名
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 联系电话
     */
    public final QueryColumn PHONE = new QueryColumn(this, "phone");

    /**
     * 详细地址
     */
    public final QueryColumn DETAIL = new QueryColumn(this, "detail");

    /**
     * 用户ID
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 区县
     */
    public final QueryColumn DISTRICT = new QueryColumn(this, "district");

    /**
     * 省份
     */
    public final QueryColumn PROVINCE = new QueryColumn(this, "province");

    /**
     * 创建时间
     */
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");

    /**
     * 是否默认地址 0-否 1-是
     */
    public final QueryColumn IS_DEFAULT = new QueryColumn(this, "is_default");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CITY, NAME, PHONE, DETAIL, USER_ID, DISTRICT, PROVINCE, CREATED_AT, IS_DEFAULT, UPDATED_AT};

    public AddressTableDef() {
        super("", "address");
    }

    private AddressTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AddressTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AddressTableDef("", "address", alias));
    }

}
