package cn.hy.framework.security.handle;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.alibaba.fastjson2.JSON;
import cn.hy.common.constant.Constants;
import cn.hy.common.core.domain.AjaxResult;
import cn.hy.common.core.domain.model.LoginUser;
import cn.hy.common.utils.MessageUtils;
import cn.hy.common.utils.ServletUtils;
import cn.hy.common.utils.StringUtils;
import cn.hy.framework.manager.AsyncManager;
import cn.hy.framework.manager.factory.AsyncFactory;
import cn.hy.framework.web.service.TokenService;

/**
 * 自定义退出处理类 返回成功
 * 
 * @author ruoyi
 */
@Configuration
@RequiredArgsConstructor
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    private final TokenService tokenService;

    /**
     * 退出处理
     * 
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, MessageUtils.message("user.logout.success")));
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success(MessageUtils.message("user.logout.success"))));
    }
}
