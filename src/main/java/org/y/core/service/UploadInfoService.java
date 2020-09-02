package org.y.core.service;

import org.y.core.model.entity.UploadInfo;

/**
 * 上传信息服务接口
 */
public interface UploadInfoService {

    /**
     * 文件上传, 上传成功
     * @param info 上传信息, 成功回填ID
     */
    void upload(UploadInfo info);

    /**
     * 指定ID获取上传信息
     * @param id 上传信息ID
     * @return 上传信息
     */
    UploadInfo getById(int id);
}
