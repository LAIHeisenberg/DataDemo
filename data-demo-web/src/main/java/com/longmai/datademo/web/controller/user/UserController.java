package com.longmai.datademo.web.controller.user;

import com.longmai.datademo.web.controller.user.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserFacade userFacade;

    @PreAuthorize("@el.check('user:list')")
    @GetMapping
    public ResponseEntity<Object> listAllUser(){

        return new ResponseEntity<>(userFacade.listAllUser(), HttpStatus.OK);
    }


}
