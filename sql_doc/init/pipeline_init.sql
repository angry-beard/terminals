CREATE TABLE `pipeline_task` (
  `id` int NOT NULL AUTO_INCREMENT,
  `serial` varchar(64) DEFAULT NULL COMMENT '唯一序列号',
  `exec_status` varchar(8) NOT NULL COMMENT '执行状态',
  `name` varchar(32) DEFAULT NULL COMMENT '名字',
  `trans_result` varchar(64) DEFAULT NULL,
  `request_param` longtext COMMENT 'process执行结果缓存',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_serial` (`serial`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='task';


CREATE TABLE `pipeline_process` (
  `id` int NOT NULL AUTO_INCREMENT,
  `task_serial` varchar(64) DEFAULT NULL COMMENT '唯一序列号',
  `exec_status` varchar(8) NOT NULL COMMENT '执行状态',
  `name` varchar(32) DEFAULT NULL COMMENT '名字',
  `order` int DEFAULT NULL COMMENT '排序号',
  `class_name` varchar(128) DEFAULT NULL COMMENT '全类名',
  `try_no` int DEFAULT NULL COMMENT '执行次数',
  `param` longtext COMMENT '执行结果',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_serial` (`task_serial`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='process';