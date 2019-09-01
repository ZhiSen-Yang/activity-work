package com.example.demo.feign;

import com.example.demo.feign.impl.FeiginServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Component
@FeignClient(value = "activity-api",url = "http://localhost:8080/activityApi",fallback = FeiginServiceImpl.class)
public interface FeiginService {

    @PostMapping(value = "/model/startProcess")
    String startProcess(@RequestParam(value = "key") String key, @RequestParam(value = "businessKey") String businessKey, @RequestBody Map<String,String> map);

    @GetMapping(value = "/model/test")
    String test(@RequestParam(value = "key") String key, @RequestParam(value = "businessKey") String businessKey);
}
