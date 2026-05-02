package com.ecommerce.service;

import com.ecommerce.dto.AddressDTO;
import com.ecommerce.entity.Address;
import java.util.List;

public interface AddressService {
    void addAddress(Long userId, AddressDTO dto);
    void updateAddress(Long userId, AddressDTO dto);
    void deleteAddress(Long userId, Long addressId);
    List<Address> getAddressList(Long userId);
    Address getDefaultAddress(Long userId);
    void setDefaultAddress(Long userId, Long addressId);
}
