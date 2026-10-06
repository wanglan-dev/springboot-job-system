package com.internship.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.internship.entity.WeeklyReport;
import com.internship.entity.User;
import com.internship.service.WeeklyReportService;
import com.internship.service.UserService;
import com.internship.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weekly")
public class WeeklyReportController {

    @Autowired
    private WeeklyReportService weeklyReportService;

    @Autowired
    private UserService userService;

    /**
     * 学生提交周记
     */
    @PostMapping
    public Result<String> save(@RequestAttribute("userId") Long userId, @RequestBody WeeklyReport report) {
        report.setStudentId(userId);
        report.setStatus(0); // 待审核
        boolean flag = weeklyReportService.save(report);
        return flag ? Result.success("提交成功") : Result.error("提交失败");
    }

    /**
     * 学生查看我的周记
     */
    @GetMapping("/my")
    public Result<List<WeeklyReport>> getMyReports(@RequestAttribute("userId") Long userId) {
        QueryWrapper<WeeklyReport> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", userId).orderByDesc("create_time");
        List<WeeklyReport> list = weeklyReportService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 老师查看待审核周记列表
     */
    @GetMapping("/audit-list")
    public Result<List<WeeklyReport>> getAuditList() {
        QueryWrapper<WeeklyReport> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0).orderByDesc("create_time");
        List<WeeklyReport> list = weeklyReportService.list(wrapper);
        // 填充学生姓名
        for (WeeklyReport r : list) {
            User user = userService.getById(r.getStudentId());
            if (user != null) {
                r.setStudentName(user.getRealName() != null ? user.getRealName() : user.getUsername());
            }
        }
        return Result.success(list);
    }

    /**
     * 老师审核周记
     */
    @PutMapping("/audit/{id}")
    public Result<String> audit(@PathVariable Long id, @RequestBody WeeklyReport report) {
        WeeklyReport old = weeklyReportService.getById(id);
        if (old == null) {
            return Result.error("记录不存在");
        }
        old.setStatus(report.getStatus());
        old.setTeacherComment(report.getTeacherComment());
        boolean flag = weeklyReportService.updateById(old);
        return flag ? Result.success("审核成功") : Result.error("审核失败");
    }
}
