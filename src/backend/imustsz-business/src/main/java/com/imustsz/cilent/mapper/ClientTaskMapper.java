package com.imustsz.cilent.mapper;


import com.imustsz.cilent.domain.vo.PendingTaskVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientTaskMapper {
    public List<PendingTaskVO> getPendingTaskList();
}
