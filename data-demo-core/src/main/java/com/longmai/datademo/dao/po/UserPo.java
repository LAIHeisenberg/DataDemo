package com.longmai.datademo.dao.po;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPo {

    private Long id;

    private String username;

    private String nickName;

    private String email;

    private String phone;

    private String gender;

    private String avatarPath;

    private String password;

    private Integer authMethod;

    private String dn;

    private String cert;

    private Boolean enabled;

    private Long createTime;

    private Long updateTime;

}
