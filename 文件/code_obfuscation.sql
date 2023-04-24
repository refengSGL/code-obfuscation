create table file
(
    id                 int auto_increment comment '主键'
        primary key,
    name               varchar(200)      null comment 'UUID生成的文件名',
    type               varchar(32)       null comment '文件类型',
    memory             varchar(32)       null comment '文件内存大小',
    user_id            int               null comment '文件所属用户的id',
    store              varchar(200)      null comment '文件存储的路径',
    create_time        datetime          null comment '创建时间',
    update_time        datetime          null comment '更新时间',
    is_deleted         tinyint default 0 null comment '逻辑删除：0表示没有删除，1表示已经删除',
    original_file      int               null comment '被保护的原始文件的id',
    original_file_name varchar(64)       null comment '原始上传的文件名'
)
    comment '文件表';


create table user
(
    username    varchar(32)       null comment '用户名',
    id          int auto_increment comment '主键'
        primary key,
    password    varchar(400)      null comment '密码',
    phone       varchar(11)       null comment '电话',
    email       varchar(64)       null comment '邮箱',
    create_time datetime          null comment '创建时间',
    update_time datetime          null comment '更新时间',
    is_login    tinyint default 0 null comment '登录状态：0表示用户离线，1表示用户在线'
)
    comment '用户';


