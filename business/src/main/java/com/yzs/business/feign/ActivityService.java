package com.yzs.business.feign;

import com.yzs.business.feign.impl.ActivityServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Component
@FeignClient(value = "activity-api", fallback = ActivityServiceImpl.class)
public interface ActivityService {

    @PostMapping(value = "/model/startProcess")
    String startProcess(@RequestParam(value = "key") String key,@RequestParam(value = "businessKey") String businessKey,@RequestParam(value = "map") Map<String,String> map);
}
