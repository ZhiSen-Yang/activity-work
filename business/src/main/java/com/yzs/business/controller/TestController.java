package com.yzs.business.controller;


import com.yzs.business.feign.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
