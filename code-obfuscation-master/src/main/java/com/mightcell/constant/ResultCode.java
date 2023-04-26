package com.mightcell.constant;

/**
 * @author mightcell
 * @description 返回状态码
 * @date 2023年4月26日16:34:32
 */
public interface ResultCode {
    /**
     * 请求成功
     */
    Integer SUCCESS = 200;

    /**
     * 请求失败
     */
    Integer ERROR = 0;

    /**
     * 请求已经被接受
     */
    Integer ACCEPTED = 202;

    /**
     * 操作已经执行成功，但是没有返回数据
     */
    Integer NO_CONTENT = 204;

    /**
     * 资源已经被移除
     */
    Integer MOVE_PERM = 301;

    /**
     * 重定向
     */
    Integer SEE_OTHER = 303;

    /**
     * 资源没有被修改
     */
    Integer NOT_MODIFIED = 304;

    /**
     * 参数列表错误（缺少、格式不匹配）
     */
    Integer BAD_REQUEST = 400;

    /**
     * 未授权
     */
    Integer UNAUTHORIZED = 401;

    /**
     * 访问受限，授权过期
     */
    Integer FORBIDDEN = 403;

    /**
     * 资源，服务未找到
     */
    Integer NOT_FOUND = 404;

    /**
     * 不允许的http方法
     */
    Integer BAD_METHOD = 405;

    /**
     * 资源冲突，或者资源被锁
     */
    Integer CONFLICT = 409;

    /**
     * 不支持的数据，媒体类型
     */
    Integer UNSUPPORTED_TYPE = 415;

    /**
     * 接口未实现
     */
    Integer NOT_IMPLEMENTED = 501;

}
