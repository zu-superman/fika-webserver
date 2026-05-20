package cn.hy.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TurnstileErrorCode {

    MISSING_INPUT_SECRET("missing-input-secret", "缺少Secret Key"),
    INVALID_INPUT_SECRET("invalid-input-secret", "Secret Key无效或过期"),
    MISSING_INPUT_RESPONSE("missing-input-response", "未提供验证码（人机验证挑战结果）"),
    INVALID_INPUT_RESPONSE("invalid-input-response", "验证码（人机验证挑战结果）无效、格式错误或过期"),
    BAD_REQUEST("bad-request", "请求格式不正确"),
    TIMEOUT_OR_DUPLICATE("timeout-or-duplicate", "验证码（人机验证挑战结果）已验证"),
    INTERNAL_ERROR("internal-error", "turnstile服务器内部错误"),

    ;

    private final String errorCode;
    private final String message;

    public static TurnstileErrorCode getByErrorCode(String errorCode) {
        for (TurnstileErrorCode value : TurnstileErrorCode.values()) {
            if (value.errorCode.equals(errorCode)) {
                return value;
            }
        }
        return null;
    }
}
