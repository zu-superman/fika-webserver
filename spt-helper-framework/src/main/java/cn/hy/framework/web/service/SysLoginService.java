package cn.hy.framework.web.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hy.common.config.RuoYiConfig;
import cn.hy.common.core.domain.model.TurnstileResult;
import cn.hy.common.enums.TurnstileErrorCode;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import cn.hy.common.constant.CacheConstants;
import cn.hy.common.constant.Constants;
import cn.hy.common.constant.UserConstants;
import cn.hy.common.core.domain.model.LoginUser;
import cn.hy.common.core.redis.RedisCache;
import cn.hy.common.exception.ServiceException;
import cn.hy.common.exception.user.BlackListException;
import cn.hy.common.exception.user.CaptchaException;
import cn.hy.common.exception.user.CaptchaExpireException;
import cn.hy.common.exception.user.UserNotExistsException;
import cn.hy.common.exception.user.UserPasswordNotMatchException;
import cn.hy.common.utils.DateUtils;
import cn.hy.common.utils.MessageUtils;
import cn.hy.common.utils.StringUtils;
import cn.hy.common.utils.ip.IpUtils;
import cn.hy.framework.manager.AsyncManager;
import cn.hy.framework.manager.factory.AsyncFactory;
import cn.hy.framework.security.context.AuthenticationContextHolder;
import cn.hy.system.service.ISysConfigService;
import cn.hy.system.service.ISysUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录校验方法
 * 
 * @author ruoyi
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SysLoginService {
    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;

    private final RedisCache redisCache;

    private final ISysUserService userService;

    private final ISysConfigService configService;

    private final RuoYiConfig ruoYiConfig;

    /**
     * 登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid) {
        // 验证码校验
//        validateCaptcha(username, code, uuid);
        validTurnstile(code, uuid);
        // 登录前置校验
        loginPreCheck(username, password);
        // 用户验证
        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        }
        catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
        finally {
            AuthenticationContextHolder.clearContext();
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 校验turnstile
     * @param response turnstile人机验证结果值
     * @param idempotencyKey 幂等键
     */
    private void validTurnstile(String response, String idempotencyKey) {

        Map<String, String> params = new HashMap<>(4);
        params.put("secret", ruoYiConfig.getTurnstileSiteSecret());
        params.put("response", response);
        params.put("remoteip", IpUtils.getIpAddr());
        params.put("idempotency-key", idempotencyKey);

        try {
            HttpRequest httpRequest = HttpUtil.createPost(ruoYiConfig.getTurnstileVerifyApi())
                    .contentType(ContentType.JSON.getValue())
                    .timeout(60000)
                    .body(JSONObject.toJSONString(params));

            try (HttpResponse httpResponse = httpRequest.execute()) {
                String validResultJson = httpResponse.body();
                TurnstileResult validResult = JSONObject.parseObject(validResultJson, TurnstileResult.class);

                if (!validResult.getSuccess()) {
                    List<String> errorList = validResult.getErrorCodes();

                    log.error("turnstile验证失败, 错误码: {}", errorList);

                    if (CollectionUtil.contains(errorList, TurnstileErrorCode.TIMEOUT_OR_DUPLICATE.getErrorCode())) {
                        throw new CaptchaExpireException();
                    }

                    throw new CaptchaException();
                }

            }

        } catch (Exception e) {
            log.error("turnstile验证失败: ", e);
            throw new CaptchaException();
        }



    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled) {
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
            String captcha = redisCache.getCacheObject(verifyKey);
            if (captcha == null) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                throw new CaptchaExpireException();
            }
            redisCache.deleteObject(verifyKey);
            if (!code.equalsIgnoreCase(captcha)) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                throw new CaptchaException();
            }
        }
    }

    /**
     * 登录前置校验
     * @param username 用户名
     * @param password 用户密码
     */
    public void loginPreCheck(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked")));
            throw new BlackListException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        userService.updateLoginInfo(userId, IpUtils.getIpAddr(), DateUtils.getNowDate());
    }
}
