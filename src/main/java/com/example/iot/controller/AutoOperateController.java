package com.example.iot.controller;

import com.example.iot.service.AutoOperateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AutoOperateController {
    @Autowired
    AutoOperateService autoOperateService;

    @RequestMapping("/environment")
    public void autoOperate(@RequestParam(value = "username",required = true) String username,@RequestParam(value = "time",required = true) String time,@RequestParam(value = "temperature",required = true) String temperature,@RequestParam(value = "humidity",required = true) String humidity,@RequestParam(value = "ownerState",required = true) String ownerState,@RequestParam(value = "timeInterval",required = true) String timeInterval){
        autoOperateService.autoOperate(username,time,temperature,humidity,ownerState,timeInterval);
    }
}