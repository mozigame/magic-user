package com.magic.user.agent.resource;

import com.magic.api.commons.core.auth.Access;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: joey
 * Date: 2017/5/3
 * Time: 15:47
 */
@Controller
@RequestMapping(value = "/v1/agent")
public class AgentResource {

    /**
     * @param condition 检索条件
     * @param page      当前页
     * @param count     每页数据量
     * @return
     * @Doc 代理列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String list(
            @RequestParam(name = "condition", required = false) String condition,
            @RequestParam(name = "page", required = false, defaultValue = "1") String page,
            @RequestParam(name = "count", required = false, defaultValue = "10") String count
    ) {
        return "        {\n" +
                "            \"page\":1,\n" +
                "                \"count\":10,\n" +
                "                \"total\":100,\n" +
                "                \"list\":[{\n" +
                "            \"holder\":110,\n" +
                "                    \"holderName\":\"adjh\",\n" +
                "                    \"id\":1001,\n" +
                "                    \"realname\":\"李月华\",\n" +
                "                    \"account\":\"seredios\",\n" +
                "                    \"registerTime\":\"2017-03-01 16:43:22\",\n" +
                "                    \"members\":2009,\n" +
                "                    \"storeMembers\":1340,\n" +
                "                    \"depositTotalMoney\":\"134000\",\n" +
                "                    \"withdrawTotalMoney\":\"4400\",\n" +
                "                    \"promotionCode\":\"dawciz\",\n" +
                "                    \"status\":1,\n" +
                "                    \"showStatus\":\"启用\",\n" +
                "                    \"reviewer\":\"some\",\n" +
                "                    \"reviewTime\":\"2017-03-01 16:43:22\"\n" +
                "        }]\n" +
                "        }";
    }

    /**
     * @param condition 检索条件
     * @return
     * @Doc 代理列表导出
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list/export", method = RequestMethod.GET)
    @ResponseBody
    public String listExport(
            @RequestParam(name = "condition", required = false) String condition
    ) {
        return "";
    }

    /**
     * @param holder       所属股东ID
     * @param account      代理账号
     * @param password     登录密码
     * @param realname     真实姓名
     * @param telephone    手机号码
     * @param bankCardNo   银行卡号
     * @param email        电子邮箱
     * @param returnScheme 返佣方案
     * @param adminCost    行政成本
     * @param feeScheme    手续费
     * @param domain       域名设置
     * @param discount     优惠扣除  默认1 1不选 2勾选
     * @param cost         反水成本  默认1 1不选 2勾选
     * @return
     * @Doc
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String add(
            @RequestParam(name = "holder") long holder,
            @RequestParam(name = "account") String account,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "realname") String realname,
            @RequestParam(name = "telephone") String telephone,
            @RequestParam(name = "bankCardNo") String bankCardNo,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "returnScheme") int returnScheme,
            @RequestParam(name = "adminCost") int adminCost,
            @RequestParam(name = "feeScheme") int feeScheme,
            @RequestParam(name = "domain", required = false) String[] domain,
            @RequestParam(name = "discount", required = false, defaultValue = "1") int discount,
            @RequestParam(name = "cost", required = false, defaultValue = "1") int cost
    ) {
        return "{\n" +
                "        \"id\":10003\n" +
                "    }";
    }

    /**
     * @param id 代理ID
     * @return
     * @Doc 代理详情
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public String detail(
            @RequestParam(name = "id") long id
    ) {

        return "{\n" +
                "            \"baseInfo\":{\n" +
                "            \"id\":1001,\n" +
                "                    \"account\":\"jkljklasd\",\n" +
                "                    \"status\":1,\n" +
                "                    \"showStatus\":\"启用\",\n" +
                "                    \"realname\":\"王月华\",\n" +
                "                    \"holder\":\"admoney\",\n" +
                "                    \"registerTime\":\"2017-03-13 23:33:23\",\n" +
                "                    \"registerIp\":\"171.13.8.12\",\n" +
                "                    \"lastLoginIp\":\"171.13.8.12\",\n" +
                "                    \"promotionCode\":\"4DEKLOSDFJA6\",\n" +
                "                    \"domain\":\"www.xxxxxxxijk\",\n" +
                "                    \"telephone\":\"13430180244\",\n" +
                "                    \"email\":\"liyuehua001@gmail.com\",\n" +
                "                    \"bankCardNo\":\"622848770596789\"\n" +
                "        },\n" +
                "            \"settings\":{\n" +
                "            \"returnScheme\":1,\n" +
                "                    \"returnSchemeName\":\"退佣方案1\",\n" +
                "                    \"adminCost\":1,\n" +
                "                    \"adminCostName\":\"行政成本1\",\n" +
                "                    \"feeScheme\":1,\n" +
                "                    \"feeSchemeName\":\"手续费1\"\n" +
                "        },\n" +
                "            \"fundProfile\":{\n" +
                "            \"syncTime\":\"2017-04-18 09:29:33\",\n" +
                "                    \"info\":{\n" +
                "                \"members\":490,\n" +
                "                        \"depositMembers\":410,\n" +
                "                        \"depositTotalMoney\":\"29006590\",\n" +
                "                        \"withdrawTotalMoney\":\"24500120\",\n" +
                "                        \"betTotalMoney\":\"20900067\",\n" +
                "                        \"betEffMoney\":\"19007689\",\n" +
                "                        \"gains\":\"4908763\"\n" +
                "            }\n" +
                "        }\n" +
                "        }";
    }

    /**
     * @param id       代理ID
     * @param password 密码
     * @return
     * @Doc 代理密码重置
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/password/reset", method = RequestMethod.POST)
    @ResponseBody
    public String passwordRest(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "password") String password

    ) {
        return "";
    }

    /**
     * @param id         代理ID
     * @param realname   姓名
     * @param telephone  手机号
     * @param email      电子邮箱
     * @param bankCardNo 银行卡号
     * @param status     当前状态
     * @return
     * @Doc 代理资料修改
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String update(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "realname", required = false) String realname,
            @RequestParam(name = "telephone", required = false) String telephone,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "bankCardNo", required = false) String bankCardNo,
            @RequestParam(name = "status", required = false) int status
    ) {
        return "";
    }

    /**
     * @param id           代理ID
     * @param returnScheme 退佣方案ID
     * @param adminCost    行政成本ID
     * @param feeScheme    手续费方案ID
     * @return
     * @Doc 代理参数配置修改
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    @ResponseBody
    public String settings(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "returnScheme", required = false, defaultValue = "-1") long returnScheme,
            @RequestParam(name = "adminCost", required = false, defaultValue = "-1") int adminCost,
            @RequestParam(name = "feeScheme", required = false, defaultValue = "-1") int feeScheme
    ) {
        return "";
    }

    /**
     * @param account    代理账号
     * @param password   密码
     * @param realname   真实姓名
     * @param telephone  电话
     * @param email      电子邮箱
     * @param bankCardNo 银行卡
     * @return
     * @Doc 代理申请--前端页面
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    @ResponseBody
    public String apply(
            @RequestParam(name = "account") String account,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "realname") String realname,
            @RequestParam(name = "telephone") String telephone,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "bankCardNo") String bankCardNo
    ) {
        return "";
    }

    /**
     * @param account 账号
     * @param status  状态
     * @param page    当前页
     * @param count   每页数据量
     * @return
     * @Doc 新增代理审核列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/review/list", method = RequestMethod.GET)
    @ResponseBody
    public String reviewList(
            @RequestParam(name = "account", required = false) String account,
            @RequestParam(name = "status", required = false) int status,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "count", required = false, defaultValue = "10") int count
    ) {
        return "{\n" +
                "        \"page\":1,\n" +
                "                \"count\":10,\n" +
                "                \"total\":100,\n" +
                "                \"list\":[{\n" +
                "            \"holder\":111,\n" +
                "                    \"holderName\":\"seredios\",\n" +
                "                    \"id\":1001,\n" +
                "                    \"account\":\"asdffasd\",\n" +
                "                    \"realname\":\"李月华\",\n" +
                "                    \"telephone\":\"+86 18421233421\",\n" +
                "                    \"email\":\"wdad@qq.com\",\n" +
                "                    \"status\":1,\n" +
                "                    \"showStatus\":\"未审核\",\n" +
                "                    \"source\":\"www.22431.com\",\n" +
                "                    \"registerIp\":\"13.33.11.4\",\n" +
                "                    \"operaterTime\":\"2017-03-01 16:43:22\"\n" +
                "        }]\n" +
                "    }";
    }

    /**
     * @param account 账号
     * @param status  状态
     * @return
     * @Doc 新增代理审核列表导出
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/review/list/export", method = RequestMethod.GET)
    @ResponseBody
    public String reviewListExport(
            @RequestParam(name = "account", required = false) String account,
            @RequestParam(name = "status", required = false) int status
//            股东ID（从RequestContext中获取股东ID）
    ) {
        return "";
    }

    /**
     * @param id 代理审核id
     * @return
     * @Doc 代理审核基础信息    --代理审核页
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/detail/simple", method = RequestMethod.GET)
    @ResponseBody
    public String listExport(
            @RequestParam(name = "id") long id
    ) {
        return "{\n" +
                "            \"baseInfo\":{\n" +
                "            \"id\":1001,\n" +
                "                    \"account\":\"adsfsa\",\n" +
                "                    \"realname\":\"王月华\",\n" +
                "                    \"telephone\":\"13430180244\",\n" +
                "                    \"email\":\"liyuehua001@gmail.com\",\n" +
                "                    \"bankCardNo\":\"622848770596789\",\n" +
                "        }\n" +
                "        }";
    }

    /**
     * @param id           代理审核ID
     * @param reviewStatus 审核结果
     * @param holder       所属股东ID
     * @param realname     真实姓名
     * @param telephone    手机号码
     * @param bankCardNo   银行卡号
     * @param email        电子邮箱
     * @param returnScheme 返佣方案
     * @param adminCost    行政成本
     * @param feeScheme    手续费
     * @param domain       域名设置
     * @param discount     优惠扣除 默认1 1不选 2勾选
     * @param cost         返水成本 默认1 1不选 2勾选
     * @return
     * @Doc 代理审核/拒绝
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    @ResponseBody
    public String review(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "reviewStatus") int reviewStatus,
            @RequestParam(name = "holder", required = false) long holder,
            @RequestParam(name = "realname", required = false) String realname,
            @RequestParam(name = "telephone", required = false) String telephone,
            @RequestParam(name = "bankCardNo", required = false) String bankCardNo,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "returnScheme", required = false) int returnScheme,
            @RequestParam(name = "adminCost", required = false) int adminCost,
            @RequestParam(name = "feeScheme", required = false) int feeScheme,
            @RequestParam(name = "domain", required = false) String[] domain,
            @RequestParam(name = "discount", required = false, defaultValue = "1") int discount,
            @RequestParam(name = "cost", required = false, defaultValue = "1") int cost

    ) {
        return "";
    }


}
