package org.y.core.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.y.core.config.Configure;
import org.y.core.model.entity.UploadInfo;
import org.y.core.model.result.Result;
import org.y.core.service.UploadInfoService;
import org.y.core.util.FileUtil;
import org.y.core.web.util.WebUtil;

import java.io.File;


@RestController
@RequestMapping("/upload")
public class UploadController {

    private final UploadInfoService uploadInfoService;
    private final Configure configure;

    @Autowired
    public UploadController(UploadInfoService uploadInfoService,
                            Configure configure) {
        this.uploadInfoService = uploadInfoService;
        this.configure = configure;
    }

    /**
     * 文件上传
     *
     * @param file 上传文件
     * @return 上传成功返回文件信息ID(> 0)
     */
    @RequestMapping("/up")
    public Result upload(MultipartFile file) {
        FileUtil.FileWrapper fileWrapper = FileUtil.upload(file, configure.getModuleDirectory());
        UploadInfo info = new UploadInfo();
        if (null != fileWrapper) {
            info.setPath(fileWrapper.PATH);
            info.setSize(fileWrapper.FILE.length());
            info.setType(fileWrapper.FILE_TYPE);
            info.setName(fileWrapper.ORIGIN_FILE_NAME);
            info.setRelativePath(fileWrapper.RELATIVE_PATH);
            uploadInfoService.upload(info);
        }
        return Result.data(info.getId());
    }

    /**
     * 页面显示图片
     *
     * @param id 上传信息ID
     */
    @RequestMapping("/img/{id}")
    public void image(@PathVariable int id) {
        UploadInfo info = uploadInfoService.getById(id);
        if (null != info)
            WebUtil.writeImage(new File(info.getPath()));
    }

}
