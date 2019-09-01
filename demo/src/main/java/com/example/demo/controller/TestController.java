package com.example.demo.controller;

import com.example.demo.feign.FeiginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private FeiginService feiginService;

    @RequestMapping("/aaaaa")
    public String test(){

        return "11111111";
    }
    @RequestMapping("/ssss")
    public String test1(){
        Map<String,String>  map=new HashMap<String,String>();
        String mes=feiginService.startProcess("12513","1",map);
        return mes;
    }
    @RequestMapping("/ddd")
    public String ddd(){
        String mes=feiginService.test("sdf","dsf");
        return mes;
    }
}
