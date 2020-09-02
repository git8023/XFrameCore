/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50610
Source Host           : localhost:3306
Source Database       : xframecore

Target Server Type    : MYSQL
Target Server Version : 50610
File Encoding         : 65001

Date: 2020-07-26 23:41:54
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for module
-- ----------------------------
DROP TABLE IF EXISTS `module`;
CREATE TABLE `module` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `name` varchar(255) DEFAULT NULL COMMENT '模块名称',
  `author` varchar(255) DEFAULT NULL COMMENT '作者',
  `upload_date` datetime DEFAULT NULL COMMENT '上传日期',
  `status` tinyint(4) DEFAULT NULL COMMENT '模块状态: 0-运行中, 1-停止, 2-启动失败',
  `note` text COMMENT '模块描述',
  `upload_info` int(11) DEFAULT NULL COMMENT '模块文件ID',
  `web_port` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjq82t0uvhrcoyskui0ofgl6ia` (`upload_info`),
  CONSTRAINT `FKjq82t0uvhrcoyskui0ofgl6ia` FOREIGN KEY (`upload_info`) REFERENCES `upload_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='模块信息表';

-- ----------------------------
-- Table structure for upload_info
-- ----------------------------
DROP TABLE IF EXISTS `upload_info`;
CREATE TABLE `upload_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `type` varchar(255) DEFAULT NULL COMMENT '文件类型',
  `path` varchar(255) DEFAULT NULL COMMENT '路径',
  `size` int(11) DEFAULT NULL COMMENT '文件大小',
  `name` varchar(255) DEFAULT NULL COMMENT '文件名(带后缀)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 COMMENT='上传信息表';

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
