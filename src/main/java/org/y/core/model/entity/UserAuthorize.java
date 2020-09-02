package org.y.core.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户授权信息
 */
@Entity
@Table
@Data
public class UserAuthorize implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    private Date createDate;

    private String token;
}
