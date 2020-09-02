package org.y.core.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.y.core.model.enu.ModuleStatus;
import org.y.core.model.enu.ModuleType;

import javax.persistence.*;
import java.util.Date;

/**
 * 模块实体
 */
@Entity
@Table(name = "module")
@Data
@EqualsAndHashCode(of = {"id"})
public class Module {
    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 模块名称
    private String name;

    // 作者
    private String author;

    // 上传日期
    private Date uploadDate;

    // 状态
    private ModuleStatus status;

    // 描述
    private String note;

    // 端口
    private int webPort;

    // 文件路径
    @OneToOne
    @JoinColumn(name = "jar_info")
    private UploadInfo jarInfo;

    // 是否跟随系统自动运行
    private ModuleType type;

    // 启动图标
    @OneToOne
    @JoinColumn(name = "icon_info")
    private UploadInfo iconInfo;

    // 日志文件路径
    private String logPath;

    // 内部通信token
    private String token;
}
