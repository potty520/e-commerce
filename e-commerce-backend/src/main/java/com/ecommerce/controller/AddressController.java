package com.ecommerce.controller;

import com.ecommerce.common.Result;
import com.ecommerce.dto.AddressDTO;
import com.ecommerce.entity.Address;
import com.ecommerce.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/add")
    public Result<Void> addAddress(@RequestAttribute Long userId, @Valid @RequestBody AddressDTO dto) {
        addressService.addAddress(userId, dto);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> updateAddress(@RequestAttribute Long userId, @RequestBody AddressDTO dto) {
        addressService.updateAddress(userId, dto);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteAddress(@RequestAttribute Long userId, @PathVariable Long id) {
        addressService.deleteAddress(userId, id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Address>> getAddressList(@RequestAttribute Long userId) {
        List<Address> list = addressService.getAddressList(userId);
        return Result.success(list);
    }

    @GetMapping("/default")
    public Result<Address> getDefaultAddress(@RequestAttribute Long userId) {
        Address address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }

    @PutMapping("/default/{id}")
    public Result<Void> setDefaultAddress(@RequestAttribute Long userId, @PathVariable Long id) {
        addressService.setDefaultAddress(userId, id);
        return Result.success();
    }
}
