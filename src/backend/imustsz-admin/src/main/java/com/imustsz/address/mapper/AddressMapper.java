package com.imustsz.address.mapper;

import java.util.List;
import com.imustsz.address.domain.Address;

/**
 * 地址信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-30
 */
public interface AddressMapper 
{
    /**
     * 查询地址信息
     * 
     * @param id 地址信息主键
     * @return 地址信息
     */
    public Address selectAddressById(Long id);

    /**
     * 查询地址信息列表
     * 
     * @param address 地址信息
     * @return 地址信息集合
     */
    public List<Address> selectAddressList(Address address);

    /**
     * 新增地址信息
     * 
     * @param address 地址信息
     * @return 结果
     */
    public int insertAddress(Address address);

    /**
     * 修改地址信息
     * 
     * @param address 地址信息
     * @return 结果
     */
    public int updateAddress(Address address);

    /**
     * 删除地址信息
     * 
     * @param id 地址信息主键
     * @return 结果
     */
    public int deleteAddressById(Long id);

    /**
     * 批量删除地址信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAddressByIds(Long[] ids);
}
