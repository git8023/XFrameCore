package org.y.core.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.y.core.service.MailService;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MailServiceImplTest {

    @Autowired
    private MailService mailService;

    @Test
    void sendSimpleMail() {
        mailService.sendSimpleMail("jinlong_8023@163.com", "测112试", "测33试223内容");
    }

    @Test
    void sendHtmlMail() {
        mailService.sendHtmlMail("jinlong_8023@163.com", "测112试", "测33试223<b>内</b>容");
    }

    @Test
    void sendAttachmentsMail() {
        mailService.sendAttachmentsMail("jinlong_8023@163.com", "测112试", "测33试223<b>内</b>容",
                new File("J:/电子照片/460_640.jpg").getAbsolutePath());
    }
}