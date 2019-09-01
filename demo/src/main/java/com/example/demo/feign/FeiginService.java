package com.example.demo.feign;

import com.example.demo.feign.impl.FeiginServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Component
@FeignClient(value = "activity-api",fallback = FeiginServiceImpl.class)
public interface FeiginService {

    @PostMapping(value = "/model/startProcess")
    String startProcess(@RequestParam(value = "key") String key, @RequestParam(value = "businessKey") String businessKey, @RequestParam(value = "map") Map<String,String> map);


}
