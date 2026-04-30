package com.imustsz.collect.service.impl;

import com.imustsz.collect.domain.BizDataset;
import com.imustsz.collect.mapper.BizDatasetMapper;
import com.imustsz.collect.service.IBizDatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BizDatasetServiceImpl implements IBizDatasetService {

    @Autowired
    private BizDatasetMapper bizDatasetMapper;

    @Override
    public BizDataset selectBizDatasetById(Long id) {
        return bizDatasetMapper.selectBizDatasetById(id);
    }

    @Override
    public List<BizDataset> selectBizDatasetList(BizDataset BizDataset) {
        return bizDatasetMapper.selectBizDatasetList(BizDataset);
    }

    @Override
    public int insertBizDataset(BizDataset BizDataset) {
        return bizDatasetMapper.insertBizDataset(BizDataset);
    }

    @Override
    public int updateBizDataset(BizDataset BizDataset) {
        return bizDatasetMapper.updateBizDataset(BizDataset);
    }

    @Override
    public int deleteBizDatasetByIds(Long[] ids) {
        return bizDatasetMapper.deleteBizDatasetByIds(ids);
    }

    @Override
    public int deleteBizDatasetById(Long id) {
        return bizDatasetMapper.deleteBizDatasetById(id);
    }
}
