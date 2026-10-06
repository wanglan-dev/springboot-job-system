package com.internship.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.internship.entity.Resume;
import com.internship.service.ResumeService;
import com.internship.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    /**
     * 获取我的简历（学生用）
     */
    @GetMapping("/my")
    public Result<Resume> getMyResume(@RequestAttribute("userId") Long userId) {
        QueryWrapper<Resume> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", userId);
        Resume resume = resumeService.getOne(wrapper);
        return Result.success(resume);
    }

    /**
     * 新增简历
     */
    @PostMapping
    public Result<String> save(@RequestAttribute("userId") Long userId, @RequestBody Resume resume) {
        resume.setStudentId(userId);
        boolean flag = resumeService.save(resume);
        return flag ? Result.success("保存成功") : Result.error("保存失败");
    }

    /**
     * 修改简历
     */
    @PutMapping
    public Result<String> update(@RequestBody Resume resume) {
        boolean flag = resumeService.updateById(resume);
        return flag ? Result.success("更新成功") : Result.error("更新失败");
    }
}
