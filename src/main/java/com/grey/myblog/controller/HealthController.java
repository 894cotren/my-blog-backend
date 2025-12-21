package com.grey.myblog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {


    @GetMapping("checkHealth")
    public String checkHealth(){
        return "I'm fine,thank you! and you?";
    }

}
