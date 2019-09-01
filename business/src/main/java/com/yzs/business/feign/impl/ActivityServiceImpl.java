package com.yzs.business.feign.impl;

import com.yzs.business.feign.ActivityService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {
    final String PRECONDITION_ERROR_MESSAGE="访问工作流微服务失败：请求失败";

    @Override
    public String startProcess(String key, String businessKey, Map<String, String> map) {
        throw new IcException(PRECONDITION_ERROR_MESSAGE);
    }

    @Override
    public String getDemandByUserId(String userName, int pageNum, int pageSize) {
        throw new IcException(PRECONDITION_ERROR_MESSAGE);

    }
}
