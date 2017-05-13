package com.magic.user.resource;

import com.magic.api.commons.core.context.RequestContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: joey
 * Date: 2017/5/13
 * Time: 14:57
 */
@Controller
@RequestMapping("/v1/help")
public class HelpResource {

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        RequestContext rc = RequestContext.getRequestContext();
        return rc.getIp();
    }
}
