package org.y.core.service;

import org.y.core.model.condition.ModuleCondition;
import org.y.core.model.entity.Module;
import org.y.core.model.enu.ModuleStatus;
import org.y.core.util.page.Page;

import java.io.File;
import java.util.List;

/**
 * 模块服務接口
 */
public interface ModuleService {

    /**
     * 编辑模块信息, 包含ID为修改, 不包含为添加
     *
     * @param module 模块信息
     */
    void add(Module module);

    /**
     * 分页获取模块列表
     *
     * @param pc 过滤条件
     * @return 分页对象
     */
    Page<Module> getPage(ModuleCondition pc);

    /**
     * 改变模块状态
     *
     * @param id           模块ID
     * @param targetStatus 目标状态
     * @return 执行结果状态
     */
    ModuleStatus changeStatus(int id, ModuleStatus targetStatus);

    /**
     * 删除模块
     *
     * @param id 模块ID
     */
    void deleteById(int id);

    /**
     * 逆序分页获取日志内容
     *
     * @param id        模块ID
     * @param pageIndex 分页页码
     * @return 日志内容列表
     */
    List<String> pageLogContents(int id, int pageIndex);

    /**
     * 启动系统服务
     */
    void startSystemServices();

    /**
     * 获取应用模块列表
     *
     * @return 模块列表
     */
    List<Module> getApplications();

    /**
     * 关闭所有运行中模块
     */
    void shutdownAll();

    /**
     * 获取日志文件
     *
     * @param id 模块ID
     * @return 日志文件
     */
    File getLogFile(int id);

    /**
     * 更新日志目录
     *
     * @param id     模块ID
     * @param token  本次启动令牌
     * @param logDir 日志目录
     */
    void updateLogDir(int id, String token, String logDir);

    /**
     * 指定ID获取模块
     *
     * @param id 模块ID
     * @return 模块信息
     */
    Module getById(int id);

    /**
     * 编辑模块信息, 对于jarInfo和iconInfo如果值为null则不修改
     *
     * @param module 模块信息
     */
    void edit(Module module);

    /**
     * 授权登录
     *
     * @param userId   用户ID
     * @param id       模块ID
     * @param modToken 模块授权码
     * @return 用户登录模块授权码
     */
    String authorize(int userId, int id, String modToken);
}
