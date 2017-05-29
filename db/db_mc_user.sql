/*
Navicat MySQL Data Transfer

Source Server         : 192.168.0.253-3306
Source Server Version : 50636
Source Host           : 192.168.0.253:3306
Source Database       : db_mc_user

Target Server Type    : MYSQL
Target Server Version : 50636
File Encoding         : 65001

Date: 2017-05-25 20:45:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_account_oper_history
-- ----------------------------
DROP TABLE IF EXISTS `t_account_oper_history`;
CREATE TABLE `t_account_oper_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `before_info` varchar(500) NOT NULL,
  `after_info` varchar(500) NOT NULL,
  `proc_user_id` bigint(20) NOT NULL,
  `proc_username` varchar(20) NOT NULL,
  `type` tinyint(4) NOT NULL COMMENT '1 业主\r\n            2 股东\r\n            3 代理\r\n            4 子账号/工作人员\r\n            5 会员',
  `owner_id` bigint(20) NOT NULL,
  `owner_name` varchar(20) NOT NULL,
  `create_time` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `index_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_account_oper_history
-- ----------------------------
INSERT INTO `t_account_oper_history` VALUES ('1', 'stock_test', '104174', '{\"bankCardNo\":\"889988777877777\",\"telephone\":\"13300002222\",\"email\":\"update@ee.com\",\"realname\":\"股东修改3\"}', '{\"bankCardNo\":\"889988777877777\",\"telephone\":\"13300002222\",\"email\":\"update@ee.com\",\"realname\":\"股东修改5\"}', '105094', 'stock2', '5', '10', '', '1494497801159');
INSERT INTO `t_account_oper_history` VALUES ('2', 'stock_test', '104174', '{\"bankCardNo\":\"889988777877777\",\"telephone\":\"13300002222\",\"email\":\"update@ee.com\",\"realname\":\"股东修改5\"}', '{\"bankCardNo\":\"889988777877777\",\"telephone\":\"13300002222\",\"email\":\"update@ee.com\",\"realname\":\"股东修改1\"}', '105094', 'stock2', '5', '10', '', '1494498220634');
INSERT INTO `t_account_oper_history` VALUES ('3', 'stock_test', '104174', '{\"bankCardNo\":\"889988777877777\",\"telephone\":\"13300002222\",\"email\":\"update@ee.com\",\"realname\":\"股东修改1\"}', '{\"bankCardNo\":\"889988777877777\",\"telephone\":\"13300002222\",\"email\":\"update@ee.com\",\"realname\":\"股东修改2\"}', '105094', 'stock2', '2', '10', '', '1494498234871');
INSERT INTO `t_account_oper_history` VALUES ('4', 'test_member_1', '102823', '{\"bank\":\"中国建设银行\",\"bankCardNo\":\"199988892343232232\",\"telephone\":\"1330002220\",\"email\":\"123@qq.com\",\"realname\":\"会员_test_1_update\"}', '{\"bank\":\"中国建设银行\",\"bankCardNo\":\"889988777877777\",\"bankDeposit\":\"马来西亚分行\",\"loginPassword\":\"password reset\",\"paymentPassword\":\"password reset\",\"telephone\":\"13300002222\",\"email\":\"update@ee.com\",\"realname\":\"会员_test_1_up1\"}', '105094', 'owner2_dl', '5', '10001', 'owner2', '1495276506657');

-- ----------------------------
-- Table structure for t_agent_apply
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_apply`;
CREATE TABLE `t_agent_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(19) NOT NULL,
  `realname` varchar(20) NOT NULL,
  `password` varchar(32) NOT NULL,
  `stock_id` bigint(20) NOT NULL,
  `stock_name` varchar(20) NOT NULL DEFAULT '',
  `owner_id` bigint(20) NOT NULL,
  `bank_card_no` varchar(25) NOT NULL DEFAULT '',
  `telephone` varchar(25) NOT NULL DEFAULT '',
  `email` varchar(50) NOT NULL DEFAULT '',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 未审核\r\n            2 已拒绝\r\n            3 已审核\r\n            ',
  `resource_url` varchar(50) NOT NULL DEFAULT '',
  `register_ip` int(11) NOT NULL DEFAULT '0',
  `create_time` bigint(20) NOT NULL DEFAULT '0',
  `temp1` varchar(128) NOT NULL DEFAULT '',
  `temp2` varchar(256) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_id_username` (`username`,`stock_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_agent_apply
-- ----------------------------
INSERT INTO `t_agent_apply` VALUES ('1', 'applyAgent', '代理申请', '11111', '0', '', '0', '', 'qq@qqq.com', '1330000000', '2', '', '0', '20170510183202', '', '');
INSERT INTO `t_agent_apply` VALUES ('3', 'applyAgent11', '代理申请111', '11111', '0', '', '0', '', 'qq@qqq.com', '1330000000', '2', '', '0', '20170510184931', '', '');
INSERT INTO `t_agent_apply` VALUES ('4', 'applyAgent22', '代理申请', '11111', '0', '', '0', '', 'qq@qqq.com', '1330000000', '2', '', '0', '20170510185134', '', '');
INSERT INTO `t_agent_apply` VALUES ('5', 'applyAgent', '代理申请', '11111', '10001', '', '0', '', 'qq@qqq.com', '1330000000', '2', 'http://localhost:8080/', '2130706433', '1494582939604', '', '');
INSERT INTO `t_agent_apply` VALUES ('6', 'applyAgent_1', '代理申请', '11111', '10001', '', '10001', '', 'qq@qqq.com', '1330000000', '1', 'http://localhost:8080/', '2130706433', '1494585728970', '', '');
INSERT INTO `t_agent_apply` VALUES ('7', 'applyAgent_2', '代理申请', '11111', '10001', '', '10001', '', 'qq@qqq.com', '1330000000', '1', 'http://localhost:8080/', '2130706433', '1494585732418', '', '');
INSERT INTO `t_agent_apply` VALUES ('8', 'applyAgent_3', '代理申请', '11111', '10001', '', '10001', '', 'qq@qqq.com', '1330000000', '1', 'http://localhost:8080/', '2130706433', '1494585734928', '', '');
INSERT INTO `t_agent_apply` VALUES ('9', 'applyAgent_4', '代理申请', '11111', '10001', '', '10001', '', 'qq@qqq.com', '1330000000', '1', 'http://localhost:8080/', '2130706433', '1494585738580', '', '');
INSERT INTO `t_agent_apply` VALUES ('10', 'applyAgent_test_1', '代理申请_test_1', '12345678901234567890123456789012', '10001', 'owner2', '10001', '', 'qq@qqq.com', '1330000000', '1', 'http://localhost:8080/', '2130706433', '1495265983653', '', '');
INSERT INTO `t_agent_apply` VALUES ('13', 'applyAgent_test_3', '代理申请_test_3', '206838b462cb3114efd3567e2c0e7af0', '10001', 'owner2', '10001', '', '1330000000', 'qq@qqq.com', '2', 'http://localhost:8080/', '2130706433', '1495267505801', '', '');
INSERT INTO `t_agent_apply` VALUES ('14', 'applyAgent_t_1', '代理申请_test_3', '206838b462cb3114efd3567e2c0e7af0', '10001', 'owner2', '10001', '', '1330000000', 'qq@qqq.com', '3', 'http://localhost:8080/', '2130706433', '1495268071467', '', '');

-- ----------------------------
-- Table structure for t_agent_config_info
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_config_info`;
CREATE TABLE `t_agent_config_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agent_id` bigint(20) NOT NULL,
  `return_scheme_id` int(11) NOT NULL,
  `admin_cost_id` int(11) NOT NULL,
  `fee_id` int(11) NOT NULL,
  `discount` int(11) NOT NULL,
  `cost` int(11) NOT NULL,
  `domain` varchar(500) DEFAULT NULL,
  `temp1` varchar(128) DEFAULT NULL,
  `temp2` varchar(256) DEFAULT NULL,
  `temp3` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_agent_id` (`agent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_agent_config_info
-- ----------------------------
INSERT INTO `t_agent_config_info` VALUES ('16', '109463', '1', '1', '1', '1', '1', '', null, null, null);
INSERT INTO `t_agent_config_info` VALUES ('17', '102822', '1', '1', '1', '1', '1', '', null, null, null);
INSERT INTO `t_agent_config_info` VALUES ('18', '108131', '1', '1', '1', '1', '1', '', null, null, null);
INSERT INTO `t_agent_config_info` VALUES ('19', '103913', '1', '1', '1', '1', '1', '', null, null, null);
INSERT INTO `t_agent_config_info` VALUES ('20', '106190', '1', '1', '1', '1', '1', '', null, null, null);
INSERT INTO `t_agent_config_info` VALUES ('21', '109221', '3', '4', '4', '1', '1', '', null, null, null);
INSERT INTO `t_agent_config_info` VALUES ('22', '105093', '1', '1', '1', '1', '1', '', null, null, null);
INSERT INTO `t_agent_config_info` VALUES ('23', '105096', '3', '4', '4', '1', '1', '', null, null, null);

-- ----------------------------
-- Table structure for t_agent_review
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_review`;
CREATE TABLE `t_agent_review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `agent_apply_id` bigint(20) NOT NULL,
  `agent_name` varchar(20) NOT NULL,
  `oper_user_id` bigint(20) NOT NULL,
  `oper_user_name` varchar(20) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `status` tinyint(4) NOT NULL COMMENT '1 未审核\r\n            2 已拒绝\r\n            3 已审核',
  `create_time` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_agent_review
-- ----------------------------
INSERT INTO `t_agent_review` VALUES ('13', '13', '代理申请_test_3', '105094', 'owner2_dl', '10001', '2', '1495267991975');
INSERT INTO `t_agent_review` VALUES ('14', '14', '代理申请_t_3', '105094', 'owner2_dl', '10001', '2', '1495268143709');
INSERT INTO `t_agent_review` VALUES ('15', '14', '代理申请', '105094', 'owner2_dl', '10001', '3', '1495268232637');

-- ----------------------------
-- Table structure for t_login_history
-- ----------------------------
DROP TABLE IF EXISTS `t_login_history`;
CREATE TABLE `t_login_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `create_time` bigint(20) NOT NULL DEFAULT '0',
  `request_ip` int(11) NOT NULL,
  `login_type` tinyint(4) NOT NULL COMMENT '1 退出\r\n            2 登录',
  `platform` varchar(256) DEFAULT NULL,
  `ip_addr` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_login_history
-- ----------------------------
INSERT INTO `t_login_history` VALUES ('1', '10001', '1494654749572', '2130706433', '2', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36', null);
INSERT INTO `t_login_history` VALUES ('5', '10001', '1495261913307', '2130706433', '2', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36', null);
INSERT INTO `t_login_history` VALUES ('6', '10001', '1495262707119', '2130706433', '2', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36', null);
INSERT INTO `t_login_history` VALUES ('7', '10001', '1495262710889', '2130706433', '2', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36', null);
INSERT INTO `t_login_history` VALUES ('8', '10001', '1495263221327', '2130706433', '2', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36', null);
INSERT INTO `t_login_history` VALUES ('9', '105094', '1495263240374', '2130706433', '2', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36', null);
INSERT INTO `t_login_history` VALUES ('10', '105096', '1495268192696', '2130706433', '2', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36', null);
INSERT INTO `t_login_history` VALUES ('11', '105096', '1495268199046', '2130706433', '2', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36', null);
INSERT INTO `t_login_history` VALUES ('12', '105096', '1495270271033', '2130706433', '2', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36', null);
INSERT INTO `t_login_history` VALUES ('13', '105094', '1495510738200', '2130706433', '2', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36', null);

-- ----------------------------
-- Table structure for t_login_info
-- ----------------------------
DROP TABLE IF EXISTS `t_login_info`;
CREATE TABLE `t_login_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `username` varchar(16) NOT NULL,
  `password` varchar(32) NOT NULL,
  `update_time` bigint(20) NOT NULL DEFAULT '0',
  `last_login_ip` int(11) NOT NULL DEFAULT '0',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 退出\r\n            2 登录',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_login_info
-- ----------------------------
INSERT INTO `t_login_info` VALUES ('6', '10001', 'owner2', '969dc63c9dcf53ff5d15fd2370f819e9', '1495263221327', '2130706433', '2');
INSERT INTO `t_login_info` VALUES ('7', '105094', 'owner2_dl', '7910e3e738aa986b8352ba9b6d699288', '1495510738200', '2130706433', '2');
INSERT INTO `t_login_info` VALUES ('46', '104170', 'stock_1_test', '645fadce30ff9b1099345d0e4ed1afae', '0', '0', '1');
INSERT INTO `t_login_info` VALUES ('47', '108374', 'agent_test_1', '206838b462cb3114efd3567e2c0e7af0', '0', '0', '1');
INSERT INTO `t_login_info` VALUES ('48', '100888', 'agent_test_2', '206838b462cb3114efd3567e2c0e7af0', '0', '0', '1');
INSERT INTO `t_login_info` VALUES ('49', '105093', 'agent_test_3', '206838b462cb3114efd3567e2c0e7af0', '0', '0', '1');
INSERT INTO `t_login_info` VALUES ('50', '105096', 'applyAgent_t_1', '1761fdb6d5ca2b5d74c5969c906892a2', '1495270271033', '2130706433', '2');
INSERT INTO `t_login_info` VALUES ('51', '101977', 'stock_test_2_14', '645fadce30ff9b1099345d0e4ed1afae', '0', '0', '1');

-- ----------------------------
-- Table structure for t_member
-- ----------------------------
DROP TABLE IF EXISTS `t_member`;
CREATE TABLE `t_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) NOT NULL,
  `realname` varchar(30) NOT NULL DEFAULT '',
  `username` varchar(19) NOT NULL,
  `telephone` varchar(25) NOT NULL DEFAULT '',
  `bank_card_no` varchar(19) NOT NULL DEFAULT '',
  `bank` varchar(20) NOT NULL DEFAULT '',
  `bank_deposit` varchar(40) NOT NULL DEFAULT '',
  `agent_id` bigint(20) NOT NULL,
  `agent_username` varchar(30) NOT NULL,
  `stock_id` bigint(20) NOT NULL,
  `stock_username` varchar(30) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `owner_username` varchar(30) NOT NULL DEFAULT '',
  `source_url` varchar(70) NOT NULL,
  `email` varchar(30) NOT NULL DEFAULT '',
  `register_ip` int(11) NOT NULL,
  `register_time` bigint(20) NOT NULL DEFAULT '0',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 启用\r\n            2 停用',
  `gender` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 男\r\n            2 女',
  `currency_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 人民币\r\n            ',
  `is_delete` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 否\r\n            2 是',
  `temp1` varchar(128) NOT NULL DEFAULT '',
  `temp2` varchar(256) NOT NULL DEFAULT '',
  `temp3` varchar(512) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_id` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_member
-- ----------------------------
INSERT INTO `t_member` VALUES ('10', '102829', '闪送_2update', 'test3435', '1330002220', '199988892343232232', '中国建设银行', '马来西亚分行', '103913', 'stock_test_dl', '1', 'owner1', '14', 'owner1', 'http://localhost:8080', '123@qq.com', '2130706433', '1494586615112', '1', '1', '1', '1', '', '', '');
INSERT INTO `t_member` VALUES ('27', '102823', '会员_test_1_up1', 'test_member_1', '13300002222', '889988777877777', '中国建设银行', '亚州分行', '105094', 'owner2_dl', '10001', 'owner2', '10001', 'owner2', 'http://localhost:8080', 'update@ee.com', '2130706433', '1495270591429', '1', '1', '1', '1', '', '', '');
INSERT INTO `t_member` VALUES ('28', '5910825', '闪送', 'test_member_11', '1223444', '4445555', '中国建设银行', '马来西亚分行', '105094', 'owner2_dl', '10001', 'owner2', '10001', 'owner2', 'http://localhost:8080', 'qq@', '2130706433', '1495452458777', '1', '1', '1', '1', '', '', '');
INSERT INTO `t_member` VALUES ('29', '9844822', '闪送', 'test_member_7', '1223444', '4445555', '中国建设银行', '马来西亚分行', '105094', 'owner2_dl', '10001', 'owner2', '10001', 'owner2', 'http://localhost:8080', 'qq@', '2130706433', '1495606218215', '1', '1', '1', '1', '', '', '');
INSERT INTO `t_member` VALUES ('30', '1216798', '测试用户1', 't_member_1', '1223444', '4445555', '中国建设银行', '马来西亚分行', '105094', 'owner2_dl', '10001', 'owner2', '10001', 'owner2', 'http://localhost:8080', 'qq@', '2130706433', '1495606480114', '1', '1', '1', '1', '', '', '');

-- ----------------------------
-- Table structure for t_owner_account_user
-- ----------------------------
DROP TABLE IF EXISTS `t_owner_account_user`;
CREATE TABLE `t_owner_account_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assem_account` varchar(50) NOT NULL COMMENT '业主ID-账号名（股东或代理）',
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_account` (`assem_account`),
  UNIQUE KEY `uq_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_owner_account_user
-- ----------------------------
INSERT INTO `t_owner_account_user` VALUES ('1', '10001-owner2', '10001');
INSERT INTO `t_owner_account_user` VALUES ('2', '10001-owner2_dl', '105094');
INSERT INTO `t_owner_account_user` VALUES ('17', '10001-stock_1_test', '104170');
INSERT INTO `t_owner_account_user` VALUES ('19', '10001-agent_test_1', '108374');
INSERT INTO `t_owner_account_user` VALUES ('20', '10001-agent_test_2', '100888');
INSERT INTO `t_owner_account_user` VALUES ('21', '10001-agent_test_3', '105093');
INSERT INTO `t_owner_account_user` VALUES ('23', '10001-applyAgent_test_3', '108132');
INSERT INTO `t_owner_account_user` VALUES ('24', '10001-applyAgent_t_1', '105096');
INSERT INTO `t_owner_account_user` VALUES ('25', '10001-stock_test_2_14', '101977');

-- ----------------------------
-- Table structure for t_owner_stock_agent_member
-- ----------------------------
DROP TABLE IF EXISTS `t_owner_stock_agent_member`;
CREATE TABLE `t_owner_stock_agent_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `owner_id` bigint(20) NOT NULL,
  `stock_id` bigint(20) NOT NULL,
  `agent_id` bigint(20) NOT NULL,
  `mem_number` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_all_id` (`owner_id`,`stock_id`,`agent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_owner_stock_agent_member
-- ----------------------------
INSERT INTO `t_owner_stock_agent_member` VALUES ('1', '10001', '10001', '105094', '0');
INSERT INTO `t_owner_stock_agent_member` VALUES ('9', '10001', '10001', '105093', '0');
INSERT INTO `t_owner_stock_agent_member` VALUES ('10', '10001', '10001', '105096', '0');

-- ----------------------------
-- Table structure for t_user_info
-- ----------------------------
DROP TABLE IF EXISTS `t_user_info`;
CREATE TABLE `t_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `realname` varchar(30) NOT NULL,
  `username` varchar(19) NOT NULL,
  `telephone` varchar(25) NOT NULL,
  `email` varchar(50) NOT NULL DEFAULT '' COMMENT '\r\n            ',
  `register_time` bigint(20) NOT NULL DEFAULT '0',
  `register_ip` int(11) NOT NULL DEFAULT '0',
  `generalize_code` varchar(20) NOT NULL DEFAULT '',
  `gender` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 男\r\n            2 女',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 启用\r\n            2 停用',
  `currency_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1、人民币',
  `bank_card_no` varchar(25) NOT NULL DEFAULT '',
  `bank` varchar(20) NOT NULL DEFAULT '',
  `bank_deposit` varchar(50) NOT NULL DEFAULT '',
  `owner_id` bigint(20) NOT NULL DEFAULT '0',
  `owner_name` varchar(20) NOT NULL DEFAULT '',
  `is_delete` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 否\r\n            2 是',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1 业主\r\n            2 股东\r\n            3 代理\r\n            4 子账号/工作人员',
  `temp1` varchar(128) NOT NULL DEFAULT '',
  `temp2` varchar(256) NOT NULL DEFAULT '',
  `temp3` varchar(512) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user_info
-- ----------------------------
INSERT INTO `t_user_info` VALUES ('14', '10001', '业主2', 'owner2', '15533223355', 'update@1234', '1494491796335', '0', '', '1', '1', '1', '11111_update', '中国建设银行', '', '10001', '', '1', '2', '', '', '');
INSERT INTO `t_user_info` VALUES ('15', '105094', '业主2_dl', 'owner2_dl', '15533223355', 'update@1234', '1494491796335', '0', '', '1', '1', '1', '11111_update', '中国建设银行', '', '10001', '', '1', '3', '', '', '');
INSERT INTO `t_user_info` VALUES ('55', '104170', '股东_1_test_update', 'stock_1_test', '15533223355', 'update@1234', '1495262339852', '2130706433', '', '1', '2', '1', '11111_update', '中国建设银行', '', '10001', '', '1', '2', '', '', '');
INSERT INTO `t_user_info` VALUES ('56', '108374', '代理_test_1', 'agent_test_1', '122334422', 'agent@11.com', '1495263650800', '2130706433', 'CB3BD06207BB', '1', '1', '1', '99889912', '', '', '10001', '', '1', '3', '', '', '');
INSERT INTO `t_user_info` VALUES ('57', '100888', '代理_test_1', 'agent_test_2', '122334422', 'agent@11.com', '1495263722166', '2130706433', 'EB7DCD50EAE8', '1', '1', '1', '99889912', '', '', '10001', '', '1', '3', '', '', '');
INSERT INTO `t_user_info` VALUES ('58', '105093', '代理_test_1', 'agent_test_3', '122334422', 'agent@11.com', '1495263760657', '2130706433', 'DDC7D460BAEF', '1', '1', '1', '99889912', '', '', '10001', '', '1', '3', '', '', '');
INSERT INTO `t_user_info` VALUES ('59', '105096', '代理申请_t_3', 'applyAgent_t_1', '1779898989', 'aa@qq.com', '1495268147818', '2130706433', '0E3290F0AAFB', '1', '1', '1', '122222', '', '', '10001', '', '1', '3', '', '', '');
INSERT INTO `t_user_info` VALUES ('60', '101977', '股东_2_14', 'stock_test_2_14', '15533223355', '', '1495527104609', '2130706433', '', '1', '1', '1', '199988884443332', '佛山支行支行', '广东佛山支行', '10001', '', '1', '2', '', '', '');
