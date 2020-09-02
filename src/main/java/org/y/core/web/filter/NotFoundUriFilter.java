package org.y.core.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.y.core.util.Constants;
import org.y.core.web.util.WebUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "nfuFilter", urlPatterns = "/*")
public class NotFoundUriFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();

        // 默认记录为404
        WebUtil.setSession(Constants.KEY_OF_NOT_FOUND_URI, uri);
        chain.doFilter(request, response);

        // 如果服务器正常响应, 删除404记录
        HttpServletResponse resp = (HttpServletResponse) response;
        int status = resp.getStatus();
        switch (status) {
            case 200:
                WebUtil.delSession(Constants.KEY_OF_NOT_FOUND_URI);
                break;
        }
    }
}
