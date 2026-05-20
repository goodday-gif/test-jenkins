CREATE DATABASE IF NOT EXISTS gym_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE gym_db;

-- 1. sys_user 管理员/教练用户表
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  real_name VARCHAR(50) COMMENT '真实姓名',
  phone VARCHAR(20) COMMENT '手机号',
  avatar VARCHAR(255) COMMENT '头像',
  role_type TINYINT DEFAULT 0 COMMENT '角色类型 0-管理员 1-教练',
  status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. member 会员表
CREATE TABLE IF NOT EXISTS member (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  nickname VARCHAR(50) COMMENT '昵称',
  avatar VARCHAR(255) COMMENT '头像',
  phone VARCHAR(20) COMMENT '手机号',
  gender TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  member_level TINYINT DEFAULT 0 COMMENT '会员等级 0-普通 1-VIP',
  balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '余额',
  expire_time DATETIME COMMENT '到期时间',
  status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';

-- 3. course_category 教程分类表
CREATE TABLE IF NOT EXISTS course_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教程分类表';

-- 4. fitness_course 健身教程表
CREATE TABLE IF NOT EXISTS fitness_course (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT COMMENT '分类ID',
  title VARCHAR(100) NOT NULL COMMENT '教程标题',
  cover_img VARCHAR(255) COMMENT '封面图',
  video_url VARCHAR(500) COMMENT '视频地址',
  content TEXT COMMENT '图文内容',
  difficulty TINYINT DEFAULT 1 COMMENT '难度 1-入门 2-初级 3-中级 4-高级',
  duration INT DEFAULT 0 COMMENT '时长(分钟)',
  play_count INT DEFAULT 0 COMMENT '播放量',
  like_count INT DEFAULT 0 COMMENT '点赞数',
  collect_count INT DEFAULT 0 COMMENT '收藏数',
  suitable_for VARCHAR(200) COMMENT '适合人群',
  coach_tip VARCHAR(500) COMMENT '教练建议',
  publish_status TINYINT DEFAULT 0 COMMENT '发布状态 0-草稿 1-已发布 2-已下架',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健身教程表';

-- 5. course_interact 教程互动表
CREATE TABLE IF NOT EXISTS course_interact (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_id BIGINT NOT NULL COMMENT '会员ID',
  course_id BIGINT NOT NULL COMMENT '教程ID',
  type TINYINT NOT NULL COMMENT '互动类型 1-点赞 2-收藏 3-评论',
  content VARCHAR(500) COMMENT '评论内容',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教程互动表';

-- 6. coach 教练表
CREATE TABLE IF NOT EXISTS coach (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT COMMENT '关联sys_user的ID',
  name VARCHAR(50) NOT NULL COMMENT '姓名',
  avatar VARCHAR(255) COMMENT '头像',
  phone VARCHAR(20) COMMENT '手机号',
  experience_years INT DEFAULT 0 COMMENT '执教年限',
  specialty VARCHAR(200) COMMENT '擅长领域',
  certificate VARCHAR(500) COMMENT '资质证书',
  introduction TEXT COMMENT '简介',
  teaching_style VARCHAR(200) COMMENT '授课风格',
  status TINYINT DEFAULT 1 COMMENT '状态 0-离职 1-在职',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练表';

-- 7. gym_class 健身课程表
CREATE TABLE IF NOT EXISTS gym_class (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL COMMENT '课程名称',
  coach_id BIGINT COMMENT '教练ID',
  class_type TINYINT DEFAULT 1 COMMENT '课程类型 1-团课 2-私教课 3-小班课',
  class_time DATETIME COMMENT '上课时间',
  location VARCHAR(100) COMMENT '上课地点',
  max_capacity INT DEFAULT 0 COMMENT '最大名额',
  current_count INT DEFAULT 0 COMMENT '已预约人数',
  price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
  description TEXT COMMENT '课程描述',
  cover_img VARCHAR(255) COMMENT '封面图',
  status TINYINT DEFAULT 1 COMMENT '状态 0-已取消 1-未开始 2-进行中 3-已结束',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健身课程表';

-- 8. class_reserve 课程预约表
CREATE TABLE IF NOT EXISTS class_reserve (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_id BIGINT NOT NULL COMMENT '会员ID',
  class_id BIGINT NOT NULL COMMENT '课程ID',
  reserve_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
  check_in TINYINT DEFAULT 0 COMMENT '签到状态 0-未签到 1-已签到',
  status TINYINT DEFAULT 1 COMMENT '状态 0-已取消 1-已预约 2-已完成',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程预约表';

-- 9. gym_equipment 器材表
CREATE TABLE IF NOT EXISTS gym_equipment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL COMMENT '器材名称',
  category VARCHAR(50) COMMENT '分类',
  image VARCHAR(255) COMMENT '图片',
  function_desc TEXT COMMENT '功能介绍',
  usage_method TEXT COMMENT '使用方法',
  location VARCHAR(100) COMMENT '摆放位置',
  status TINYINT DEFAULT 1 COMMENT '状态 1-正常 2-维修 3-报废',
  purchase_time DATE COMMENT '购买时间',
  maintain_time DATE COMMENT '维护时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材表';

-- 10. announcement 公告/资讯表
CREATE TABLE IF NOT EXISTS announcement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL COMMENT '标题',
  content TEXT COMMENT '内容',
  cover_img VARCHAR(255) COMMENT '封面图',
  type TINYINT DEFAULT 1 COMMENT '类型 1-公告 2-资讯',
  author VARCHAR(50) COMMENT '发布人',
  is_top TINYINT DEFAULT 0 COMMENT '是否置顶 0-否 1-是',
  status TINYINT DEFAULT 1 COMMENT '状态 0-草稿 1-已发布',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告资讯表';

-- 11. orders 订单表
CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
  member_id BIGINT NOT NULL COMMENT '会员ID',
  order_type TINYINT DEFAULT 1 COMMENT '订单类型 1-课程购买 2-会员充值 3-会员续费',
  amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '金额',
  class_id BIGINT COMMENT '关联课程ID',
  pay_status TINYINT DEFAULT 0 COMMENT '支付状态 0-未支付 1-已支付 2-已退款',
  pay_time DATETIME COMMENT '支付时间',
  remark VARCHAR(255) COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ===================== 插入基础测试数据 =====================

-- 管理员账号 admin/123456 (BCrypt加密)
INSERT INTO sys_user (username, password, real_name, phone, role_type, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800000000', 0, 1);

-- 教练账号 coach1/123456
INSERT INTO sys_user (username, password, real_name, phone, role_type, status) VALUES
('coach1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张教练', '13800000001', 1, 1);

-- 教程分类
INSERT INTO course_category (name, sort, status) VALUES
('力量训练', 1, 1),
('有氧训练', 2, 1),
('瑜伽普拉提', 3, 1),
('康复训练', 4, 1),
('新手入门', 5, 1);

-- 教练数据
INSERT INTO coach (user_id, name, avatar, phone, experience_years, specialty, certificate, introduction, teaching_style, status) VALUES
(2, '张教练', NULL, '13800000001', 8, '力量训练,体能训练', 'NSCA-CPT认证,ACE认证', '国家一级健身教练，曾获全国健美锦标赛冠军，专注力量训练与体能提升。', '严谨专业,循序渐进', 1),
(NULL, '李教练', NULL, '13800000002', 5, '瑜伽,普拉提', '全美瑜伽联盟RYT200认证', '资深瑜伽导师，擅长流瑜伽和阴瑜伽，帮助学员找到身心平衡。', '温和耐心,注重细节', 1),
(NULL, '王教练', NULL, '13800000003', 3, '有氧训练,减脂塑形', 'ACSM认证', '热情活力的有氧训练专家，擅长设计高效的减脂课程。', '活力四射,鼓励式教学', 1);

-- 会员数据
INSERT INTO member (username, password, nickname, phone, gender, member_level, balance, expire_time, status) VALUES
('member1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '健身达人小明', '13900000001', 1, 1, 500.00, '2027-12-31 23:59:59', 1),
('member2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '瑜伽爱好者小红', '13900000002', 2, 0, 100.00, '2026-06-30 23:59:59', 1),
('member3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '运动新手小李', '13900000003', 1, 0, 0.00, NULL, 1);

-- 健身教程
INSERT INTO fitness_course (category_id, title, cover_img, video_url, content, difficulty, duration, play_count, like_count, collect_count, suitable_for, coach_tip, publish_status) VALUES
(1, '新手力量训练入门指南', NULL, NULL, '本教程将带你了解力量训练的基础知识，包括正确的姿势、呼吸方法和训练计划制定。', 1, 30, 1520, 328, 156, '零基础健身新手', '建议从轻重量开始，注重动作标准度。', 1),
(1, '哑铃全身力量训练', NULL, NULL, '使用一对哑铃即可完成的全身力量训练课程，适合家庭健身。', 2, 45, 2340, 512, 234, '有一定基础的健身爱好者', '每组间休息60-90秒，注意控制离心阶段。', 1),
(2, '燃脂HIIT训练30分钟', NULL, NULL, '高效燃脂的HIIT间歇训练，30分钟极速燃烧卡路里。', 2, 30, 5680, 1023, 567, '想要减脂的人群', '训练前充分热身，训练中注意心率不要过高。', 1),
(3, '零基础瑜伽入门', NULL, NULL, '从最基础的呼吸法开始，逐步学习基础瑜伽体式，帮助你开启瑜伽之旅。', 1, 60, 3210, 789, 432, '瑜伽初学者', '不要强迫身体，循序渐进最重要。', 1),
(4, '办公室颈椎康复操', NULL, NULL, '针对长期伏案工作导致的颈椎问题，提供简单有效的康复训练动作。', 1, 15, 8900, 2100, 1560, '办公室久坐人群', '动作要慢，感觉不适立即停止。', 1),
(5, '健身房器械使用指南', NULL, NULL, '详细讲解健身房常见器械的正确使用方法和注意事项。', 1, 40, 4560, 890, 670, '健身房新手', '第一次使用新器械建议在教练指导下进行。', 1);

-- 健身课程
INSERT INTO gym_class (name, coach_id, class_type, class_time, location, max_capacity, current_count, price, description, status) VALUES
('晨间活力瑜伽', 2, 1, '2026-05-01 07:00:00', 'A区瑜伽室', 20, 12, 0.00, '清晨瑜伽唤醒身体，开启活力满满的一天。', 1),
('力量训练进阶班', 1, 3, '2026-05-01 10:00:00', 'B区力量区', 8, 5, 99.00, '针对有一定基础的学员，进行系统的力量训练提升。', 1),
('燃脂搏击操', 3, 1, '2026-05-01 15:00:00', 'C区操房', 30, 22, 0.00, '融合拳击和有氧运动的高效燃脂课程。', 1),
('一对一私教体态矫正', 1, 2, '2026-05-02 09:00:00', 'D区私教室', 1, 0, 299.00, '针对个人体态问题定制矫正方案。', 1),
('晚间拉伸放松课', 2, 1, '2026-05-02 20:00:00', 'A区瑜伽室', 25, 18, 0.00, '一天结束后的深度拉伸放松，缓解肌肉疲劳。', 1);

-- 器材数据
INSERT INTO gym_equipment (name, category, image, function_desc, usage_method, location, status, purchase_time, maintain_time) VALUES
('史密斯机', '力量器械', NULL, '多功能力量训练器械，可进行深蹲、卧推、硬拉等多种动作。', '调整好安全挡杆高度，选择合适重量，沿固定轨道进行动作。', 'B区力量区', 1, '2024-01-15', '2026-03-01'),
('跑步机 T800', '有氧器械', NULL, '商用级跑步机，支持0-20km/h速度调节，带心率监测。', '站上跑带后再启动，从慢速开始逐渐加速，使用安全夹。', 'A区有氧区', 1, '2024-03-20', '2026-04-01'),
('可调节哑铃组', '自由重量', NULL, '2.5kg-25kg可调节哑铃，满足不同训练需求。', '选择合适重量，确保锁扣锁紧后再使用。', 'B区自由重量区', 1, '2024-02-10', '2026-03-15'),
('椭圆机 E500', '有氧器械', NULL, '低冲击有氧训练器械，保护膝关节的同时有效燃脂。', '双手握住把手，脚踏踏板，保持自然节奏运动。', 'A区有氧区', 1, '2024-05-08', '2026-04-10'),
('龙门架', '力量器械', NULL, '综合训练器械，可进行夹胸、下拉、绳索飞鸟等动作。', '调整滑轮高度和重量，选择合适的配件进行训练。', 'B区力量区', 2, '2023-11-20', '2026-04-15');

-- 公告/资讯数据
INSERT INTO announcement (title, content, cover_img, type, author, is_top, status) VALUES
('五一假期营业时间调整通知', '尊敬的会员朋友们，五一假期（5月1日-5月5日）营业时间调整为 8:00-20:00，请合理安排训练时间。祝大家节日快乐！', NULL, 1, '系统管理员', 1, 1),
('新教练入驻公告', '欢迎王教练加入我们的团队！王教练擅长有氧训练和减脂塑形，持有ACSM认证，欢迎大家预约体验课。', NULL, 1, '系统管理员', 0, 1),
('夏季健身小贴士', '夏季运动要注意：1.充分补水 2.避开高温时段 3.运动强度适当降低 4.注意防晒 5.运动后及时更换汗湿衣物', NULL, 2, '系统管理员', 0, 1),
('科学减脂指南：如何制定有效的减脂计划', '减脂的关键在于制造合理的热量缺口。建议每天减少300-500卡路里的摄入，配合每周3-5次的有氧运动和2-3次的力量训练。', NULL, 2, '张教练', 0, 1);

-- 订单数据
INSERT INTO orders (order_no, member_id, order_type, amount, class_id, pay_status, pay_time, remark) VALUES
('ORD20260401001', 1, 1, 99.00, 2, 1, '2026-04-01 10:30:00', '购买力量训练进阶班'),
('ORD20260401002', 1, 2, 500.00, NULL, 1, '2026-04-01 11:00:00', '会员充值'),
('ORD20260402001', 2, 3, 1200.00, NULL, 1, '2026-04-02 09:15:00', '会员续费一年'),
('ORD20260403001', 3, 1, 299.00, 4, 0, NULL, '预约私教体态矫正课');

-- 课程预约数据
INSERT INTO class_reserve (member_id, class_id, check_in, status) VALUES
(1, 1, 0, 1),
(1, 2, 0, 1),
(2, 1, 0, 1),
(2, 5, 0, 1),
(3, 3, 0, 1);
