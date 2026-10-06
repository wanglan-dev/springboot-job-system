-- 创建数据库（在Navicat中先执行这一步，或手动创建）
-- CREATE DATABASE internship_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE internship_db;

-- 角色表
CREATE TABLE IF NOT EXISTS role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role_id BIGINT COMMENT '角色ID',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 企业表
CREATE TABLE IF NOT EXISTS company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_name VARCHAR(100) NOT NULL COMMENT '企业名称',
    industry VARCHAR(50) COMMENT '所属行业',
    scale VARCHAR(50) COMMENT '企业规模',
    address VARCHAR(255) COMMENT '企业地址',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    description TEXT COMMENT '企业简介',
    status TINYINT DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业表';

-- 岗位表
CREATE TABLE IF NOT EXISTS job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_name VARCHAR(100) NOT NULL COMMENT '岗位名称',
    company_id BIGINT COMMENT '企业ID',
    salary_min INT COMMENT '最低薪资',
    salary_max INT COMMENT '最高薪资',
    city VARCHAR(50) COMMENT '工作城市',
    education VARCHAR(20) COMMENT '学历要求',
    experience VARCHAR(50) COMMENT '经验要求',
    job_type VARCHAR(50) COMMENT '岗位类型',
    description TEXT COMMENT '岗位描述',
    requirements TEXT COMMENT '岗位要求',
    status TINYINT DEFAULT 1 COMMENT '状态：0已下架 1招聘中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- 简历表
CREATE TABLE IF NOT EXISTS resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生用户ID',
    real_name VARCHAR(50) COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    school VARCHAR(100) COMMENT '学校',
    major VARCHAR(100) COMMENT '专业',
    education VARCHAR(20) COMMENT '学历',
    skills TEXT COMMENT '技能特长',
    experience TEXT COMMENT '实习/项目经历',
    self_eval TEXT COMMENT '自我评价',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (student_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- 投递记录表
CREATE TABLE IF NOT EXISTS delivery (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    job_id BIGINT NOT NULL COMMENT '岗位ID',
    resume_id BIGINT COMMENT '简历ID',
    status VARCHAR(20) DEFAULT '待处理' COMMENT '状态：待处理/已查看/面试邀请/已录用/已拒绝',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (student_id) REFERENCES user(id),
    FOREIGN KEY (job_id) REFERENCES job(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投递记录表';

-- 周记表
CREATE TABLE IF NOT EXISTS weekly_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    week_num INT COMMENT '第几周',
    title VARCHAR(200) COMMENT '周记标题',
    content TEXT COMMENT '周记内容',
    teacher_comment TEXT COMMENT '老师评语',
    status TINYINT DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已退回',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (student_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='周记表';

-- 公告表
CREATE TABLE IF NOT EXISTS notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT COMMENT '公告内容',
    publisher_id BIGINT COMMENT '发布人ID',
    top TINYINT DEFAULT 0 COMMENT '是否置顶：0否 1是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (publisher_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 插入角色数据
INSERT INTO role (role_name, role_code) VALUES
('管理员', 'ADMIN'),
('企业', 'COMPANY'),
('学生', 'STUDENT');

-- 插入测试管理员（密码是BCrypt加密的"123456"）
-- TODO: 如果登录失败，把下面这行密码换成明文 $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E 也是123456的加密
INSERT INTO user (username, password, real_name, role_id, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '系统管理员', 1, 1);

-- 插入测试企业数据
INSERT INTO company (company_name, industry, scale, address, contact_name, contact_phone, description, status) VALUES
('腾讯科技', '互联网', '10000人以上', '深圳市南山区', '张经理', '13800138000', '腾讯是一家世界领先的互联网科技公司', 1),
('阿里巴巴', '互联网', '10000人以上', '杭州市余杭区', '李经理', '13800138001', '阿里巴巴集团经营多项业务', 1),
('字节跳动', '互联网', '10000人以上', '北京市海淀区', '王经理', '13800138002', '字节跳动是全球化的移动互联网公司', 1),
('华为技术', '通信', '10000人以上', '深圳市龙岗区', '赵经理', '13800138003', '华为是全球领先的信息与通信技术解决方案供应商', 1),
('美团点评', '互联网', '5000-10000人', '北京市朝阳区', '刘经理', '13800138004', '美团是一家科技零售公司', 1);

-- 插入测试岗位数据
INSERT INTO job (job_name, company_id, salary_min, salary_max, city, education, experience, job_type, description, requirements, status) VALUES
('Java后端开发实习生', 1, 300, 500, '深圳', '本科', '不限', '技术', '负责后端服务开发', '熟悉Java、SpringBoot', 1),
('前端开发实习生', 1, 300, 500, '深圳', '本科', '不限', '技术', '负责前端页面开发', '熟悉Vue、HTML/CSS', 1),
('产品经理实习生', 2, 350, 600, '杭州', '本科', '不限', '产品', '协助产品经理完成需求分析', '有产品思维', 1),
('数据分析实习生', 2, 350, 600, '杭州', '本科', '不限', '数据', '负责数据分析与报表', '熟悉SQL、Python', 1),
('算法工程师实习生', 3, 400, 800, '北京', '硕士', '不限', '技术', '负责推荐算法优化', '熟悉机器学习', 1),
('测试工程师实习生', 3, 350, 600, '北京', '本科', '不限', '技术', '负责软件测试', '熟悉测试理论', 1),
('嵌入式开发实习生', 4, 350, 600, '深圳', '本科', '不限', '技术', '负责嵌入式软件开发', '熟悉C/C++', 1),
('人力资源实习生', 4, 250, 400, '深圳', '本科', '不限', '职能', '协助招聘工作', '沟通能力强', 1),
('运营实习生', 5, 300, 500, '北京', '本科', '不限', '运营', '负责用户运营', '有创意', 1),
('UI设计实习生', 5, 350, 600, '北京', '本科', '不限', '设计', '负责UI设计', '熟练使用设计软件', 1);


-- 插入测试学生账号（密码明文123456）
INSERT INTO user (username, password, real_name, role_id, status) VALUES
('student', '123456', '张三', 3, 1),
('student2', '123456', '李四', 3, 1);

-- 插入测试企业HR账号
INSERT INTO user (username, password, real_name, role_id, status) VALUES
('hr', '123456', '王HR', 2, 1);

-- 插入测试老师账号
INSERT INTO user (username, password, real_name, role_id, status) VALUES
('teacher', '123456', '赵老师', 2, 1);
