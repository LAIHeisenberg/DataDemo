package com.longmai.datademo.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;


@Data
@TableName("ddm_user")
public class UserPo {

    @Id
    private Long id;

    private String userName;

    private String nickName;

    private String email;

    private String phone;

    private String gender;

    private String password;

    private Integer authMethod;

    private Boolean adminFlag;

    private String dn;

    private String cert;

    private Boolean enabled;

    private Long createTime;

    private Long updateTime;

}
