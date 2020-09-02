package org.y.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.y.core.model.entity.UploadInfo;
import org.y.core.repository.UploadInfoRepository;
import org.y.core.service.UploadInfoService;

@Service
public class UploadInfoServiceImpl implements UploadInfoService {

    private final UploadInfoRepository uploadInfoRepository;

    @Autowired
    public UploadInfoServiceImpl(UploadInfoRepository uploadInfoRepository) {
        this.uploadInfoRepository = uploadInfoRepository;
    }

    @Override
    public void upload(UploadInfo info) {
        uploadInfoRepository.JPA.save(info);
    }

    @Override
    public UploadInfo getById(int id) {
        return uploadInfoRepository.JPA.findById(id);
    }
}
