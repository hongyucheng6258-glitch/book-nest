package com.example.book.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class UserInfoTableDef extends TableDef {

    /**
     * 用户详情表 实体类。

 @author BookApp Developer
 @since 1.0.0
     */
    public static final UserInfoTableDef USER_INFO = new UserInfoTableDef();

    /**
     * 用户详情ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 邮箱
     */
    public final QueryColumn EMAIL = new QueryColumn(this, "email");

    /**
     * 手机号
     */
    public final QueryColumn PHONE = new QueryColumn(this, "phone");

    /**
     * 头像URL
     */
    public final QueryColumn AVATAR = new QueryColumn(this, "avatar");

    /**
     * 性别（0: 未知, 1: 男, 2: 女）
     */
    public final QueryColumn GENDER = new QueryColumn(this, "gender");

    /**
     * 用户ID
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 生日
     */
    public final QueryColumn BIRTHDAY = new QueryColumn(this, "birthday");

    /**
     * 昵称
     */
    public final QueryColumn NICKNAME = new QueryColumn(this, "nickname");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, EMAIL, PHONE, AVATAR, GENDER, USER_ID, BIRTHDAY, NICKNAME, CREATED_AT, UPDATED_AT};

    public UserInfoTableDef() {
        super("", "user_info");
    }

    private UserInfoTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public UserInfoTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new UserInfoTableDef("", "user_info", alias));
    }

}
