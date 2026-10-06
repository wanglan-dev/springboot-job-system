package com.internship.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("resume")
public class Resume {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String realName;
    private String gender;
    private String phone;
    private String email;
    private String school;
    private String major;
    private String education;
    private String skills;
    private String experience;
    private String selfEval;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
