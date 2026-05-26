package cn.hy.web.controller.fikaweb;

import cn.hy.common.core.controller.BaseController;
import cn.hy.common.core.domain.AjaxResult;
import cn.hy.common.core.page.TableDataInfo;
import cn.hy.fikaweb.service.FikawebProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fikaweb")
@RequiredArgsConstructor
@Slf4j
public class FikawebProfileController extends BaseController {

    private final FikawebProfileService fikawebProfileService;

    @GetMapping("/profiles")
    @PreAuthorize("@ss.hasPermi('fika:profile:list')")
    public TableDataInfo getProfileList() {
        return getDataTable(fikawebProfileService.list());
    }
}
