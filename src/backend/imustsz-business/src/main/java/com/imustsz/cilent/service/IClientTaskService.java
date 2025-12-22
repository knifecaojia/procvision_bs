package com.imustsz.cilent.service;

import com.imustsz.cilent.domain.vo.PendingTaskVO;

import java.util.List;

public interface IClientTaskService {
    List<PendingTaskVO> getPendingTaskList();
}
