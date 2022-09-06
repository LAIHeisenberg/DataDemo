package com.longmai.datademo.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ddm_menu")
public class MenuPo {

    private Long id;

    private String title;

    private String componentName;

    private Integer menuSort = 999;

    private String component;

    private String path;

    private Integer type;

    private String permission;

    private String icon;

    private Boolean cache;

    private Boolean hidden;

    private Long pid;

    private Integer subCount = 0;

    private Boolean iFrame;

}
