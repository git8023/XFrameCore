package org.y.core;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class RestTemplateTest {

    @Test
    void get() {
        RestTemplate rest = new RestTemplate();
        int id = 1;
        String token = "2";
        String logFileDir = "/aab";
        String url = "http://localhost/module/logDir/" + id + "/" + token + "/" + logFileDir;
        String result = rest.getForObject(url, String.class);
        System.out.println(result);
    }

}
