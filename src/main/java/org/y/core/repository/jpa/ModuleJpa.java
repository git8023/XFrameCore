package org.y.core.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y.core.model.entity.Module;
import org.y.core.model.enu.ModuleStatus;
import org.y.core.model.enu.ModuleType;

import java.util.List;

@Repository
public interface ModuleJpa extends JpaRepository<Module, Integer> {

    /**
     * 指定port查询是否已存在
     *
     * @param webPort 网页端口
     * @return 模块信息
     */
    Module findByWebPort(int webPort);

    /**
     * 指定ID获取模块数据
     *
     * @param id 模块ID
     * @return 模块信息
     */
    Module findById(int id);

    /**
     * 指定类型查找模块列表
     *
     * @param type 模块类型
     * @return 模块列表
     */
    List<Module> findAllByType(ModuleType type);

    /**
     * 指定类型和状态查询模块列表
     *
     * @param type   类型
     * @param status 状态
     * @return 模块列表
     */
    List<Module> findAllByTypeAndStatus(ModuleType type, ModuleStatus status);

    /**
     * 指定状态查询模块列表
     *
     * @param status 状态
     * @return 模块列表
     */
    List<Module> findAllByStatus(ModuleStatus status);
}
