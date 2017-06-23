package com.magic.user.resource;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.po.DownLoadFile;
import com.magic.user.resource.service.WorkerResourceService;
import org.apache.http.protocol.ResponseServer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String list(
            @RequestParam(name = "account", required = false, defaultValue = "") String account,
            @RequestParam(name = "realname", required = false, defaultValue = "") String realname,
            @RequestParam(name = "roleId", required = false, defaultValue = "-1") Integer roleId,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "count", required = false, defaultValue = "10") Integer count
    ) {
        return workerResourceService.list(RequestContext.getRequestContext(), account, realname, roleId, page, count);
    }

    /**
     * @param account  账号
     * @param realname 姓名
     * @return
     * @Doc 子账号列表，子账号只包括工作人员账号
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/list/export", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public void list(
            HttpServletResponse response,
            @RequestParam(name = "account", required = false, defaultValue = "") String account,
            @RequestParam(name = "realname", required = false, defaultValue = "") String realname,
            @RequestParam(name = "roleId", required = false) Integer roleId,
            @RequestParam(name = "userId") Long userId
    ) throws Exception {
        RequestContext rc = RequestContext.getRequestContext();
        rc.setUid(userId);
        DownLoadFile downLoadFile = workerResourceService.workerListExport(rc, account, realname, roleId);
        response.setCharacterEncoding("UTF-8");
        if (downLoadFile != null && downLoadFile.getContent() != null && downLoadFile.getContent().length > 0) {
            String contnetDisposition = "attachment;filename=";
            if (downLoadFile.getFilename() != null) {
                contnetDisposition += URLEncoder.encode(downLoadFile.getFilename(), "utf-8");
                response.setHeader("Location", URLEncoder.encode(downLoadFile.getFilename(), "utf-8"));
            }
            response.setHeader("Content-Disposition", contnetDisposition);
            ServletOutputStream outputStream = response.getOutputStream();
            try {
                outputStream.write(downLoadFile.getContent());
            } catch (Exception e) {
                ApiLogger.error(String.format("export excel error. file: %s", downLoadFile.getContent()), e);
            } finally {
                outputStream.flush();
                outputStream.close();
            }
        }
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
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String update(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "realname", required = false, defaultValue = "") String realname,
            @RequestParam(name = "roleId", required = false, defaultValue = "-1") Integer roleId
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
    @RequestMapping(value = "/update/status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/password/reset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String pwdReset(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "password") String password
    ) {
        return workerResourceService.pwdReset(RequestContext.getRequestContext(), userId, password);
    }


}
