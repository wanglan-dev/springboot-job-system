package com.internship.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.internship.entity.Job;
import com.internship.entity.Company;
import com.internship.service.JobService;
import com.internship.service.CompanyService;
import com.internship.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private CompanyService companyService;

    /**
     * 分页查询岗位列表（通用）
     */
    @GetMapping("/list")
    public Result<Page<Job>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String jobName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer status) {

        Page<Job> page = new Page<>(current, size);
        QueryWrapper<Job> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");

        if (jobName != null && !jobName.isEmpty()) {
            wrapper.like("job_name", jobName);
        }
        if (city != null && !city.isEmpty()) {
            wrapper.eq("city", city);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }

        Page<Job> result = jobService.page(page, wrapper);
        // 填充企业名称
        for (Job job : result.getRecords()) {
            Company company = companyService.getById(job.getCompanyId());
            if (company != null) {
                job.setCompanyName(company.getCompanyName());
            }
        }
        return Result.success(result);
    }

    /**
     * 企业HR查看我的岗位
     */
    @GetMapping("/my-jobs")
    public Result<List<Job>> getMyJobs() {
        QueryWrapper<Job> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        List<Job> list = jobService.list(wrapper);
        for (Job job : list) {
            Company company = companyService.getById(job.getCompanyId());
            if (company != null) {
                job.setCompanyName(company.getCompanyName());
            }
        }
        return Result.success(list);
    }

    /**
     * 根据ID查询岗位详情
     */
    @GetMapping("/{id}")
    public Result<Job> getById(@PathVariable Long id) {
        Job job = jobService.getById(id);
        if (job != null) {
            Company company = companyService.getById(job.getCompanyId());
            if (company != null) {
                job.setCompanyName(company.getCompanyName());
            }
        }
        return Result.success(job);
    }

    /**
     * 新增岗位
     */
    @PostMapping
    public Result<String> save(@RequestBody Job job) {
        boolean flag = jobService.save(job);
        return flag ? Result.success("新增成功") : Result.error("新增失败");
    }

    /**
     * 修改岗位
     */
    @PutMapping
    public Result<String> update(@RequestBody Job job) {
        boolean flag = jobService.updateById(job);
        return flag ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 删除岗位
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean flag = jobService.removeById(id);
        return flag ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取所有城市列表（用于筛选）
     */
    @GetMapping("/cities")
    public Result<List<String>> getCities() {
        List<String> cities = jobService.listObjs(new QueryWrapper<Job>()
                .select("DISTINCT city")
                .isNotNull("city"));
        return Result.success(cities);
    }
}
