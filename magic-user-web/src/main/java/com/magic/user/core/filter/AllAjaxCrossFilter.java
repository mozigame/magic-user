package com.magic.user.core.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: joey
 * Date: 2017/5/4
 * Time: 11:08
 */
public class AllAjaxCrossFilter implements Filter {

    private FilterConfig config = null;

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.config = config;
    }

    @Override
    public void destroy() {
        this.config = null;
    }

    /**
     * @author wwhhf
     * @comment 跨域的设置
     * @since 2016/5/30
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Headers", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
//        httpResponse.setHeader("Content-Type", "application/json;charset=utf-8");
        chain.doFilter(request, response);
    }
}
