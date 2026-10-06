package com.internship.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("delivery")
public class Delivery {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long jobId;
    private Long resumeId;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
    @TableField(exist = false)
    private String studentName;
    @TableField(exist = false)
    private String jobName;
    @TableField(exist = false)
    private String companyName;
}
