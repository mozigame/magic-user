/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/5/5 20:33:20                            */
/*==============================================================*/


drop index index_user_id on t_account_oper_history;

drop table if exists t_account_oper_history;

drop index uq_id_username on t_agent_apply;

drop table if exists t_agent_apply;

drop index uq_agent_id on t_agent_config_info;

drop table if exists t_agent_config_info;

drop table if exists t_agent_review_history;

drop index index_user_id on t_login_history;

drop table if exists t_login_history;

drop index uq_user_id on t_login_info;

drop table if exists t_login_info;

drop index uq_user_id on t_member;

drop table if exists t_member;

drop index uq_user_id on t_pro_account_user;

drop index uq_account on t_pro_account_user;

drop table if exists t_pro_account_user;

drop index uq_all_id on t_prop_stock_agent_member;

drop table if exists t_prop_stock_agent_member;

drop index uq_user_id on t_user_info;

drop table if exists t_user_info;

/*==============================================================*/
/* Table: t_account_oper_history                                */
/*==============================================================*/
create table t_account_oper_history
(
  id                   bigint not null auto_increment,
  username             varchar(20) not null,
  user_id              bigint not null,
  before_info          varchar(500) not null,
  after_info           varchar(500) not null,
  proc_user_id         bigint not null,
  proc_username        varchar(20) not null,
  type                 tinyint not null comment '1 业主
            2 股东
            3 代理
            4 子账号/工作人员
            5 会员',
  create_time          datetime not null,
  primary key (id)
);

/*==============================================================*/
/* Index: index_user_id                                         */
/*==============================================================*/
create index index_user_id on t_account_oper_history
(
  user_id
);

/*==============================================================*/
/* Table: t_agent_apply                                         */
/*==============================================================*/
create table t_agent_apply
(
  id                   bigint not null auto_increment,
  username             varchar(19) not null,
  stock_id             bigint not null,
  telephone            varchar(25) not null,
  email                varchar(50) not null,
  status               tinyint not null comment '1 未审核
            2 已拒绝
            3 已审核
            ',
  resource_url         varchar(50) not null,
  register_ip          int not null,
  create_time          datetime not null,
  temp1                varchar(128) not null,
  temp2                varchar(256) not null,
  primary key (id)
);

/*==============================================================*/
/* Index: uq_id_username                                        */
/*==============================================================*/
create unique index uq_id_username on t_agent_apply
(
  username,
  stock_id
);

/*==============================================================*/
/* Table: t_agent_config_info                                   */
/*==============================================================*/
create table t_agent_config_info
(
  id                   int not null auto_increment,
  agent_id             bigint not null,
  return_scheme_id     int not null,
  admin_cost_id        int not null,
  fee_id               int not null,
  discount             int not null,
  cost                 int not null,
  domain               varchar(500),
  temp1                varchar(128),
  temp2                varchar(256),
  temp3                varchar(512),
  primary key (id)
);

/*==============================================================*/
/* Index: uq_agent_id                                           */
/*==============================================================*/
create unique index uq_agent_id on t_agent_config_info
(
  agent_id
);

/*==============================================================*/
/* Table: t_agent_review_history                                */
/*==============================================================*/
create table t_agent_review_history
(
  id                   bigint not null auto_increment,
  agent_apply_id       bigint not null,
  oper_user_id         bigint not null,
  status               tinyint not null comment '1 未审核
            2 已拒绝
            3 已审核',
  create_time          datetime not null,
  primary key (id)
);

/*==============================================================*/
/* Table: t_login_history                                       */
/*==============================================================*/
create table t_login_history
(
  id                   bigint not null auto_increment,
  user_id              bigint not null,
  create_time          datetime not null,
  request_ip           int not null,
  login_type           tinyint not null comment '1 退出
            2 登录',
  platform             varchar(256),
  ip_addr              varchar(50),
  primary key (id)
);

/*==============================================================*/
/* Index: index_user_id                                         */
/*==============================================================*/
create index index_user_id on t_login_history
(
  user_id
);

