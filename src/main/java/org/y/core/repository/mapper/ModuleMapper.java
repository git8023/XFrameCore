package org.y.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.y.core.model.condition.ModuleCondition;
import org.y.core.model.entity.Module;
import org.y.core.model.enu.ModuleStatus;
import org.y.core.util.page.PageCondition;

import java.util.List;

/**
 * 模块数据访问接口
 */
@Repository
public interface ModuleMapper {

    /**
     * 插入模块信息
     *
     * @param module 模块信息
     * @return 数据库受影响行数
     */
    @Insert({
            "INSERT INTO `module` (",
            "    `name`,",
            "    `author`,",
            "    `upload_date`,",
            "    `status`,",
            "    `note`,",
            "    `web_port`,",
            "    `jar_info`",
            ") VALUES (",
            "    #{module.name},",
            "    #{module.author},",
            "    #{module.uploadDate},",
            "    #{module.status},",
            "    #{module.note},",
            "    #{module.webPort},",
            "    #{module.jarInfo.id}",
            ")"
    })
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "module.id")
    int insertFillId(@Param("module") Module module);

    /**
     * 查询数量
     *
     * @param pc 过滤条件
     * @return 数量
     */
    @Select({
            "<script>",
            "SELECT",
            "    COUNT(1)",
            "FROM",
            "    module",
            "</script>",
    })
    int count(@Param("pc") ModuleCondition pc);

    /**
     * 分页获取列表
     *
     * @param offset 偏移量
     * @param size   页大小
     * @param pc     过滤条件
     * @return 分页列表
     */
    @Select({
            "<script>",
            "SELECT",
            "    module.id              as `id`,            ",
            "    module.`name`          as `name`,          ",
            "    module.author          as `author`,        ",
            "    module.upload_date     as `uploadDate`,    ",
            "    module.`status`        as `status`,        ",
            "    module.note            as `note`,          ",
            "    module.type            as `type`,          ",
            "    module.web_port        as `webPort`,       ",
            "    module.jar_info        as `jarInfo.id`,    ",
            "    module.icon_info       as `iconInfo.id`    ",
            "FROM",
            "    module",
            "LIMIT",
            "    #{offset}, #{size}",
            "</script>",
    })
    List<Module> selectList(
            @Param("offset") long offset,
            @Param("size") long size,
            @Param("pc") ModuleCondition pc
    );

    /**
     * 更新状态
     *
     * @param id     模块ID
     * @param status 状态
     * @return 数据库受影响行数
     */
    @Update({
            "UPDATE",
            "    `module`",
            "SET",
            "    `status` = #{status}",
            "WHERE",
            "    `id` = #{id}"
    })
    int updateStatus(@Param("id") int id, @Param("status") ModuleStatus status);
}
