CREATE TABLE IF NOT EXISTS `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `target_type` tinyint NOT NULL COMMENT '评价对象类型：1=课程 2=教练 3=器材',
  `target_id` bigint NOT NULL COMMENT '评价对象ID',
  `member_id` bigint NOT NULL COMMENT '评价会员ID',
  `rating` tinyint NOT NULL COMMENT '评分：1-5星',
  `content` text COMMENT '评价内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_target` (`target_type`, `target_id`),
  KEY `idx_member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';
