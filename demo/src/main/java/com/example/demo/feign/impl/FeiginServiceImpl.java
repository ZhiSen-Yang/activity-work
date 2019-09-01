package com.example.demo.feign.impl;

import com.example.demo.feign.FeiginService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FeiginServiceImpl implements FeiginService {
    final String PRECONDITION_ERROR_MESSAGE="访问前提条件微服务失败：请求失败";

    @Override
    public String startProcess(String key, String businessKey, Map<String, String> map) {
        throw new IcException(PRECONDITION_ERROR_MESSAGE);
    }

    @Override
    public String test(String key, String businessKey) {
        throw new IcException(PRECONDITION_ERROR_MESSAGE);
    }
}
