package com.internship.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.internship.entity.Company;
import com.internship.entity.User;
import com.internship.service.CompanyService;
import com.internship.service.UserService;
import com.internship.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    /**
     * 企业HR获取我的企业信息
     */
    @GetMapping("/my")
    public Result<Company> getMyCompany(@RequestAttribute("userId") Long userId) {
        // 简化：根据用户ID查找关联的企业（实际应该通过用户-企业关联表）
        // 这里先返回第一个企业作为示例
        List<Company> list = companyService.list();
        if (!list.isEmpty()) {
            return Result.success(list.get(0));
        }
        return Result.success(null);
    }

    /**
     * 新增企业
     */
    @PostMapping
    public Result<String> save(@RequestBody Company company) {
        company.setStatus(0); // 待审核
        boolean flag = companyService.save(company);
        return flag ? Result.success("提交成功，等待审核") : Result.error("提交失败");
    }

    /**
     * 修改企业信息
     */
    @PutMapping
    public Result<String> update(@RequestBody Company company) {
        boolean flag = companyService.updateById(company);
        return flag ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 老师查看待审核企业列表
     */
    @GetMapping("/audit-list")
    public Result<List<Company>> getAuditList() {
        QueryWrapper<Company> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0).orderByDesc("create_time");
        List<Company> list = companyService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 老师审核企业
     */
    @PutMapping("/audit/{id}")
    public Result<String> audit(@PathVariable Long id, @RequestBody Company company) {
        Company old = companyService.getById(id);
        if (old == null) {
            return Result.error("企业不存在");
        }
        old.setStatus(company.getStatus());
        boolean flag = companyService.updateById(old);
        String msg = company.getStatus() == 1 ? "通过" : "拒绝";
        return flag ? Result.success("审核" + msg + "成功") : Result.error("审核失败");
    }
}
