package org.y.core.web.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.y.core.model.result.Result;
import org.y.core.util.Constants;
import org.y.core.util.StringUtil;
import org.y.core.web.util.WebUtil;

import javax.servlet.http.HttpServletResponse;

/**
 * 通用处理
 */
@Slf4j
@RestController
public class FinalErrorController implements ErrorController {
    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    @ResponseBody
    public Result error(Exception e) throws Exception {
        HttpServletResponse response = WebUtil.response();
        int status = response.getStatus();
        switch (status) {
            case 404:
                String redirectUri = "/";
                String uri = WebUtil.getSession(Constants.KEY_OF_NOT_FOUND_URI);
                redirectUri += "?uri=" + (StringUtil.isNotBlank(uri) ? uri : "/");
                response.sendRedirect(redirectUri);
                return null;
            default:
                log.warn(e.getMessage(), e);
        }
        throw e;
    }
}
