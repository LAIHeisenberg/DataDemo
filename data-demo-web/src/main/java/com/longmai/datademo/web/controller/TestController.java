package com.longmai.datademo.web.controller;

import com.longmai.datademo.annotation.AnonymousAccess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")

public class TestController {

    @RequestMapping("/index")
    @AnonymousAccess
    public String index(){
        return "index";
    }

}
