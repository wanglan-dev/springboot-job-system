package com.internship.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.internship.entity.WeeklyReport;
import com.internship.mapper.WeeklyReportMapper;
import com.internship.service.WeeklyReportService;
import org.springframework.stereotype.Service;

@Service
public class WeeklyReportServiceImpl extends ServiceImpl<WeeklyReportMapper, WeeklyReport> implements WeeklyReportService {
}
