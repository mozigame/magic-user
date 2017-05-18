package com.magic.user.resource;

import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.tools.HeaderUtil;
import com.magic.user.resource.service.AgentResourceService;
import com.magic.user.util.PasswordCapture;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: joey
 * Date: 2017/5/13
 * Time: 15:01
 */
@Controller
@RequestMapping("/v1/user")
public class UserLoginResource {


    @Resource(name = "agentResourceService")
    private AgentResourceService agentResourceService;

    /**
     * 会员登陆
     *
     * @param request
     * @param response
     * @param code
     * @param username
     * @param password
     * @return
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String login(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "code", required = false, defaultValue = "") String code,
            @RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password

    ) {
        RequestContext rc = RequestContext.getRequestContext();
        //获取浏览器、操作系统名称等数据
        String agent = request.getHeader(HeaderUtil.USER_AGENT);
        //获取域名
        StringBuffer requestURL = request.getRequestURL();
        String url = requestURL.delete(requestURL.length() - request.getRequestURI().length(), requestURL.length()).toString();
        return agentResourceService.login(rc, agent, url, username, password, code);
    }

    /**
     * 会员注销
     *
     * @param username
     * @return
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String logout(
            HttpServletRequest request,
            @RequestParam(name = "username") String username
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        //获取浏览器、操作系统名称等数据
        String agent = request.getHeader(HeaderUtil.USER_AGENT);
        return agentResourceService.logout(rc, agent, username);
    }
}
