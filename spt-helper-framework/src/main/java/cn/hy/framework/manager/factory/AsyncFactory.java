package cn.hy.framework.manager.factory;

import java.util.TimerTask;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.hy.common.constant.Constants;
import cn.hy.common.utils.LogUtils;
import cn.hy.common.utils.ServletUtils;
import cn.hy.common.utils.StringUtils;
import cn.hy.common.utils.http.UserAgentUtils;
import cn.hy.common.utils.ip.AddressUtils;
import cn.hy.common.utils.ip.IpUtils;
import cn.hy.common.utils.spring.SpringUtils;
import cn.hy.system.domain.SysLogininfor;
import cn.hy.system.domain.SysOperLog;
import cn.hy.system.service.ISysLogininforService;
import cn.hy.system.service.ISysOperLogService;

/**
 * 异步工厂（产生任务用）
 * 
 * @author ruoyi
 */
@Slf4j(topic = "sys-user")
public class AsyncFactory {
    /**
     * 记录登录信息
     * 
     * @param username 用户名
     * @param status 状态
     * @param message 消息
     * @param args 列表
     * @return 任务task
     */
    public static TimerTask recordLogininfor(final String username, final String status, final String message,
            final Object... args) {
        final String userAgent = ServletUtils.getRequest().getHeader("User-Agent");
        final String ip = IpUtils.getIpAddr();
        return new TimerTask() {
            @Override
            public void run() {
                String address = AddressUtils.getRealAddressByIP(ip);
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(address);
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(status));
                s.append(LogUtils.getBlock(message));
                // 打印信息到日志
                log.info(s.toString(), args);
                // 获取客户端操作系统
                String os = UserAgentUtils.getOperatingSystem(userAgent);
                // 获取客户端浏览器
                String browser = UserAgentUtils.getBrowser(userAgent);
                // 封装对象
                SysLogininfor logininfor = new SysLogininfor();
                logininfor.setUserName(username);
                logininfor.setIpaddr(ip);
                logininfor.setLoginLocation(address);
                logininfor.setBrowser(browser);
                logininfor.setOs(os);
                logininfor.setMsg(message);
                // 日志状态
                if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
                    logininfor.setStatus(Constants.SUCCESS);
                }
                else if (Constants.LOGIN_FAIL.equals(status)) {
                    logininfor.setStatus(Constants.FAIL);
                }
                // 插入数据
                SpringUtils.getBean(ISysLogininforService.class).insertLogininfor(logininfor);
            }
        };
    }

    /**
     * 操作日志记录
     * 
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final SysOperLog operLog) {
        return new TimerTask() {
            @Override
            public void run() {
                // 远程查询操作地点
                operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
                SpringUtils.getBean(ISysOperLogService.class).insertOperlog(operLog);
            }
        };
    }
}
