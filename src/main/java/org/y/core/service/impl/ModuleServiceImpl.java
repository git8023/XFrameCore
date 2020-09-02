package org.y.core.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.y.core.model.condition.ModuleCondition;
import org.y.core.model.entity.Module;
import org.y.core.model.entity.UploadInfo;
import org.y.core.model.entity.User;
import org.y.core.model.entity.UserAuthorize;
import org.y.core.model.enu.ErrorCode;
import org.y.core.model.enu.ModuleStatus;
import org.y.core.model.enu.ModuleType;
import org.y.core.repository.ModuleRepository;
import org.y.core.repository.UploadInfoRepository;
import org.y.core.repository.UserRepository;
import org.y.core.service.ModuleService;
import org.y.core.service.UserAuthorizationService;
import org.y.core.util.FileUtil;
import org.y.core.util.ModuleUtil;
import org.y.core.util.ProcessUtil;
import org.y.core.util.StringUtil;
import org.y.core.util.page.MySqlDataHandler;
import org.y.core.util.page.Page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final UploadInfoRepository uploadInfoRepository;
    private final UserRepository userRepository;
    private final UserAuthorizationService userAuthorizationService;

    @Autowired
    public ModuleServiceImpl(
            ModuleRepository moduleRepository,
            UploadInfoRepository uploadInfoRepository,
            UserRepository userRepository, UserAuthorizationService userAuthorizationService) {
        this.moduleRepository = moduleRepository;
        this.uploadInfoRepository = uploadInfoRepository;
        this.userRepository = userRepository;
        this.userAuthorizationService = userAuthorizationService;
    }

    @Override
    public void add(Module module) {
        Module existModule = moduleRepository.JPA.findByWebPort(module.getWebPort());
        if (null != existModule)
            ErrorCode.EXIST.breakOff("指定端口已被模块【" + existModule.getName() + "】占用");

        module.setUploadDate(new Date());
        module.setStatus(ModuleStatus.STOP);
        moduleRepository.JPA.save(module);
    }

    @Override
    public Page<Module> getPage(ModuleCondition pc) {
        return new Page<>(pc.getPageIndex(), pc.getPageSize(), new MySqlDataHandler<Module>() {
            @Override
            protected List<Module> elements(long offset, long size) {
                List<Module> modules = moduleRepository.MAPPER.selectList(offset, size, pc);
                for (Module module : modules) {
                    int iconInfoId = module.getIconInfo().getId();
                    UploadInfo iconInfo = uploadInfoRepository.JPA.findById(iconInfoId);
                    iconInfo.setPath(null);
                    module.setIconInfo(iconInfo);

                    int jarInfoId = module.getJarInfo().getId();
                    UploadInfo jarInfo = uploadInfoRepository.JPA.findById(jarInfoId);
                    jarInfo.setPath(null);
                    module.setJarInfo(jarInfo);
                }
                return modules;
            }

            @Override
            public long getRowCount() {
                return moduleRepository.MAPPER.count(pc);
            }
        });
    }

    @Transactional
    @Override
    public ModuleStatus changeStatus(int id, ModuleStatus targetStatus) {
        Module module = moduleRepository.JPA.findById(id);
        if (null == module)
            return null;

        int webPort = module.getWebPort();
        switch (targetStatus) {

            // 启动
            case RUNNING: {
                final String token = StringUtil.genGUID();
                module.setToken(token);
                try {
                    moduleRepository.MAPPER.updateStatus(module.getId(), ModuleStatus.RUNNING);
                    ModuleUtil.CMD.startup(module);
                } catch (Exception e) {
                    log.warn("模块启动失败: " + e.getMessage(), e);
                    moduleRepository.MAPPER.updateStatus(module.getId(), ModuleStatus.STOP);
                    return ModuleStatus.STOP;
                }

                moduleRepository.JPA.save(module);
                log.info("为模块[" + module.getId() + "]生成的TOKEN[" + token + "]");
                return ModuleStatus.RUNNING;
            }

            // 停止
            case STOP: {
                new Thread(() -> {
                    RestTemplate rest = new RestTemplate();
                    String url = ModuleUtil.URL.getShutdown(module);
                    try {
                        rest.getForObject(url, Void.class);
                    } catch (Exception e) {
                        log.warn("[" + url + "]Http方式关闭失败: " + e.getMessage());
                        ProcessUtil.killProcess(webPort);
                    }
                }).start();

                // moduleRepository.MAPPER.updateStatus(module.getId(), ModuleStatus.STOP);

                module.setStatus(ModuleStatus.STOP);
                module.setToken(StringUtil.EMPTY_STRING);
                moduleRepository.JPA.save(module);

                return ModuleStatus.STOP;
            }

        }

        return targetStatus;
    }

    @Transactional
    @Override
    public void deleteById(int id) {
        // 获取模块信息
        Module module = moduleRepository.JPA.findById(id);
        if (null == module)
            return;

        // 停止模块
        changeStatus(id, ModuleStatus.STOP);

        // 删除模块文件
        UploadInfo uploadInfo = module.getJarInfo();
        int jarId = uploadInfo.getId();
        uploadInfo = uploadInfoRepository.JPA.findById(jarId);
        if (null != uploadInfo) {
            String path = uploadInfo.getPath();
            File file = new File(path);
            FileUtil.deleteQuietly(file);
        }

        // 删除应用图标
        UploadInfo iconInfo = module.getIconInfo();
        int iconId = iconInfo.getId();
        UploadInfo icon = uploadInfoRepository.JPA.findById(iconId);
        if (null != icon) {
            String path = icon.getPath();
            File file = new File(path);
            FileUtil.deleteQuietly(file);
        }

        // 删除模块信息
        moduleRepository.JPA.deleteById(id);

        // 删除上传文件记录
        uploadInfoRepository.JPA.deleteById(jarId);
        uploadInfoRepository.JPA.deleteById(iconId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> pageLogContents(int id, int pageIndex) {
        Module module = moduleRepository.JPA.findById(id);
        if (null == module)
            return Collections.EMPTY_LIST;

        try {
            File log = ModuleUtil.FILE.log(module);
            int lineTotal = pageIndex * 100;
            return FileUtil.reverseReadLines(log, lineTotal);
        } catch (RestClientException e) {
            log.warn("获取服务器日志失败: " + e.getMessage(), e);
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public void startSystemServices() {
        List<Module> modules = moduleRepository.JPA.findAllByType(ModuleType.SERVICE);
        log.info("待启动服务数量: " + modules.size());

        List<Module> failedList = new ArrayList<>();
        for (Module module : modules) {
            try {
                ModuleStatus status = changeStatus(module.getId(), ModuleStatus.RUNNING);
                if (ModuleStatus.RUNNING != status) {
                    failedList.add(module);
                }
            } catch (Exception e) {
                log.warn("服务[ " + module.getName() + " ]启动失败: " + e.getMessage(), e);
                failedList.add(module);
            }
        }

        if (!failedList.isEmpty()) {
            log.warn("启动系统服务失败数量: " + failedList.size());
            for (Module module : failedList) {
                log.warn("[id:" + module.getId() + "] [name: " + module.getName() + "]");
            }
        } else {
            log.info("系统服务启动成功!");
        }
    }

    @Override
    public List<Module> getApplications() {
        List<Module> modules = moduleRepository.JPA.findAllByTypeAndStatus(ModuleType.APPLICATION, ModuleStatus.RUNNING);
        for (Module module : modules) {
            UploadInfo jarInfo = module.getJarInfo();
            module.setJarInfo(new UploadInfo(jarInfo.getId()));

            UploadInfo iconInfo = module.getIconInfo();
            module.setIconInfo(new UploadInfo(iconInfo.getId()));
        }
        return modules;
    }

    @Override
    public void shutdownAll() {
        List<Module> modules = moduleRepository.JPA.findAllByStatus(ModuleStatus.RUNNING);
        log.info("已找到运行中模块数量: " + modules.size());

        for (Module module : modules) {
            try {
                changeStatus(module.getId(), ModuleStatus.STOP);
            } catch (Exception ignore) {
            }
        }
    }

    @Override
    public File getLogFile(int id) {
        Module module = moduleRepository.JPA.findById(id);
        return ModuleUtil.FILE.log(module);
    }

    @Override
    public void updateLogDir(int id, String token, String logDir) {
        Module module = moduleRepository.JPA.findById(id);
        if (null == module) {
            log.info("无效ID: " + id);
            return;
        }

        String mToken = module.getToken();
        boolean tokenOk = StringUtil.equals(mToken, token);
        if (!tokenOk) {
            log.info("无效token: " + token + ", 期望值: " + mToken);
            return;
        }

        module.setLogPath(logDir);
        moduleRepository.JPA.save(module);
    }

    @Override
    public Module getById(int id) {
        Module module = moduleRepository.JPA.findById(id);
        if (null != module) {
            UploadInfo jarInfo = module.getJarInfo();
            if (null != jarInfo) jarInfo.setPath(null);

            UploadInfo iconInfo = module.getIconInfo();
            if (null != iconInfo) iconInfo.setPath(null);
        }
        return module;
    }

    @Override
    public void edit(Module module) {
        Module exist = moduleRepository.JPA.findByWebPort(module.getWebPort());
        int id = module.getId();
        if (null != exist && id != exist.getId())
            ErrorCode.EXIST.breakOff("指定端口已被模块【" + exist.getName() + "】占用");

        Module old = moduleRepository.JPA.findById(id);

        old.setName(module.getName());
        old.setStatus(ModuleStatus.STOP);
        old.setUploadDate(new Date());
        old.setAuthor(module.getAuthor());
        old.setType(module.getType());
        old.setNote(module.getNote());
        old.setWebPort(module.getWebPort());

        UploadInfo jarInfo = module.getJarInfo();
        if (null != jarInfo) old.setJarInfo(jarInfo);
        UploadInfo iconInfo = module.getIconInfo();
        if (null != iconInfo) old.setIconInfo(iconInfo);

        moduleRepository.JPA.save(old);
    }

    @Transactional
    @Override
    public String authorize(int userId, int id, String modToken) {

        // 检查module是否有效
        Module module = moduleRepository.JPA.findById(id);
        if (null == module || !StringUtil.equals(modToken, module.getToken()))
            return null;

        // 检查用户是否存在
        User user = userRepository.JPA.findById(userId);
        if (null == user)
            return null;

        // 为用户本次登录授权
        UserAuthorize auth = new UserAuthorize();
        auth.setUser(user);
        auth.setModule(module);
        String userToken = StringUtil.genGUID();
        auth.setToken(userToken);
        auth.setCreateDate(new Date());
        userAuthorizationService.add(auth);

        return userToken;
    }

}
