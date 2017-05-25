package com.magic.user.resource;

import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.resource.service.WorkerResourceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/24
 * Time: 18:36
 */
@Controller
@RequestMapping("/v1/worker")
public class WorkerResource {

    @Resource
    private WorkerResourceService workerResourceService;
    /**
     * @param account  账号
     * @param realname 姓名
     * @return
     * @Doc 子账号列表，子账号只包括工作人员账号
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String list(
            @RequestParam(name = "account", required = false, defaultValue = "") String account,
            @RequestParam(name = "realname", required = false, defaultValue = "") String realname,
            @RequestParam(name = "page", required = false, defaultValue = "") Integer page,
            @RequestParam(name = "count", required = false, defaultValue = "") Integer count
    ) {
        return workerResourceService.list(RequestContext.getRequestContext(), account, realname, page, count);
    }

    /**
     * @param account
     * @param password
     * @param realname
     * @param roleId
     * @return
     * @Doc 添加子账号
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String add(
            @RequestParam(name = "account") String account,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "realname") String realname,
            @RequestParam(name = "roleId") Integer roleId
    ) {
        return workerResourceService.add(RequestContext.getRequestContext(), account,password, realname, roleId);
    }

    /**
     * 修改子账号
     *
     * @param userId
     * @param realname
     * @param roleId
     * @return
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String update(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "realname", required = false) String realname,
            @RequestParam(name = "roleId", required = false) Integer roleId
    ) {
        return workerResourceService.update(RequestContext.getRequestContext(), userId, realname, roleId);
    }


    /**
     * 修改子账号状态
     * @param userId
     * @param status
     * @return
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    @ResponseBody
    public String updateStatus(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "status", required = false, defaultValue = "-1") Integer status
    ) {
        return workerResourceService.updateStatus(RequestContext.getRequestContext(), userId, status);
    }


    /**
     * 查询子账号信息
     * @param userId
     * @return
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public String detail(
            @RequestParam(name = "userId") Long userId
    ) {
        return workerResourceService.detail(RequestContext.getRequestContext(), userId);

    }

    /**
     * 重置子账号密码
     * @param userId
     * @param password
     * @return
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/password/reset", method = RequestMethod.POST)
    @ResponseBody
    public String pwdReset(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "password") String password
    ) {
        return workerResourceService.pwdReset(RequestContext.getRequestContext(), userId, password);
    }
}
