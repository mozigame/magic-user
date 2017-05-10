package com.magic.user.stockholder.resource;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.core.auth.Access;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.CurrencyType;
import com.magic.user.enums.GeneraType;
import com.magic.user.stockholder.resource.service.StockResourceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Generated;
import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/3
 * Time: 14:45
 */
@Controller
@RequestMapping("/v1/stockholder")
public class StockholderResource {


    @Resource(name = "stockResourceService")
    private StockResourceService stockResourceService;


    /**
     * @return
     * @Doc 股东列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String list() {
        return stockResourceService.findAllStock();
    }

    /**
     * @return
     * @Doc 股东列表导出
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list/export", method = RequestMethod.GET)
    @ResponseBody
    public String listExport() {

        return "";
    }

    /**
     * @return
     * @Doc 所属股东列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list/simple", method = RequestMethod.GET)
    @ResponseBody
    public String listSimple(
            @RequestParam(name = "ids") long[] ids
    ) {

        return "{\n" +
                "            \"list\":[{\n" +
                "            \"id\":1001,\n" +
                "                    \"account\":\"seredios\"\n" +
                "        }]\n" +
                "        }";
    }

    /**
     * @param id 股东ID
     * @return
     * @Doc 股东详情
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public String detail(
            @RequestParam(name = "id") long id
    ) {
        return stockResourceService.getStockDetail(id);
    }


    /**
     * @param id       股东ID
     * @param password 新密码
     * @return
     * @Doc 股东密码重置
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/password/reset", method = RequestMethod.POST)
    @ResponseBody
    public String passwordReset(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "password") String password
    ) {
        return stockResourceService.updatePwd(id, password);
    }

    /**
     * @param id         股东ID
     * @param telephone  手机号
     * @param email      电子邮箱
     * @param bankCardNo 银行卡号
     * @param status     当前状态
     * @return
     * @Doc 股东基础信息修改
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String update(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "telephone", required = false) String telephone,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "bankCardNo", required = false) String bankCardNo,
            @RequestParam(name = "bank", required = false) String bank,
            @RequestParam(name = "status", required = false) int status
    ) {

        return stockResourceService.update(id, telephone, email, bankCardNo, bank, status);
    }

    /**
     * @param account      股东账号
     * @param password     登录密码
     * @param realname     真实姓名
     * @param telephone    手机号
     * @param currencyType 币种
     * @param email        电子邮箱
     * @param sex          性别
     * @return
     * @Doc 新增股东
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String add(
            @RequestParam(name = "account") String account,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "realname") String realname,
            @RequestParam(name = "telephone") String telephone,
            @RequestParam(name = "currencyType") int currencyType,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "sex") int sex
    ) {


        return stockResourceService.add(account, password, realname, telephone, currencyType, email, sex);
    }

    /**
     * @param id     股东ID
     * @param status 状态,1、启用，2、禁用
     * @return
     * @Doc 启用停用股东
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/disable", method = RequestMethod.POST)
    @ResponseBody
    public String disable(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "status") int status
    ) {

        return stockResourceService.disable(id, status);
    }


}
