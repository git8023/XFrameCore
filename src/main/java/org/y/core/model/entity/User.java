package org.y.core.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 登录用户名
    private String username;

    // 登录密码
    private String password;

    // 邮箱
    private String email;

    // 创建时间
    private Date createDate;

    // 最后修改时间
    private Date lastModified;

}
