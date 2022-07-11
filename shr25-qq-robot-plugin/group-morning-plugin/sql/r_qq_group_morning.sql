-- ----------------------------
-- Table structure for r_qq_group_morning
-- ----------------------------
DROP TABLE IF EXISTS `r_qq_group_morning`;
CREATE TABLE `r_qq_group_morning`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `group_id` bigint NOT NULL DEFAULT 0 COMMENT 'qq群号',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '固定图片',
  `msg` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '早安语',
  `is_img` tinyint NULL DEFAULT 1 COMMENT '是否发送图片',
  `is_msg` tinyint NULL DEFAULT 1 COMMENT '是否发送早安语',
  `is_random_img` tinyint NULL DEFAULT 1 COMMENT '是否随机发送图片',
  `is_random_msg` tinyint NULL DEFAULT 1 COMMENT '是否随机发送早安语',
  `is_sign_in` tinyint NULL DEFAULT 1 COMMENT '是否提醒签到',
  `send_time` datetime NULL DEFAULT NULL COMMENT '下一次发送的时间',
  `send_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '08:15' COMMENT '提醒时间，HH:MM',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1541756253436985347 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'qq群早安信息' ROW_FORMAT = Dynamic;

