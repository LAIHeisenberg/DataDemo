package com.longmai.datademo.web.controller.user.view;

import lombok.Data;

@Data
public class UserView {

    private Long id;
    private String userName;
    private String nickName;
    private String phone;
    private String email;
    private Boolean enabled;
    private Integer gender;
    private String authMethod;
    private String createTimeDesc;

}
