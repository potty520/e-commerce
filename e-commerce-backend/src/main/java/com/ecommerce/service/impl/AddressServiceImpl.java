package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ecommerce.common.BusinessException;
import com.ecommerce.dto.AddressDTO;
import com.ecommerce.entity.Address;
import com.ecommerce.mapper.AddressMapper;
import com.ecommerce.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public void addAddress(Long userId, AddressDTO dto) {
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }
        Address address = new Address();
        BeanUtils.copyProperties(dto, address);
        address.setUserId(userId);
        addressMapper.insert(address);
    }

    @Override
    public void updateAddress(Long userId, AddressDTO dto) {
        Address address = addressMapper.selectById(dto.getId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("Address not found");
        }
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }
        BeanUtils.copyProperties(dto, address);
        addressMapper.updateById(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("Address not found");
        }
        addressMapper.deleteById(addressId);
    }

    @Override
    public List<Address> getAddressList(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId).orderByDesc(Address::getIsDefault).orderByDesc(Address::getId);
        return addressMapper.selectList(wrapper);
    }

    @Override
    public Address getDefaultAddress(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId).eq(Address::getIsDefault, 1);
        return addressMapper.selectOne(wrapper);
    }

    @Override
    public void setDefaultAddress(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("Address not found");
        }
        clearDefaultAddress(userId);
        LambdaUpdateWrapper<Address> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Address::getId, addressId).set(Address::getIsDefault, 1);
        addressMapper.update(null, wrapper);
    }

    private void clearDefaultAddress(Long userId) {
        LambdaUpdateWrapper<Address> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Address::getUserId, userId).set(Address::getIsDefault, 0);
        addressMapper.update(null, wrapper);
    }
}
