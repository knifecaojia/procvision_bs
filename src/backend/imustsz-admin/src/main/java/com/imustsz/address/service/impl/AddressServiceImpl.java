package com.imustsz.address.service.impl;

import java.util.List;
import com.imustsz.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.address.mapper.AddressMapper;
import com.imustsz.address.domain.Address;
import com.imustsz.address.service.IAddressService;

/**
 * 地址信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-30
 */
@Service
public class AddressServiceImpl implements IAddressService 
{
    @Autowired
    private AddressMapper addressMapper;

    /**
     * 查询地址信息
     * 
     * @param id 地址信息主键
     * @return 地址信息
     */
    @Override
    public Address selectAddressById(Long id)
    {
        return addressMapper.selectAddressById(id);
    }

    /**
     * 查询地址信息列表
     * 
     * @param address 地址信息
     * @return 地址信息
     */
    @Override
    public List<Address> selectAddressList(Address address)
    {
        return addressMapper.selectAddressList(address);
    }

    /**
     * 新增地址信息
     * 
     * @param address 地址信息
     * @return 结果
     */
    @Override
    public int insertAddress(Address address)
    {
        address.setCreateTime(DateUtils.getNowDate());
        return addressMapper.insertAddress(address);
    }

    /**
     * 修改地址信息
     * 
     * @param address 地址信息
     * @return 结果
     */
    @Override
    public int updateAddress(Address address)
    {
        address.setUpdateTime(DateUtils.getNowDate());
        return addressMapper.updateAddress(address);
    }

    /**
     * 批量删除地址信息
     * 
     * @param ids 需要删除的地址信息主键
     * @return 结果
     */
    @Override
    public int deleteAddressByIds(Long[] ids)
    {
        return addressMapper.deleteAddressByIds(ids);
    }

    /**
     * 删除地址信息信息
     * 
     * @param id 地址信息主键
     * @return 结果
     */
    @Override
    public int deleteAddressById(Long id)
    {
        return addressMapper.deleteAddressById(id);
    }
}
