package com.longmai.datademo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long id;

    private String userName;

    private String nickName;

    private String email;

    private String phone;

    private String gender;

    private String avatarPath;

    private String password;

    private Integer authMethod;

    private Boolean isAdmin;

    private String dn;

    private String cert;

    private Boolean enabled;

    private Long createTime;
}
