package org.y.core.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 上传信息实体类
 */
@Data
@Entity
@Table(name = "upload_info")
@NoArgsConstructor
public class UploadInfo {
    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 类型(后缀)
    private String type;

    // 路径
    private String path;

    // 相对路径
    private String relativePath;

    // 大小
    private long size;

    // 文件名
    private String name;

    public UploadInfo(int id) {
        this.id = id;
    }
}
