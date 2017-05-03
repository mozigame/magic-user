package com.magic.user.member.resource;

import com.magic.api.commons.core.auth.Access;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: joey
 * Date: 2017/5/3
 * Time: 17:13
 */
@Component
@RequestMapping("/info")
public class MemInfoResource {


    /**
     * @param type    账号类型
     * @param account 账号名
     * @return
     * @Doc 资料查询
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public String detail(
            @RequestParam(name = "type", required = false) int type,
            @RequestParam(name = "account", required = false) String account
//            股东ID（从RequestContext中获取股东ID）
    ) {

        return " {\n" +
                "            \"id\":1001,\n" +
                "                \"type\":1,\n" +
                "                \"account\":\"ssdfwda\",\n" +
                "                \"realname\":\"李月华\",\n" +
                "                \"telephone\":\"13430180255\",\n" +
                "                \"email\":\"liyuehua009@gmail.com\",\n" +
                "                \"bankCardNo\":\"622848770596789\",\n" +
                "                \"loginPassword\":\"123456\",\n" +
                "                \"paymentPassword\":\"123456\"\n" +
                "        }";
    }

    /**
     * @param id              账号ID
     * @param type            账号类型    1业主  2股东     3代理     4会员
     * @param realname        真实姓名
     * @param telephone       手机号码
     * @param email           邮箱
     * @param bankCardNo      银行卡号
     * @param loginPassword   登录密码
     * @param paymentPassword 支付密码
     * @return
     * @Doc 资料修改
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    @ResponseBody
    public String modify(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "type") int type,
            @RequestParam(name = "realname", required = false) String realname,
            @RequestParam(name = "telephone", required = false) String telephone,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "bankCardNo", required = false) String bankCardNo,
            @RequestParam(name = "loginPassword", required = false) String loginPassword,
            @RequestParam(name = "paymentPassword", required = false) String paymentPassword
//            股东ID（从RequestContext中获取股东ID）
    ) {
        return "";
    }

    /**
     * @param type    账号类型
     * @param account 账号名
     * @param page    当前页
     * @param count   每页数据量
     * @return
     * @Doc 修改记录查询
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/modify/list", method = RequestMethod.GET)
    @ResponseBody
    public String modifyList(
            @RequestParam(name = "type") int type,
            @RequestParam(name = "account", required = false) String account,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "count", required = false, defaultValue = "10") int count
    ) {

        return "{\n" +
                "            \"page\":1,\n" +
                "                \"count\":10,\n" +
                "                \"total\":100,\n" +
                "                \"list\":[{\n" +
                "            \"id\":153452,\n" +
                "                    \"holder\":\"seredios\",\n" +
                "                    \"accountId\":1001,\n" +
                "                    \"type\":1,\n" +
                "                    \"showType\":\"代理\",\n" +
                "                    \"account\":\"ssdfwda\",\n" +
                "                    \"before\":{\n" +
                "                \"name\":\"李月华\",\n" +
                "                        \"bankCardNo\":\"62XXXXXXX\",\n" +
                "                        \"telephone\":\"133XXXXXX\",\n" +
                "                        \"email\":\"xxx@xxx.com\"\n" +
                "            },\n" +
                "            \"after\":{\n" +
                "                \"name\":\"李月华\",\n" +
                "                        \"bankCardNo\":\"62XXXXXXX\",\n" +
                "                        \"telephone\":\"133XXXXXX\",\n" +
                "                        \"email\":\"xxx@xxx.com\"\n" +
                "            },\n" +
                "            \"operatorId\":1001,\n" +
                "                    \"operatorName\":\"admin\",\n" +
                "                    \"operatorTime\":\"2017-03-01 16:43:22\"\n" +
                "        }]\n" +
                "        }";
    }

    /**
     * @param type    账号类型
     * @param account 账号名
     * @return
     * @Doc 修改记录导出
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/modify/list/export", method = RequestMethod.GET)
    @ResponseBody
    public String modifyListExport(
            @RequestParam(name = "type") int type,
            @RequestParam(name = "account", required = false) String account
    ) {
        return "";
    }


}
