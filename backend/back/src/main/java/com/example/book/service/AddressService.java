package com.example.book.service;

import com.example.book.entity.Address;

import java.util.List;

/**
 * 地址表 Service 接口
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
public interface AddressService {
    // 根据用户ID查询地址列表
    List<Address> getAddressListByUserId(Integer userId);

    // 根据ID查询地址
    Address getAddressById(Integer id);

    // 添加地址
    Address addAddress(Address address);

    // 更新地址
    int updateAddress(Address address);

    // 删除地址
    int deleteAddress(Integer id);

    // 设置默认地址
    int setDefaultAddress(Integer userId, Integer addressId);
}