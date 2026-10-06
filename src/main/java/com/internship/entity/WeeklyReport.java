package com.internship.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("weekly_report")
public class WeeklyReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Integer weekNum;
    private String title;
    private String content;
    private String teacherComment;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
    @TableField(exist = false)
    private String studentName;
}
