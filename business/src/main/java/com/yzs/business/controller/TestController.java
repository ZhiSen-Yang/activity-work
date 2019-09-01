package com.yzs.business.controller;


import com.yzs.business.feign.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private ActivityService activityService;
    @GetMapping("/test")
    public String test(){

       String mes= activityService.startProcess("12513","1",null);
        return mes;
    }
    @GetMapping("/tests")
    public String tests(){


        return "22222222";
    }
    @GetMapping("/ssss")
    public String ssss(){
        String mes= activityService.getDemandByUserId("10005",0,5);
        return mes;
    }
}
