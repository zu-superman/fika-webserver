package cn.hy.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Fika API 枚举定义
 * 
 * @author singledog
 */
@Getter
@AllArgsConstructor
public enum FikaApiEnums {

    /**
     * 获取配置列表
     */
    GET_PROFILE_LIST("/fikaweb/profiles", HttpMethod.GET),

    /**
     * 获取配置详情
     */
    GET_PROFILE_DETAIL("/fikaweb/profiles/{id}", HttpMethod.GET),

    /**
     * 新增配置
     */
    CREATE_PROFILE("/fikaweb/profiles", HttpMethod.POST),

    /**
     * 修改配置
     */
    UPDATE_PROFILE("/fikaweb/profiles", HttpMethod.PUT),

    /**
     * 删除配置
     */
    DELETE_PROFILE("/fikaweb/profiles/{id}", HttpMethod.DELETE),

    /**
     * 启用配置
     */
    ENABLE_PROFILE("/fikaweb/profiles/{id}/enable", HttpMethod.PUT),

    /**
     * 禁用配置
     */
    DISABLE_PROFILE("/fikaweb/profiles/{id}/disable", HttpMethod.PUT),

    /**
     * 获取用户信息
     */
    GET_USER_PROFILE("/fikaweb/user/profile", HttpMethod.GET),

    /**
     * 更新用户信息
     */
    UPDATE_USER_PROFILE("/fikaweb/user/profile", HttpMethod.PUT),

    /**
     * 上传头像
     */
    UPLOAD_AVATAR("/fikaweb/user/avatar", HttpMethod.POST),

    /**
     * 修改密码
     */
    UPDATE_PASSWORD("/fikaweb/user/password", HttpMethod.PUT);

    /**
     * API 路径
     */
    private final String api;

    /**
     * 请求方式
     */
    private final HttpMethod method;

    /**
     * 根据 API 路径查找对应的枚举
     *
     * @param api API 路径
     * @return 对应的枚举值，未找到返回 null
     */
    public static FikaApiEnums getByApi(String api) {
        for (FikaApiEnums item : values()) {
            if (item.getApi().equals(api)) {
                return item;
            }
        }
        return null;
    }
}
