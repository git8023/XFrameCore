package org.y.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.y.core.model.entity.UploadInfo;

/**
 * 上传信息数据访问接口
 */
@Repository
public interface UploadInfoMapper {

    /**
     * 插入上传信息
     *
     * @param info 上传信息
     * @return 数据库受影响行数
     */
    @Insert({
            "INSERT INTO `upload_info` (",
            "    `type`,",
            "    `path`,",
            "    `size`,",
            "    `name`",
            ") VALUES (",
            "    #{info.type},",
            "    #{info.path},",
            "    #{info.size},",
            "    #{info.name}",
            ")"
    })
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "info.id")
    int insert(@Param("info") UploadInfo info);

}
