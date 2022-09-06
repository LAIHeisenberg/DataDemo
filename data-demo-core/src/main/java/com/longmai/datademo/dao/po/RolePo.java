package com.longmai.datademo.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ddm_role")
public class RolePo {

    private Long roleId;

    private String name;

    private String dataScope;

    private Integer level = 3;

    private String description;

}
