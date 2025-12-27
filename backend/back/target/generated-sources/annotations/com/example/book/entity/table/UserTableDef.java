package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class UserTableDef extends TableDef {

    /**
     * 用户表 实体类。

 @author BookApp Developer
 @since 1.0.0
     */
    public static final UserTableDef USER = new UserTableDef();

    /**
     * 用户ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 密码
     */
    public final QueryColumn PASSWORD = new QueryColumn(this, "password");

    /**
     * 用户名
     */
    public final QueryColumn USERNAME = new QueryColumn(this, "username");

    /**
     * 权限（1: 管理员, 0: 普通用户）
     */
    public final QueryColumn PRIVILEGE = new QueryColumn(this, "privilege");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, PASSWORD, USERNAME, PRIVILEGE};

    public UserTableDef() {
        super("", "user");
    }

    private UserTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public UserTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new UserTableDef("", "user", alias));
    }

}
