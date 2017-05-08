package com.magic.user.member.resource;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.model.Page;
import com.magic.user.entity.User;
import com.magic.user.member.resource.service.MemberResourceService;
import com.magic.user.vo.UserCondition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/1
 * Time: 19:05
 */
@Controller
@RequestMapping("/v1/member")
public class MemberResource {


    @Resource(name = "memberResourceService")
    private MemberResourceService memberResourceService;

    /**
     * @param condition 检索条件
     * @param page      当前页
     * @param count     每页数据量
     * @return
     * @Doc 会员列表查询
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String list(
            @RequestParam(name = "condition", required = false) String condition,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "count", required = false, defaultValue = "10") int count
    ) {
        UserCondition userCondition = JSONObject.parseObject(condition, UserCondition.class);
        userCondition.setPageNo(page);
        userCondition.setPageSize(count);
        Page<User> userPage = memberResourceService.findByPage(userCondition);
        JSONObject resultObj = new JSONObject();
        resultObj.put("page", page);
        resultObj.put("count", count);
        resultObj.put("total", userPage.getTotalCount());
        resultObj.put("list", userPage.getResult());
        return resultObj.toJSONString();
    }

    /**
     * @param condition 检索条件
     * @return
     * @Doc 会员列表导出
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
     * @param id 会员id
     * @return
     * @Doc 会员详情信息查询
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public String detail(
            @RequestParam(name = "id") long id
    ) {

        return " {\n" +
                "            \"baseInfo\":{\n" +
                "            \"id\":100,\n" +
                "                    \"account\":\"asdffasd\",\n" +
                "                    \"agentId\":10001,\n" +
                "                    \"agent\":\"adjh\",\n" +
                "                    \"realname\":\"王月华\",\n" +
                "                    \"registerTime\":\"2017-03-13 23:33:23\",\n" +
                "                    \"telephone\":\"13430180244\",\n" +
                "                    \"registerIp\":\"171.13.8.12\",\n" +
                "                    \"email\":\"liyuehua001@gmail.com\",\n" +
                "                    \"status\":1,\n" +
                "                    \"showStatus\":\"启用\",\n" +
                "                    \"bankCardNo\":\"622848770596789\",\n" +
                "                    \"lastLoginIp\":\"171.13.8.12\"\n" +
                "        },\n" +
                "            \"preferScheme\":{\n" +
                "            \"level\":1,\n" +
                "                    \"showLevel\":\"未分层\",\n" +
                "                    \"onlineDiscount\":\"100返10\",\n" +
                "                    \"depositFee\":\"无\",\n" +
                "                    \"withdrawFee\":\"无\",\n" +
                "                    \"returnWater\":\"返水基本1\",\n" +
                "                    \"depositDiscountScheme\":\"100返10\"\n" +
                "        },\n" +
                "            \"fundProfile\":{\n" +
                "            \"syncTime\":\"2017-04-18 09:29:33\",\n" +
                "                    \"info\":{\n" +
                "                \"balance\":\"1805.50\",\n" +
                "                        \"depositNumbers\":15,\n" +
                "                        \"depositTotalMoney\":\"29006590\",\n" +
                "                        \"lastDeposit\":\"1200\",\n" +
                "                        \"withdrawNumbers\":10,\n" +
                "                        \"withdrawTotalMoney\":\"24500120\",\n" +
                "                        \"lastWithdraw\":\"2500\"\n" +
                "            }\n" +
                "        },\n" +
                "            \"betHistory\":{\n" +
                "            \"totalMoney\":\"29000\",\n" +
                "                    \"effMoney\":\"28000\",\n" +
                "                    \"gains\":\"18000\"\n" +
                "        },\n" +
                "            \"discountHistory\":{\n" +
                "            \"totalMoney\":\"1350\",\n" +
                "                    \"numbers\":98,\n" +
                "                    \"returnWaterTotalMoney\":\"1450\"\n" +
                "        }\n" +
                "        }";
    }

    /**
     * @param id       会员ID
     * @param password 新密码
     * @return
     * @Doc 会员登录密码重置
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/back/password/reset", method = RequestMethod.POST)
    @ResponseBody
    public String backPasswordRest(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "password") String password
//            股东ID（从RequestContext中获取股东ID）
    ) {
        return "";
    }

    /**
     * @param id 会员ID
     * @return
     * @Doc 会员强制下线
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/force/logout", method = RequestMethod.POST)
    @ResponseBody
    public String forceLogout(
            @RequestParam(name = "id") long id
//            股东ID（从RequestContext中获取股东ID）
    ) {
        return "";
    }

    /**
     * @param id         会员ID
     * @param realname   姓名
     * @param telephone  手机号
     * @param email      电子邮箱
     * @param bankCardNo 银行卡号
     * @param status     当前状态
     * @return
     * @Doc 会员基础信息修改
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
//          股东ID（从RequestContext中获取股东ID）
    ) {
        return "";
    }

    /**
     * @param id    会员ID
     * @param level 层级ID
     * @return
     * @Doc 会员层级修改
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/update", method = RequestMethod.POST)
    @ResponseBody
    public String levelUpdate(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "level") int level
//            股东ID（从RequestContext中获取股东ID）
    ) {
        return "";
    }

    /**
     * @param lock  是否锁定分层  默认1 1：非锁定 2锁定
     * @param page  当前页
     * @param count 每页数据量
     * @return
     * @Doc 会员层级列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/list", method = RequestMethod.GET)
    @ResponseBody
    public String levelList(
            @RequestParam(name = "lock", required = false, defaultValue = "1") int lock,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "count", required = false, defaultValue = "10") int count
//            股东ID（从RequestContext中获取股东ID）
    ) {

        return " {\n" +
                "            \"page\":1,\n" +
                "                \"count\":10,\n" +
                "                \"total\":100,\n" +
                "                \"list\":[{\n" +
                "            \"id\":1,\n" +
                "                    \"name\":\"VIP1\",\n" +
                "                    \"createTime\":\"2017-03-01 16:43:22\",\n" +
                "                    \"members\":4310,\n" +
                "                    \"condition\":{\n" +
                "                \"depositNumbers\":1,\n" +
                "                        \"depositTotalMoney\":\"1\",\n" +
                "                        \"maxDepositMoney\":\"0\",\n" +
                "                        \"withdrawNumbers\":0,\n" +
                "                        \"withdrawTotalMoney\":\"0\"\n" +
                "            },\n" +
                "            \"returnWater\":1,\n" +
                "                    \"returnWaterName\":\"返水方案1\",\n" +
                "                    \"discount\":1,\n" +
                "                    \"discountName\":\"出入款优惠1\"\n" +
                "        }]\n" +
                "        }";
    }

    /**
     * @param lock 是否锁定分层  默认1 1：非锁定 2锁定
     * @return
     * @Doc 会员层级列表导出
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/list/export", method = RequestMethod.GET)
    @ResponseBody
    public String levelListExport(
            @RequestParam(name = "lock", required = false, defaultValue = "1") int lock
//            股东ID（从RequestContext中获取股东ID）
    ) {
        return "";
    }

    /**
     * @param levelId            层级ID
     * @param depositNumbers     存款次数
     * @param depositTotalMoney  存款总额
     * @param maxDepositMoney    最大存款数额
     * @param withdrawNumbers    取款次数
     * @param withdrawTotalMoney 取款总额
     * @return
     * @Doc 设置层级分层条件
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/condition/update", method = RequestMethod.POST)
    @ResponseBody
    public String levelConditionUpdate(
            @RequestParam(name = "level") int levelId,
            @RequestParam(name = "depositNumbers") int depositNumbers,
            @RequestParam(name = "depositTotalMoney") int depositTotalMoney,
            @RequestParam(name = "maxDepositMoney") int maxDepositMoney,
            @RequestParam(name = "withdrawNumbers") int withdrawNumbers,
            @RequestParam(name = "withdrawTotalMoney") int withdrawTotalMoney
//            股东ID（从RequestContext中获取股东ID）
    ) {
        return "";
    }

    /**
     * @param condition 检索条件
     * @param page      当前页
     * @param count     每页数据量
     * @return
     * @Doc 某层级下会员列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/list/special", method = RequestMethod.GET)
    @ResponseBody
    public String levelListSpecial(
            @RequestParam(name = "condition") String condition,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "count", required = false, defaultValue = "10") int count
//            股东ID（从RequestContext中获取股东ID）
    ) {

        if (condition != null) {
            UserCondition userCondition = JSONObject.parseObject(condition, UserCondition.class);
            userCondition.setPageNo(page);
            userCondition.setPageSize(count);
            Page<User> userPage = memberResourceService.findByPage(userCondition);
            JSONObject resultObj = new JSONObject();
            resultObj.put("page", page);
            resultObj.put("count", count);
            resultObj.put("total", userPage.getTotalCount());
            resultObj.put("list", userPage.getResult());
            return resultObj.toJSONString();
        }

        return " {\n" +
                "            \"page\":1,\n" +
                "                \"count\":10,\n" +
                "                \"total\":100,\n" +
                "                \"list\":[{\n" +
                "            \"id\":1001,\n" +
                "                    \"account\":\"asdffasd\",\n" +
                "                    \"agentId\":100,\n" +
                "                    \"agent\":\"adjh\",\n" +
                "                    \"level\":\"未分层\",\n" +
                "                    \"balance\":2009,\n" +
                "                    \"registerTime\":\"2017-03-01 16:43:22\",\n" +
                "                    \"lastLoginTime\":\"2017-04-17 18:03:22\",\n" +
                "                    \"status\":1,\n" +
                "                    \"showStatus\":\"未审核\"\n" +
                "        }]\n" +
                "        }";
    }

    /**
     * @param condition 检索条件
     * @return
     * @Doc 某层级系会员列表导出
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/list/special/export", method = RequestMethod.GET)
    @ResponseBody
    public String levelListSpecialExport(
            @RequestParam(name = "condition") String condition
//            股东ID（从RequestContext中获取股东ID）
    ) {
        return "";
    }

//    @Access(type = Access.AccessType.COMMON)
//    @RequestMapping(value = "/register", method = RequestMethod.POST)
//    @ResponseBody
//    public String register(
//            @RequestParam("realname") String realname,
//            @RequestParam("username") String username,
//            @RequestParam("telephone") String telephone,
//            @RequestParam("bankCardNo") String bankCardNo,
//            @RequestParam("realname") String realname,
//            @RequestParam("realname") String realname,
//            @RequestParam("realname") String realname,
//            @RequestParam("realname") String realname,
//            @RequestParam("realname") String realname,
//            @RequestParam("realname") String realname,
//            @RequestParam("realname") String realname,
//
//    ) {
//
//    }

}
