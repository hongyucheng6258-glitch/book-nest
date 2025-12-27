package com.example.book.controller;

import com.example.book.common.BusinessException;
import com.example.book.common.Result;
import com.example.book.entity.Address;
import com.example.book.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址表 Controller
 *
 * @author BookApp Developer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/address")
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取用户地址列表
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    @GetMapping("/list")
    public Result<List<Address>> getAddressList(@RequestParam Integer userId) {
        try {
            List<Address> addressList = addressService.getAddressListByUserId(userId);
            return new Result<>(true, addressList, "查询成功");
        } catch (Exception e) {
            throw new BusinessException("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取地址详情
     *
     * @param id 地址ID
     * @return 地址详情
     */
    @GetMapping("/{id}")
    public Result<Address> getAddressById(@PathVariable Integer id) {
        try {
            Address address = addressService.getAddressById(id);
            return new Result<>(true, address, "查询成功");
        } catch (Exception e) {
            throw new BusinessException("查询失败: " + e.getMessage());
        }
    }

    /**
     * 添加地址
     *
     * @param address 地址信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Address> addAddress(@RequestBody Address address) {
        try {
            Address newAddress = addressService.addAddress(address);
            return new Result<>(true, newAddress, "添加成功");
        } catch (Exception e) {
            throw new BusinessException("添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新地址
     *
     * @param id      地址ID
     * @param address 地址信息
     * @return 操作结果
     */
    @PutMapping("/update/{id}")
    public Result<Integer> updateAddress(@PathVariable Integer id, @RequestBody Address address) {
        try {
            address.setId(id);
            int result = addressService.updateAddress(address);
            return new Result<>(true, result, "更新成功");
        } catch (Exception e) {
            throw new BusinessException("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除地址
     *
     * @param id 地址ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Integer> deleteAddress(@PathVariable Integer id) {
        try {
            int result = addressService.deleteAddress(id);
            return new Result<>(true, result, "删除成功");
        } catch (Exception e) {
            throw new BusinessException("删除失败: " + e.getMessage());
        }
    }

    /**
     * 设置默认地址
     *
     * @param userId    用户ID
     * @param addressId 地址ID
     * @return 操作结果
     */
    @PutMapping("/set-default")
    public Result<Integer> setDefaultAddress(
            @RequestParam Integer userId,
            @RequestParam Integer addressId
    ) {
        try {
            int result = addressService.setDefaultAddress(userId, addressId);
            return new Result<>(true, result, "设置成功");
        } catch (Exception e) {
            throw new BusinessException("设置失败: " + e.getMessage());
        }
    }
}