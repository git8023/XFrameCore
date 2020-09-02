package org.y.core.web.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y.core.model.condition.ModuleCondition;
import org.y.core.model.entity.Module;
import org.y.core.model.entity.User;
import org.y.core.model.enu.ModuleStatus;
import org.y.core.model.result.Result;
import org.y.core.service.ModuleService;
import org.y.core.util.ModuleUtil;
import org.y.core.util.StringUtil;
import org.y.core.util.page.Page;
import org.y.core.web.util.WebUtil;

import java.io.File;
import java.util.List;

/**
 * 模块控制器, 用于控制模块添加/获取列表/启动/停止等操作
 */
@RestController
@RequestMapping("/module")
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    /**
     * 添加模块
     *
     * @param module 模块信息
     */
    @RequestMapping("/add")
    public Result add(Module module) {
        moduleService.add(module);
        return Result.success();
    }

    /**
     * 分页获取模块列表
     *
     * @param pc 过滤条件
     */
    @RequestMapping("/list")
    public Result list(ModuleCondition pc) {
        Page<Module> page = moduleService.getPage(pc);
        return Result.data(page);
    }

    /**
     * 改变模块运行状态
     *
     * @param id     模块ID
     * @param status 目标状态
     * @return 返回执行结果状态
     */
    @RequestMapping("/status/{id}/{status}")
    public Result changeStatus(@PathVariable int id, @PathVariable ModuleStatus status) {
        ModuleStatus resultStatus = moduleService.changeStatus(id, status);
        return Result.data(resultStatus);
    }

    /**
     * 删除模块记录
     *
     * @param id 模块ID
     */
    @RequestMapping("/del/{id}")
    public Result delete(@PathVariable int id) {
        moduleService.deleteById(id);
        return Result.success();
    }

    /**
     * 获取启动日志
     *
     * @param id    模块ID
     * @param index 分页日志逆序分页页码
     */
    @RequestMapping("/log/{id}/{index}")
    public Result logList(@PathVariable int id, @PathVariable int index) {
        List<String> logs = moduleService.pageLogContents(id, index);
        return Result.data(logs);
    }

    /**
     * 获取应用模块列表
     */
    @RequestMapping("/apps")
    public Result applicationList() {
        List<Module> modules = moduleService.getApplications();
        return Result.data(modules);
    }

    /**
     * 重定向到指定模块应用首页
     *
     * @param id 模块ID
     */
    @RequestMapping("/appIndex/{id}")
    public void appIndex(@PathVariable int id) {
        Module module = moduleService.getById(id);
        if (null != module) {
            String indexUrl = ModuleUtil.URL.indexUri(module);
            User user = WebUtil.getUser();
            int userId = user.getId();
            WebUtil.redirect("/post.html?uri=" + indexUrl + "&port=" + module.getWebPort() + "&user=" + userId);
            // WebUtil.forward("/post.html?uri=" + indexUrl + "&port=" + module.getWebPort() + "&user=" + userId);
        }
    }

    /**
     * 日志文件下载
     *
     * @param id 模块ID
     */
    @RequestMapping("/logDownload/{id}")
    public void logDownload(@PathVariable int id) {
        File logFile = moduleService.getLogFile(id);
        WebUtil.writeFile(logFile);
    }

    /**
     * 设置日志文件目录
     *
     * @param id     模块ID
     * @param token  本次启动令牌
     * @param logDir 日志目录
     */
    @RequestMapping("/logDir/{id}/{token}/{logDir}")
    public Result logDir(@PathVariable int id, @PathVariable String token, @PathVariable String logDir) {
        logDir = StringUtil.base64Dec(logDir);
        moduleService.updateLogDir(id, token, logDir);
        return Result.success();
    }

    /**
     * 获取详情
     *
     * @param id 模块ID
     */
    @RequestMapping("/detail/{id}")
    public Result detail(@PathVariable int id) {
        Module module = moduleService.getById(id);
        return Result.data(module);
    }

    /**
     * 编辑模块信息
     *
     * @param module 模块信息
     */
    @RequestMapping("/edit")
    public Result edit(Module module) {
        moduleService.edit(module);
        return Result.success();
    }

    /**
     * 授权用户登录当前模块
     *
     * @param userId 用户ID
     * @param mid    模块ID
     * @param token  模块令牌
     * @return 授权码
     */
    @RequestMapping("/auth/{mid}/{token}/{userId}")
    public Result auth(@PathVariable int userId, @PathVariable int mid, @PathVariable String token) {
        String retToken = moduleService.authorize(userId, mid, token);
        return Result.data(retToken);
    }
}
