package com.imustsz.cilent.service.impl;

import com.imustsz.cilent.domain.enums.Status;
import com.imustsz.cilent.domain.vo.PendingTaskVO;
import com.imustsz.cilent.mapper.ClientTaskMapper;
import com.imustsz.cilent.service.IClientTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientTaskServiceImpl implements IClientTaskService {

    @Autowired
    private ClientTaskMapper clientTaskMapper;

    @Override
    public List<PendingTaskVO> getPendingTaskList() {

        List<PendingTaskVO> pendingTaskVOS = clientTaskMapper.getPendingTaskList();

        pendingTaskVOS.forEach(pendingTaskVO -> {
            pendingTaskVO.setStatus(Status.getByCode(Integer.parseInt(pendingTaskVO.getStatus())));
        });

        return pendingTaskVOS;
    }
}
