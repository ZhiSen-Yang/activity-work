package com.yzs.business.feign;

import com.yzs.business.feign.impl.ActivityServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Component
@FeignClient(name = "activity-api",url = "http://localhost:8080/activityApi",fallback = ActivityServiceImpl.class)
public interface ActivityService {

    @PostMapping(value = "/model/startProcess")
    String startProcess(@RequestParam(value = "key") String key, @RequestParam(value = "businessKey") String businessKey, @RequestBody Map<String, String> map);

    @PostMapping(value = "/task/getDemandByUserId")
    String getDemandByUserId(@RequestParam(value = "userId")String userId,@RequestParam(value = "pageNum")int pageNum,@RequestParam(value = "pageSize")int pageSize);
}
