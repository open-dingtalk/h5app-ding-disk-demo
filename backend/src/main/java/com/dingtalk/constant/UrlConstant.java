package com.dingtalk.constant;

/**
 * 钉钉开放接口网关常量
 */
public class UrlConstant {

    /**
     * 获取access_token url
     */
    public static final String GET_ACCESS_TOKEN_URL = "https://oapi.dingtalk.com/gettoken";

    /**
     * 通过免登授权码获取用户信息 url
     */
    public static final String GET_USER_INFO_URL = "https://oapi.dingtalk.com/topapi/v2/user/getuserinfo";
    /**
     * 根据用户id获取用户详情 url
     */
    public static final String USER_GET_URL = "https://oapi.dingtalk.com/topapi/v2/user/get";
    /**
     * 上传媒体文件 url
     */
    public static final String MEDIA_UPLOAD = "https://oapi.dingtalk.com/media/upload";
    /**
     * 获取指定用户子部门 url
     */
    public static final String LIST_PARENT_BY_USER = "https://oapi.dingtalk.com/topapi/v2/department/listparentbyuser";
    /**
     * 获取部门列表 url
     */
    public static final String DEPT_LIST_SUB = "https://oapi.dingtalk.com/topapi/v2/department/listsub";
}
