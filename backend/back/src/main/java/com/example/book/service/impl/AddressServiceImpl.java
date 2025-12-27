package com.example.book.service.impl;

import com.example.book.entity.Address;
import com.example.book.mapper.AddressMapper;
import com.example.book.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地址表 Service 实现类
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 根据用户ID查询地址列表
     */
    @Override
    public List<Address> getAddressListByUserId(Integer userId) {
        return addressMapper.selectByUserId(userId);
    }

    /**
     * 根据ID查询地址
     */
    @Override
    public Address getAddressById(Integer id) {
        return addressMapper.selectById(id);
    }

    /**
     * 添加地址
     */
    @Override
    @Transactional
    public Address addAddress(Address address) {
        // 如果是默认地址，先取消其他默认地址
        if (address.getIsDefault() == 1) {
            addressMapper.cancelDefaultAddress(address.getUserId());
        }
        // 插入地址
        addressMapper.insert(address);
        return address;
    }

    /**
     * 更新地址
     */
    @Override
    @Transactional
    public int updateAddress(Address address) {
        // 如果是默认地址，先取消其他默认地址
        if (address.getIsDefault() == 1) {
            addressMapper.cancelDefaultAddress(address.getUserId());
        }
        // 更新地址
        return addressMapper.update(address);
    }

    /**
     * 删除地址
     */
    @Override
    public int deleteAddress(Integer id) {
        return addressMapper.deleteById(id);
    }

    /**
     * 设置默认地址
     */
    @Override
    @Transactional
    public int setDefaultAddress(Integer userId, Integer addressId) {
        // 先取消所有默认地址
        addressMapper.cancelDefaultAddress(userId);
        // 设置当前地址为默认地址
        return addressMapper.setDefaultAddress(userId, addressId);
    }
}