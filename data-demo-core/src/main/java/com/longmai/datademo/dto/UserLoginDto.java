package com.longmai.datademo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto {

    private Long id;
    private String userName;
    private String password;
    private Boolean enabled;
    private Boolean adminFlag;

    public boolean isAdmin(){
        return adminFlag;
    }


}
