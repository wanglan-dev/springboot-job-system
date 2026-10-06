package com.internship.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("job")
public class Job {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String jobName;
    private Long companyId;
    private Integer salaryMin;
    private Integer salaryMax;
    private String city;
    private String education;
    private String experience;
    private String jobType;
    private String description;
    private String requirements;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
    @TableField(exist = false)
    private String companyName;
}
