/*
 Navicat Premium Data Transfer

 Source Server         : shrzhr
 Source Server Type    : MySQL
 Source Server Version : 80029
 Source Host           : localhost:3306
 Source Schema         : shrzhr

 Target Server Type    : MySQL
 Target Server Version : 80029
 File Encoding         : 65001

 Date: 23/06/2022 11:25:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for r_msg_content
-- ----------------------------
DROP TABLE IF EXISTS `r_msg_content`;
CREATE TABLE `r_msg_content`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '群关键字，用于筛选',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '欢迎语',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1394 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '关键字返回内容' ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for r_qq_group_info
-- ----------------------------
DROP TABLE IF EXISTS `r_qq_group_info`;
CREATE TABLE `r_qq_group_info`  (
    `id` bigint NOT NULL COMMENT '主键',
    `group_id` bigint NULL DEFAULT 0 COMMENT 'qq群号',
    `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'qq群名称',
    `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '群关键字，用于筛选',
    `is_img` tinyint NULL DEFAULT 0 COMMENT '是否发送图片',
    `is_msg` tinyint NULL DEFAULT 0 COMMENT '是否发送欢迎语',
    `welcome` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '欢迎语',
    `is_out_message` int NULL DEFAULT 0 COMMENT '是否退出提醒群主 0 否 1是',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'qq群信息' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Table structure for r_qq_group_log
-- ----------------------------
DROP TABLE IF EXISTS `r_qq_group_log`;
CREATE TABLE `r_qq_group_log`  (
  `id` bigint NOT NULL COMMENT '主键',
  `group_id` bigint NULL DEFAULT 0 COMMENT 'qq群号',
  `qq` bigint NULL DEFAULT 1 COMMENT 'qq',
  `qq_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'qq昵称',
  `event` int NULL DEFAULT NULL COMMENT '事件，1.邀请入群 2.主动加群 3.踢人 4. 退群',
  `invite` bigint NULL DEFAULT NULL COMMENT '邀请人',
  `kick` bigint NULL DEFAULT NULL COMMENT '踢人者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'qq群进群，退群记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for r_qq_group_message
-- ----------------------------
DROP TABLE IF EXISTS `r_qq_group_message`;
CREATE TABLE `r_qq_group_message`  (
  `id` bigint NOT NULL COMMENT '主键',
  `group_id` bigint NULL DEFAULT 0 COMMENT 'qq群号',
  `qq` bigint NULL DEFAULT 1 COMMENT 'qq',
  `qq_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'qq昵称',
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '群关键字，用于筛选',
  `message` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'qq群关键消息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for r_qq_group_plugin
-- ----------------------------
DROP TABLE IF EXISTS `r_qq_group_plugin`;
CREATE TABLE `r_qq_group_plugin`  (
  `id` bigint NOT NULL COMMENT '主键',
  `group_id` bigint NULL DEFAULT 0 COMMENT 'qq群号',
  `plugin_id` bigint NULL DEFAULT 1 COMMENT '插件id',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `enabled` int NULL DEFAULT NULL COMMENT '是否启用  1启用 0 关闭',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'QQ群可以使用的插件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for r_qq_plugin
-- ----------------------------
DROP TABLE IF EXISTS `r_qq_plugin`;
CREATE TABLE `r_qq_plugin`  (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件名称',
  `plugin_desc` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件描述',
  `state` int NULL DEFAULT NULL COMMENT '状态， 1启动  0关闭',
  `sort` int NULL DEFAULT NULL COMMENT '默认排序',
  `class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类的完整名称',
  `is_all` int NULL DEFAULT 0 COMMENT '是否所有都可以使用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'QQ群插件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_qq_plugin
-- ----------------------------
INSERT INTO `r_qq_plugin` VALUES (1, '娱乐插件', '娱乐插件', 1, 2, 'com.shr25.robot.qq.plugins.FunRobotPlugin', 1);
INSERT INTO `r_qq_plugin` VALUES (2, '群管理插件', '群管理插件', 1, 1000, 'com.shr25.robot.qq.plugins.GroupManageRobotPlugin', 1);
INSERT INTO `r_qq_plugin` VALUES (3, '媒体插件', '媒体插件', 1, 10000, 'com.shr25.robot.qq.plugins.MediaRobotPlugin', 0);
INSERT INTO `r_qq_plugin` VALUES (4, '树人分享插件', '查找树人分享的信息', 1, 1, 'com.shr25.robot.qq.plugins.TaskShr25Plugin', 1);
INSERT INTO `r_qq_plugin` VALUES (5, '传声通插件', '多个群，同步消息', 1, 1001, 'com.shr25.robot.qq.plugins.MicrophoneRobotPlugin', 1);
INSERT INTO `r_qq_plugin` VALUES (6, '青云客ai机器人', '青云客ai机器人', 1, 9999999, 'com.shr25.robot.qq.plugins.AiRobotPlugin', 1);


-- ----------------------------
-- Table structure for t_qq_info
-- ----------------------------
DROP TABLE IF EXISTS `t_qq_info`;
CREATE TABLE `t_qq_info`  (
  `id` bigint NOT NULL COMMENT '主键',
  `qq_openid` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'qq统一openid',
  `qq_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'qq昵称',
  `sex` char(1) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '头像路径',
  `collection_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收款二维码',
  `first_extension_id` bigint NULL DEFAULT NULL COMMENT '第一推荐人',
  `first_extension_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第一推荐人名称',
  `extension_id` bigint NULL DEFAULT NULL COMMENT '推荐人',
  `extension_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '推荐人名称',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`, `qq_openid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'qq用户信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_qq_login_log
-- ----------------------------
DROP TABLE IF EXISTS `t_qq_login_log`;
CREATE TABLE `t_qq_login_log`  (
  `id` bigint NOT NULL COMMENT '主键',
  `openid` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'qq统一openid',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ip',
  `user_agent` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备信息',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`, `openid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'qq登录日志' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
