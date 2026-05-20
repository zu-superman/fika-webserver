package cn.hy.common.core.domain.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * turnstile 验证结果响应对象
 */
@Data
public class TurnstileResult implements Serializable {
    private static final long serialVersionUID = -252541226447298411L;

    /**
     * 是否验证成功
     */
    private Boolean success;

    /**
     * 验证时间（ISO 8601标准）
     */
    private String challengeTs;

    /**
     * 验证源
     */
    private String hostname;

    /**
     * 错误码列表<br/>
     *  - missing-input-secret: 缺少Secret Key <br/>
     *  - invalid-input-secret: Secret Key无效或过期 <br/>
     *  - missing-input-response: 未提供验证码（人机验证挑战结果） <br/>
     *  - invalid-input-response: 验证码（人机验证挑战结果）无效、格式错误或过期 <br/>
     *  - bad-request: 请求格式不正确 <br/>
     *  - timeout-or-duplicate: 验证码（人机验证挑战结果）已验证 <br/>
     *  - internal-error: turnstile服务器内部错误
     */
    private List<String> errorCodes;

    /**
     * 行为
     */
    private String action;

    /**
     * 扩展数据
     */
    private String cdata;

    /**
     * 元数据（企业版专享）
     */
    private Map<String, String> metadata;

}