/*==============================================================*/
/* Table: t_login_info                                          */
/*==============================================================*/
create table t_login_info
(
  id                   bigint not null auto_increment,
  user_id              bigint not null,
  username             varchar(16) not null,
  password             varchar(32) not null,
  update_time          datetime not null,
  last_login_ip        int not null,
  status               tinyint not null comment '1 退出
            2 登录',
  primary key (id)
);

/*==============================================================*/
/* Index: uq_user_id                                            */
/*==============================================================*/
create unique index uq_user_id on t_login_info
(
  user_id
);

/*==============================================================*/
/* Table: t_member                                              */
/*==============================================================*/
create table t_member
(
  id                   bigint not null auto_increment,
  member_id            bigint not null,
  realname             varchar(30) not null,
  username             varchar(19) not null,
  telephone            varchar(25) not null,
  bank_card_no         varchar(19) not null,
  agent_id             bigint not null,
  agent_username       varchar(30) not null,
  stock_id             bigint not null,
  stock_username       varchar(30) not null,
  proprietor_id        bigint not null,
  proprietor_username  varchar(30) not null,
  source_url           varchar(70) not null,
  register_ip          int not null,
  register_time        datetime not null,
  status               tinyint not null default 2 comment '1 启用
            2 停用',
  gender               tinyint not null default 1 comment '1 男
            2 女',
  currency_type        tinyint not null default 1 comment '1 人民币
            ',
  is_delete            tinyint not null comment '1 否
            2 是',
  temp1                varchar(128) not null,
  temp2                varchar(256) not null,
  temp3                varchar(512) not null,
  primary key (id)
);

/*==============================================================*/
/* Index: uq_user_id                                            */
/*==============================================================*/
create unique index uq_user_id on t_member
(
  member_id
);

/*==============================================================*/
/* Table: t_pro_account_user                                    */
/*==============================================================*/
create table t_pro_account_user
(
  id                   bigint not null auto_increment,
  assem_account        varchar(50) not null comment '业主ID-账号名（股东或代理）',
  user_id              bigint not null,
  primary key (id)
);

/*==============================================================*/
/* Index: uq_account                                            */
/*==============================================================*/
create unique index uq_account on t_pro_account_user
(
  assem_account
);

/*==============================================================*/
/* Index: uq_user_id                                            */
/*==============================================================*/
create unique index uq_user_id on t_pro_account_user
(
  user_id
);

/*==============================================================*/
/* Table: t_prop_stock_agent_member                             */
/*==============================================================*/
create table t_prop_stock_agent_member
(
  id                   bigint not null auto_increment,
  proprietor_id        bigint not null,
  stock_id             bigint not null,
  agent_id             bigint not null,
  mem_number           int not null,
  primary key (id)
);

/*==============================================================*/
/* Index: uq_all_id                                             */
/*==============================================================*/
create unique index uq_all_id on t_prop_stock_agent_member
(
  proprietor_id,
  stock_id,
  agent_id
);

/*==============================================================*/
/* Table: t_user_info                                           */
/*==============================================================*/
create table t_user_info
(
  id                   bigint not null auto_increment,
  user_id              bigint not null,
  realname             varchar(30) not null,
  username             varchar(19) not null,
  telephone            varchar(25) not null,
  email                varchar(50) not null comment '
            ',
  register_time        datetime not null,
  register_ip          int not null,
  generalize_code      varchar(20) not null,
  gender               tinyint not null default 1 comment '1 男
            2 女',
  status               tinyint not null default 2 comment '1 启用
            2 停用',
  currency_type        tinyint,
  bank_card_no         varchar(25) not null,
  proprietor_id        bigint not null,
  is_delete            tinyint not null comment '1 否
            2 是',
  type                 tinyint comment '1 业主
            2 股东
            3 代理
            4 子账号/工作人员',
  temp1                varchar(128) not null,
  temp2                varchar(256) not null,
  temp3                varchar(512) not null,
  primary key (id)
);

/*==============================================================*/
/* Index: uq_user_id                                            */
/*==============================================================*/
create unique index uq_user_id on t_user_info
(
  user_id
);

