/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.0.12 docker linux server root root
 Source Server Type    : MySQL
 Source Server Version : 50734
 Source Host           : 192.168.0.12:3306
 Source Schema         : BotMan

 Target Server Type    : MySQL
 Target Server Version : 50734
 File Encoding         : 65001

 Date: 06/09/2022 09:26:07
*/

-- ----------------------------
-- 如果 BotMan 数据库已存在 则删除
-- ----------------------------
DROP
DATABASE IF EXISTS BotMan;

-- ----------------------------
-- 创建数据库 BotMan 字符格式 utf8
-- ----------------------------
CREATE
DATABASE BotMan charset utf8;

-- ----------------------------
-- 显示数据库 BotMan 的创建属性 | 已注释
-- ----------------------------
-- SHOW CREATE DATABASE BotMan;

-- ----------------------------
-- 使用数据库 BotMan
-- ----------------------------
use
BotMan;

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user | 创建 user 表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`            varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户编号',
    `user_name`     varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
    `name`          varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'admin' COMMENT '名称',
    `password`      char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '123456' COMMENT '密码',
    `email`         varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
    `age`           tinyint(3) UNSIGNED NULL DEFAULT 18 COMMENT '年龄',
    `del_flag`      tinyint(1) NULL DEFAULT 0 COMMENT '删除标识符',
    `register_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `user_name`(`user_name`) USING BTREE,
    INDEX           `email`(`email`) USING BTREE,
    INDEX           `del_flag`(`del_flag`) USING BTREE,
    INDEX           `register_time`(`register_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user | 初始化数据
-- ----------------------------
INSERT INTO `user`
VALUES ('1', 'ahviplc', 'LC', '123456', 'ahlc@sina.cn', 18, 0, '2022-09-06 09:46:31');
INSERT INTO `BotMan`.`user`(`id`, `user_name`, `name`, `password`, `email`, `age`, `del_flag`, `register_time`)
VALUES ('2', 'shviplc', 'LC', '123456', 'ahlc@sina.cn', 18, 0, '2022-09-06 09:46:31');

SET
FOREIGN_KEY_CHECKS = 1;



