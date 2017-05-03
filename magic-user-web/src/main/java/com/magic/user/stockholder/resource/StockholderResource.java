package com.magic.user.stockholder.resource;

import com.magic.api.commons.core.auth.Access;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: joey
 * Date: 2017/5/3
 * Time: 14:45
 */
@Controller
@RequestMapping("/v1/stockholder")
public class StockholderResource {


    /**
     * @return
     * @Doc 股东列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String list() {

        return "{\n" +
                "            \"list\":[{\n" +
                "            \"id\":1001,\n" +
                "                    \"realname\":\"股东\",\n" +
                "                    \"account\":\"seredios\",\n" +
                "                    \"currencyType\":\"人民币\",\n" +
                "                    \"agentNumber\":189,\n" +
                "                    \"registerTime\":\"2017-02-16 22:00:22\",\n" +
                "                    \"status\":1,\n" +
                "                    \"showStatus\":\"启用\"\n" +
                "        }]\n" +
                "        }";
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
        return " {\n" +
                "            \"baseInfo\":{\n" +
                "            \"id\":1002,\n" +
                "                    \"realname\":\"李月华\",\n" +
                "                    \"telephone\":\"13430180244\",\n" +
                "                    \"bankCardNo\":\"622848770596789\",\n" +
                "                    \"email\":\"liyuehua0019@gmail.com\",\n" +
                "                    \"registerIp\":\"172.13.8.12\",\n" +
                "                    \"registerTime\":\"2017-03-13 23:33:23\",\n" +
                "                    \"status\":1,\n" +
                "                    \"showStatus\":\"启用\",\n" +
                "                    \"lastLoginIp\":\"171.13.8.12\",\n" +
                "                    \"agentNumber\":899,\n" +
                "                    \"members\":8990000\n" +
                "        },\n" +
                "            \"operation\":{\n" +
                "            \"syncTime\":\"2017-04-18 09:29:33\",\n" +
                "                    \"info\":{\n" +
                "                \"bets\":129000,\n" +
                "                        \"notes\":34560000,\n" +
                "                        \"betTotalMoney\":\"80500000\",\n" +
                "                        \"betEffMoney\":\"78966789\",\n" +
                "                        \"gains\":\"5800000\"\n" +
                "            }\n" +
                "        }\n" +
                "        }";
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
        return "";
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
            @RequestParam(name = "status", required = false) int status
    ) {
        return "";
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
        return "{\n" +
                "        \"id\":10003\n" +
                "    }";
    }


}
