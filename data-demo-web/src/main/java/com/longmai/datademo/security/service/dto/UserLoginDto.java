package com.longmai.datademo.security.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto {

    private String userName;

    private String password;

    private boolean enabled;

    private Boolean isAdmin;
}
