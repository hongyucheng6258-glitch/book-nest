package com.example.book.mapper;

import com.example.book.entity.Address;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 地址表 Mapper 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Mapper
public interface AddressMapper {
    // 根据用户ID查询地址列表
    List<Address> selectByUserId(Integer userId);

    // 根据ID查询地址
    Address selectById(Integer id);

    // 插入地址
    int insert(Address address);

    // 更新地址
    int update(Address address);

    // 删除地址
    int deleteById(Integer id);

    // 设置默认地址
    int setDefaultAddress(Integer userId, Integer id);

    // 取消默认地址
    int cancelDefaultAddress(Integer userId);
}