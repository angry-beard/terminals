CREATE TABLE `bank_pipeline_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `serial` varchar(64) DEFAULT NULL COMMENT '唯一序列号',
  `exec_status` varchar(8) NOT NULL COMMENT '执行状态',
  `name` varchar(32) DEFAULT NULL COMMENT '名字',
  `trans_result` varchar(2048) DEFAULT NULL COMMENT 'process执行结果缓存',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_serial` (`serial`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='task';


CREATE TABLE `bank_pipeline_process` (
  `id` int NOT NULL AUTO_INCREMENT,
  `task_serial` varchar(64) DEFAULT NULL COMMENT '唯一序列号',
  `exec_status` varchar(8) NOT NULL COMMENT '执行状态',
  `name` varchar(32) DEFAULT NULL COMMENT '名字',
  `order` varchar(32) DEFAULT NULL COMMENT '排序号',
  `class_name` varchar(32) DEFAULT NULL COMMENT '排序号',
  `try_no` varchar(2048) DEFAULT NULL COMMENT '执行次数',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_serial` (`task_serial`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='process';