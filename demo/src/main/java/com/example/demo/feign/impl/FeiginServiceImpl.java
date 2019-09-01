package com.example.demo.feign.impl;

import com.example.demo.feign.FeiginService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FeiginServiceImpl implements FeiginService {
    @Override
    public String startProcess(String key, String businessKey, Map<String, String> map) {
        return null;
    }
}
