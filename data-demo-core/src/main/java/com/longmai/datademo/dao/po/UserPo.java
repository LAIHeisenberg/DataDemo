package com.longmai.datademo.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;


@Data
@TableName("ddm_user")
public class UserPo {

    @Id
    protected Long id;

    protected String userName;

    protected String nickName;

    protected String email;

    protected String phone;

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
