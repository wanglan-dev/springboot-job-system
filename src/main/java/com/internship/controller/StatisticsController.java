package com.internship.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.internship.entity.*;
import com.internship.service.*;
import com.internship.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private CompanyService companyService;

    /**
     * 大屏统计数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();

        // 1. 顶部统计卡片
        Map<String, Object> cards = new HashMap<>();
        cards.put("totalStudents", userService.count(new QueryWrapper<User>().eq("role_id", 3)));
        cards.put("totalCompanies", companyService.count());
        cards.put("totalJobs", jobService.count(new QueryWrapper<Job>().eq("status", 1)));
        cards.put("totalDeliveries", deliveryService.count());
        data.put("cards", cards);

        // 2. 实习人数统计柱状图（按岗位类型）
        List<Map<String, Object>> jobTypeData = new ArrayList<>();
        QueryWrapper<Job> jobWrapper = new QueryWrapper<>();
        jobWrapper.select("job_type", "count(*) as count")
                .groupBy("job_type");
        List<Map<String, Object>> jobTypeMaps = jobService.listMaps(jobWrapper);
        for (Map<String, Object> map : jobTypeMaps) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", map.get("job_type"));
            item.put("value", map.get("count"));
            jobTypeData.add(item);
        }
        data.put("jobTypeChart", jobTypeData);

        // 3. 行业岗位分布饼图
        List<Map<String, Object>> industryData = new ArrayList<>();
        QueryWrapper<Company> companyWrapper = new QueryWrapper<>();
        companyWrapper.select("industry", "count(*) as count")
                .groupBy("industry");
        List<Map<String, Object>> industryMaps = companyService.listMaps(companyWrapper);
        for (Map<String, Object> map : industryMaps) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", map.get("industry"));
            item.put("value", map.get("count"));
            industryData.add(item);
        }
        data.put("industryChart", industryData);

        // 4. 近7天投递趋势折线图
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i);
            String dateStr = date.format(formatter);
            dates.add(dateStr);

            LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            QueryWrapper<Delivery> dw = new QueryWrapper<>();
            dw.ge("create_time", startOfDay).lt("create_time", endOfDay);
            counts.add(deliveryService.count(dw));
        }
        Map<String, Object> trendData = new HashMap<>();
        trendData.put("dates", dates);
        trendData.put("counts", counts);
        data.put("trendChart", trendData);

        return Result.success(data);
    }

    /**
     * 岗位投递排行TOP5
     */
    @GetMapping("/topJobs")
    public Result<List<Map<String, Object>>> topJobs() {
        // 查询投递量最多的5个岗位
        QueryWrapper<Delivery> wrapper = new QueryWrapper<>();
        wrapper.select("job_id", "count(*) as delivery_count")
                .groupBy("job_id")
                .orderByDesc("delivery_count")
                .last("limit 5");
        List<Map<String, Object>> list = deliveryService.listMaps(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map : list) {
            Long jobId = Long.valueOf(map.get("job_id").toString());
            Job job = jobService.getById(jobId);
            if (job != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("jobName", job.getJobName());
                item.put("count", map.get("delivery_count"));
                result.add(item);
            }
        }
        return Result.success(result);
    }
}
