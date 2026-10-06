package com.internship.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.internship.entity.Delivery;
import com.internship.entity.Job;
import com.internship.entity.User;
import com.internship.service.DeliveryService;
import com.internship.service.JobService;
import com.internship.service.UserService;
import com.internship.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    /**
     * 分页查询投递记录（管理员/老师用）
     */
    @GetMapping("/list")
    public Result<Page<Delivery>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {

        Page<Delivery> page = new Page<>(current, size);
        QueryWrapper<Delivery> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");

        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }

        Page<Delivery> result = deliveryService.page(page, wrapper);

        // 填充关联数据
        for (Delivery d : result.getRecords()) {
            User student = userService.getById(d.getStudentId());
            if (student != null) {
                d.setStudentName(student.getRealName() != null ? student.getRealName() : student.getUsername());
            }
            Job job = jobService.getById(d.getJobId());
            if (job != null) {
                d.setJobName(job.getJobName());
            }
        }

        return Result.success(result);
    }

    /**
     * 企业HR查看投递列表
     */
    @GetMapping("/hr-list")
    public Result<List<Delivery>> getHrList() {
        QueryWrapper<Delivery> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        List<Delivery> list = deliveryService.list(wrapper);

        for (Delivery d : list) {
            User student = userService.getById(d.getStudentId());
            if (student != null) {
                d.setStudentName(student.getRealName() != null ? student.getRealName() : student.getUsername());
            }
            Job job = jobService.getById(d.getJobId());
            if (job != null) {
                d.setJobName(job.getJobName());
                d.setCompanyName(job.getCompanyName());
            }
        }
        return Result.success(list);
    }

    /**
     * 学生投递简历
     */
    @PostMapping
    public Result<String> save(@RequestAttribute("userId") Long userId, @RequestBody Delivery delivery) {
        // 检查是否已投递
        QueryWrapper<Delivery> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", userId).eq("job_id", delivery.getJobId());
        if (deliveryService.count(wrapper) > 0) {
            return Result.error("您已投递过该岗位");
        }

        delivery.setStudentId(userId);
        delivery.setStatus("待处理");
        boolean flag = deliveryService.save(delivery);
        return flag ? Result.success("投递成功") : Result.error("投递失败");
    }

    /**
     * 修改投递状态（企业HR录用/拒绝）
     */
    @PutMapping("/{id}")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Delivery delivery) {
        Delivery old = deliveryService.getById(id);
        if (old == null) {
            return Result.error("记录不存在");
        }
        old.setStatus(delivery.getStatus());
        old.setRemark(delivery.getRemark());
        boolean flag = deliveryService.updateById(old);
        return flag ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除投递记录
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean flag = deliveryService.removeById(id);
        return flag ? Result.success("删除成功") : Result.error("删除失败");
    }
}
