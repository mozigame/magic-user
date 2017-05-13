package com.magic.user.resource;

import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.context.RequestContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: joey
 * Date: 2017/5/13
 * Time: 14:57
 */
@Controller
@RequestMapping("/v1/help")
public class HelpResource {

    @Access(type = Access.AccessType.INTERNAL)
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    @ResponseBody
    public String ping() {
        RequestContext rc = RequestContext.getRequestContext();
        String ip = rc.getIp();
        return ip;
    }
}
