package com.magic.user.resource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.tools.MauthUtil;
import com.magic.api.commons.mq.Producer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.DateUtil;
import com.magic.api.commons.tools.IPUtil;
import com.magic.api.commons.tools.UUIDUtil;
import com.magic.api.commons.utils.StringUtils;
import com.magic.service.java.UuidService;
import com.magic.user.bean.UserCondition;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.*;
import com.magic.user.enums.*;
import com.magic.user.exception.UserException;
import com.magic.user.po.DownLoadFile;
import com.magic.user.po.RegisterReq;
import com.magic.user.resource.service.AgentResourceService;
import com.magic.user.service.*;
import com.magic.user.vo.AgentApplyVo;
import com.magic.user.vo.AgentConfigVo;
import com.magic.user.vo.AgentInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 20:10
 */
@Service("agentResourceService")
public class AgentResourceServiceImpl implements AgentResourceService {

    @Resource(name = "agentConfigService")
    private AgentConfigService agentConfigService;
    @Resource(name = "ownerStockAgentService")
    private OwnerStockAgentService ownerStockAgentService;
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "loginService")
    private LoginService loginService;
    @Resource(name = "agentApplyService")
    private AgentApplyService agentApplyService;
    @Resource(name = "agentReviewService")
    private AgentReviewService agentReviewService;
    @Resource
    private UuidService uuidService;
    @Resource
    private AccountIdMappingService accountIdMappingService;
    @Resource
    private Producer producer;


    @Override
    public String findByPage(UserCondition userCondition, int page, int count) {

        //todo 1、在Kevin 处根据条件获取代理的id
        //todo 2、在kevin 处获取会员存款的数据

        List<Long> agentIds = Lists.newArrayList();
        List<AgentInfoVo> list = assembleAgentList(userService.findAgents(agentIds));
        if (list != null && list.size() > 0) {

            JSONObject jsonObject = new JSONObject();
            //todo
            jsonObject.put("page", 0);
            jsonObject.put("count", 0);
            jsonObject.put("total", count);
            jsonObject.put("list", list);
            return jsonObject.toJSONString();
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * @param users
     * @return
     * @Doc 封装代理列表
     */
    private List<AgentInfoVo> assembleAgentList(List<AgentInfoVo> users) {
        for (AgentInfoVo vo : users) {
            vo.setShowStatus(AccountStatus.parse(vo.getStatus()).desc());
        }
        return users;
    }

    /**
     * @param rc        RequestContext
     * @param condition 查询条件
     * @return
     * @Doc 导出代理列表信息
     */
    public DownLoadFile agentListExport(RequestContext rc, String condition) {
        UserCondition memberCondition = UserCondition.valueOf(condition);

        long uid = rc.getUid(); //业主ID、股东或代理ID
        String filename = assembleFileName(uid, "代理列表");
        DownLoadFile downLoadFile = new DownLoadFile();
        downLoadFile.setFilename(filename);
        //TODO 查询表数据，生成excel的zip，并返回zip byte[]
        byte[] content = new byte[5];
        downLoadFile.setContent(content);
        return downLoadFile;
    }

    /**
     * 组装文件名
     *
     * @param uid
     * @param name
     * @return
     */
    private String assembleFileName(long uid, String name) {
        StringBuilder filename = new StringBuilder();
        filename.append(uid);
        filename.append("-");
        filename.append(name);
        filename.append(System.currentTimeMillis());
        filename.append(".xlsx");
        return filename.toString();
    }

    @Override
    public String add(RequestContext rc, HttpServletRequest request, Long holder, String account, String password, String realname, String telephone, String bankCardNo, String email, Integer returnScheme,
                      Integer adminCost, Integer feeScheme, String[] domain, Integer discount, Integer cost) {
        String generalizeCode = UUIDUtil.getCode();
        RegisterReq req = assembleRegister(account, password);
        if (!checkRegisterAgentParam(req))
            throw UserException.ILLEGAL_PARAMETERS;
        User opera = userService.get(rc.getUid());
        if (opera == null) {
            throw UserException.ILLEGAL_USER;
        }
        User holderUser = userService.get(holder);
        if (holderUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        if (accountIdMappingService.getUid(holderUser.getOwnerId(), account) > 0) {
            throw UserException.USERNAME_EXIST;
        }

        long userId = uuidService.assignUid();
        if (userId <= 0)
            throw UserException.ILLEGAL_USER;
        //1、添加业主id账号映射消息
        OwnerAccountUser ownerAccountUser = new OwnerAccountUser(holderUser.getOwnerId() + UserContants.SPLIT_LINE + account, userId);
        if (accountIdMappingService.add(ownerAccountUser) <= 0) {
            ApiLogger.error(String.format("add ownerAccountUser failed,ownerId:%d,account:%d,agentId:%d", holderUser.getOwnerId(), account, userId));
            throw UserException.REGISTER_FAIL;
        }

        //2、添加代理登录信息
        Login login = new Login(userId, account, password);
        if (loginService.add(login) <= 0) {
            ApiLogger.error("add agent login failed,userId:" + userId);
            throw UserException.REGISTER_FAIL;
        }
        //3、添加代理基础信息
        User agentUser = assembleAgent(userId, holderUser.getOwnerId(), holderUser.getOwnerName(), realname, account, telephone, email, AccountType.agent, System.currentTimeMillis(), IPUtil.ipToInt(rc.getIp()), generalizeCode, AccountStatus.enable, bankCardNo);
        if (userService.addAgent(agentUser)) {
            ApiLogger.error("add agent info failed,userId:" + userId);
            throw UserException.REGISTER_FAIL;
        }
        String domainSpit = StringUtils.arrayToStrSplit(domain);
        //mq 处理 4、添加代理配置
        AgentConfig agentConfig = assembleAgentConfig(userId, returnScheme, adminCost, feeScheme, domainSpit, discount, cost);
        sendAgentConfigMq(agentConfig);
        //mq 处理 5、添加业主股东代理id映射信息
        OwnerStockAgentMember ownerStockAgentMember = assembleOwnerStockAgent(holderUser.getOwnerId(), holder, userId);
        sendAllUserIdMapping(ownerStockAgentMember);

        JSONObject result = new JSONObject();
        result.put("id", userId);
        return result.toJSONString();
    }

    private User assembleAgent(Long userId, long ownerId, String ownerName, String realname, String username, String telephone, String email, AccountType type, Long registerTime,
                               Integer registerIp, String generalizeCode, AccountStatus status, String bankCardNo) {
        User user = new User();
        user.setUserId(userId);
        user.setOwnerId(ownerId);
        user.setOwnerName(ownerName);
        user.setRealname(realname);
        user.setUsername(username);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setType(type);
        user.setRegisterTime(registerTime);
        user.setRegisterIp(registerIp);
        user.setGeneralizeCode(generalizeCode);
        user.setStatus(status);
        user.setBankCardNo(bankCardNo);
        return user;

    }

    private OwnerStockAgentMember assembleOwnerStockAgent(Long ownerId, Long stockId, Long agentId) {
        OwnerStockAgentMember ownerStockAgentMember = new OwnerStockAgentMember();
        ownerStockAgentMember.setOwnerId(ownerId);
        ownerStockAgentMember.setStockId(stockId);
        ownerStockAgentMember.setAgentId(agentId);
        ownerStockAgentMember.setMemNumber(0);
        return ownerStockAgentMember;
    }

    private AgentConfig assembleAgentConfig(Long agentId, Integer returnSchemeId, Integer adminCostId, Integer feeId, String domain, Integer discount, Integer cost) {
        AgentConfig agentConfig = new AgentConfig();
        agentConfig.setAgentId(agentId);
        agentConfig.setReturnSchemeId(returnSchemeId);
        agentConfig.setAdminCostId(adminCostId);
        agentConfig.setFeeId(feeId);
        agentConfig.setDomain(domain);
        agentConfig.setDiscount(discount);
        agentConfig.setCost(cost);
        return agentConfig;
    }

    @Override
    public String getDetail(RequestContext rc, Long id) {
        User opera = userService.get(rc.getUid());
        if (opera == null)
            throw UserException.ILLEGAL_USER;
        User agentUser = userService.get(id);
        if (agentUser == null)
            throw UserException.ILLEGAL_USER;
        JSONObject result = new JSONObject();
        AgentInfoVo agentVo = userService.getAgentDetail(id);
        assembleAgentDetail(agentVo);
        //todo 代理参数配置名称获取 andy 调用接口
        AgentConfigVo agentConfig = agentConfigService.findByAgentId(id);
        result.put("baseInfo", agentVo);
        result.put("settings", agentConfig);
        //TODO 本期资金状况
        String fundProfile = "{\n" +
                "    \"syncTime\": \"2017-04-18 09:29:33\",\n" +
                "    \"info\": {\n" +
                "        \"members\": 490,\n" +
                "        \"depositMembers\": 410,\n" +
                "        \"depositTotalMoney\": \"29006590\",\n" +
                "        \"withdrawTotalMoney\": \"24500120\",\n" +
                "        \"betTotalMoney\": \"20900067\",\n" +
                "        \"betEffMoney\": \"19007689\",\n" +
                "        \"gains\": \"4908763\"\n" +
                "    }\n" +
                "}";
        result.put("fundProfile", JSONObject.parseObject(fundProfile));
        return result.toJSONString();
    }

    /**
     * @param vo
     * @Doc 组装代理详情
     */
    private void assembleAgentDetail(AgentInfoVo vo) {
        vo.setShowStatus(AccountStatus.parse(vo.getStatus()).desc());
        vo.setRegisterTime(DateUtil.formatDateTime(new Date(Long.parseLong(vo.getRegisterTime())), DateUtil.formatDefaultTimestamp));
        vo.setRegisterIp(IPUtil.intToIp(Integer.parseInt(vo.getRegisterIp())));
        vo.setLastLoginIp(IPUtil.intToIp(Integer.parseInt(vo.getLastLoginIp())));
    }

    @Override
    public String resetPwd(RequestContext rc, Long id, String password) {
        User opera = userService.get(rc.getUid());
        if (opera == null)
            throw UserException.ILLEGAL_USER;
        User agentUser = userService.get(id);
        if (agentUser == null)
            throw UserException.ILLEGAL_USER;
        Login login = loginService.get(id);
        if (login == null)
            throw UserException.ILLEGAL_USER;
        if (!loginService.resetPassword(id, password)) {
            ApiLogger.error("update agent password failed,userId:" + id);
            throw UserException.PASSWORD_RESET_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    @Override
    public String update(RequestContext rc, Long id, String realname, String telephone, String email, String bankCardNo, String bank) {
        User opera = userService.get(rc.getUid());
        if (opera == null)
            throw UserException.ILLEGAL_USER;
        User agentUser = userService.get(id);
        if (agentUser == null)
            throw UserException.ILLEGAL_USER;
        User user = assembleUpdateAgentInfo(id, realname, telephone, email, bankCardNo, bank);
        if (!userService.update(user)) {
            ApiLogger.error("update agent info error,userId:" + id);
            throw UserException.USER_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @return
     * @Doc 组装修改的代理数据
     */
    private User assembleUpdateAgentInfo(Long id, String realname, String telephone, String email, String bankCardNo, String bank) {
        User user = new User();
        user.setUserId(id);
        user.setRealname(realname);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setBankCardNo(bankCardNo);
        user.setBank(bank);
        return user;
    }

    @Override
    public String updateAgentConfig(RequestContext rc, Long agentId, Integer returnScheme, Integer adminCost, Integer feeScheme) {
        User agentUser = userService.get(agentId);
        if (agentUser == null)
            throw UserException.ILLEGAL_USER;

        AgentConfig agentConfig = new AgentConfig(agentId, returnScheme, adminCost, feeScheme);
        if (agentConfigService.update(agentConfig) <= 0) {
            throw UserException.AGENT_CONFIG_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    @Override
    public String agentApply(RequestContext rc, HttpServletRequest request, String account, String password, String realname, String telephone, String email, String bankCardNo) {
        StringBuffer url = request.getRequestURL();
        String resourceUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
        //todo 根据请求的域名获取股东id
        long stockId = 0;
        if (stockId == 0)
            stockId = 105094L;
        User stockUser = userService.get(stockId);
        if (stockUser == null)
            throw UserException.ILLEGAL_USER;
        long ownerId = stockUser.getOwnerId();
        if (accountIdMappingService.getUid(ownerId, account) > 0) {
            throw UserException.USERNAME_EXIST;
        }
        int ip = IPUtil.ipToInt(rc.getIp());
        AgentApply agentApply = assembleAgentApply(account, realname, password, stockId, telephone, email, ReviewStatus.noReview, resourceUrl, ip, System.currentTimeMillis());
        if (agentApplyService.add(agentApply) <= 0) {
            throw UserException.AGENT_APPLY_ADD_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * @param username
     * @param realname
     * @param password
     * @param stockId
     * @param telephone
     * @param email
     * @param status
     * @param resourceUrl
     * @param registerIp
     * @param createTime
     * @return
     * @Doc 组装代理申请信息
     */
    private AgentApply assembleAgentApply(String username, String realname, String password, Long stockId, String telephone, String email,
                                          ReviewStatus status, String resourceUrl, Integer registerIp, Long createTime) {
        AgentApply apply = new AgentApply();
        apply.setUsername(username);
        apply.setRealname(realname);
        apply.setPassword(password);
        apply.setStockId(stockId);
        apply.setTelephone(telephone);
        apply.setEmail(email);
        apply.setStatus(status);
        apply.setResourceUrl(resourceUrl);
        apply.setRegisterIp(registerIp);
        apply.setCreateTime(createTime);
        return apply;
    }

    @Override
    public String agentApplyList(RequestContext rc, String account, Integer status, Integer page, Integer count) {
        List<AgentApplyVo> agentApplyVos = agentApplyService.findByPage(account, status, page, count);
        assembleAgentApplyList(agentApplyVos);
        long totalCount = agentApplyService.getCount(account, status);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("page", page);
        jsonObject.put("count", count);
        jsonObject.put("total", totalCount);
        jsonObject.put("list", agentApplyVos);
        return jsonObject.toJSONString();
    }

    private void assembleAgentApplyList(List<AgentApplyVo> list) {
        for (AgentApplyVo vo : list) {
            vo.setShowStatus(ReviewStatus.parse(vo.getStatus()).desc());
            vo.setRegisterIp(IPUtil.intToIp(Integer.parseInt(vo.getRegisterIp())));
            if (vo.getOperatorTime() != null)
                vo.setOperatorTime(DateUtil.formatDateTime(new Date(Long.parseLong(vo.getOperatorTime())), DateUtil.formatDefaultTimestamp));
        }
    }

    @Override
    public String agentApplyInfo(RequestContext rc, Long applyId) {
        AgentApplyVo baseInfo = agentApplyService.agentReviewInfo(applyId);
        JSONObject result = new JSONObject();
        result.put("baseInfo", baseInfo);
        return result.toJSONString();
    }


    @Override
    public String agentReview(RequestContext rc, Long id, Integer reviewStatus, Long holder, String realname, String telephone, String bankCardNo, String email, Integer returnScheme, Integer adminCost, Integer feeScheme, String[] domain, Integer discount, Integer cost) {
        User opera = userService.get(rc.getUid());
        if (opera == null) {
            throw UserException.ILLEGAL_USER;
        }
        User holderUser = userService.get(holder);
        if (holderUser == null)
            throw UserException.ILLEGAL_USER;
        AgentApply agentApply = agentApplyService.get(id);
        if (agentApply == null) {
            throw UserException.AGENT_APPLY_NOT_EXIST;
        }
        //1、如果原审核状态跟修改的状态一样，返回失败,2、如果修改状态是未审核返回失败,3、如果原状态是拒绝返回失败,4、如果原状态是通过修改的状态是拒绝返回失败
        if (agentApply.getStatus() == ReviewStatus.parse(reviewStatus)
                || reviewStatus == ReviewStatus.noReview.value()
                || agentApply.getStatus() == ReviewStatus.noPass
                || (agentApply.getStatus() == ReviewStatus.noPass && reviewStatus == ReviewStatus.noPass.value())) {
            throw UserException.AGENT_REVIEW_FAIL;
        }
        //1、如果拒绝，修改申请状态，增加审核信息
        if (reviewStatus == ReviewStatus.noPass.value()) {
            if (agentApplyService.updateStatus(id, reviewStatus) <= 0) {
                throw UserException.AGENT_REVIEW_FAIL;
            }
            //代理审核历史记录，可以通过mq处理
            AgentReview agentReview = assembleAgentReview(id, realname, rc.getUid(), opera.getUsername(), ReviewStatus.parse(reviewStatus), System.currentTimeMillis());
            sendAgentReviewMq(agentReview);
        } else if (reviewStatus == ReviewStatus.pass.value()) {//2、通过，修改申请状态，增加审核信息，增加代理信息

            RegisterReq req = assembleRegister(agentApply.getUsername(), agentApply.getPassword());
            if (!checkRegisterAgentParam(req))
                throw UserException.ILLEGAL_PARAMETERS;

            if (accountIdMappingService.getUid(holderUser.getOwnerId(), agentApply.getUsername()) > 0) {
                throw UserException.USERNAME_EXIST;
            }
            if (agentApplyService.updateStatus(id, reviewStatus) <= 0) {
                throw UserException.AGENT_REVIEW_FAIL;
            }
            AgentReview agentReview = assembleAgentReview(id, realname, rc.getUid(), opera.getUsername(), ReviewStatus.parse(reviewStatus), System.currentTimeMillis());
            sendAgentReviewMq(agentReview);

            long userId = uuidService.assignUid();
            //1、添加业主id账号映射消息
            OwnerAccountUser ownerAccountUser = new OwnerAccountUser(holderUser.getOwnerId() + UserContants.SPLIT_LINE + agentApply.getUsername(), userId);
            if (accountIdMappingService.add(ownerAccountUser) <= 0) {
                ApiLogger.error(String.format("add ownerAccountUser failed,ownerId:%d,account:%s,agentId:%d", holderUser.getOwnerId(), agentApply.getUsername(), userId));
                throw UserException.REGISTER_FAIL;
            }

            //2、添加代理登录信息
            Login login = new Login(userId, agentApply.getUsername(), agentApply.getPassword());
            if (loginService.add(login) <= 0) {
                ApiLogger.error("add agent login failed,userId:" + userId);
                throw UserException.REGISTER_FAIL;
            }
            //3、添加代理基础信息
            String generalizeCode = UUIDUtil.getCode();
            User agentUser = assembleAgent(userId, holderUser.getOwnerId(), holderUser.getOwnerName(), realname, agentApply.getUsername(), telephone, email, AccountType.agent, System.currentTimeMillis(), IPUtil.ipToInt(rc.getIp()), generalizeCode, AccountStatus.enable, bankCardNo);
            if (!userService.addAgent(agentUser)) {
                ApiLogger.error("add agent info failed,userId:" + userId);
                throw UserException.REGISTER_FAIL;
            }
            String domainSpit = StringUtils.arrayToStrSplit(domain);
            //mq 处理 4、添加代理配置
            AgentConfig agentConfig = assembleAgentConfig(userId, returnScheme, adminCost, feeScheme, domainSpit, discount, cost);
            sendAgentConfigMq(agentConfig);
            //mq 处理 5、添加业主股东代理id映射信息
            OwnerStockAgentMember ownerStockAgentMember = assembleOwnerStockAgent(holderUser.getOwnerId(), holder, userId);
            sendAllUserIdMapping(ownerStockAgentMember);
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * @param agentConfig
     * @Doc 发送增加代理配置消息
     */
    private void sendAgentConfigMq(AgentConfig agentConfig) {
        producer.send(Topic.AGENT_ADD_SUCCESS, agentConfig.getAgentId() + "", UserContants.CUSTOMER_MQ_TAG.AGENT_CONFIG_ADD.name(), JSONObject.toJSONString(agentConfig));
    }

    /**
     * @param ownerStockAgentMember
     * @Doc 发送所有用户id映射消息
     */
    private void sendAllUserIdMapping(OwnerStockAgentMember ownerStockAgentMember) {
        producer.send(Topic.AGENT_ADD_SUCCESS, ownerStockAgentMember.getOwnerId() + "", UserContants.CUSTOMER_MQ_TAG.USER_ID_MAPPING_ADD.name(), JSONObject.toJSONString(ownerStockAgentMember));
    }

    /**
     * @param review
     * @Doc 发送代理审核历史
     */
    private void sendAgentReviewMq(AgentReview review) {
        producer.send(Topic.AGENT_REVIEW_HISTORY, review.getAgentApplyId() + "", JSONObject.toJSONString(review));

    }


    private AgentReview assembleAgentReview(Long agentApplyId, String agentName, Long operUserId, String operUserName, ReviewStatus status, Long createTime) {
        AgentReview review = new AgentReview();
        review.setAgentApplyId(agentApplyId);
        review.setAgentName(agentName);
        review.setOperUserId(operUserId);
        review.setOperUserName(operUserName);
        review.setStatus(status);
        review.setCreateTime(createTime);
        return review;
    }

    @Override
    public String disable(RequestContext rc, Long agentId, Integer status) {
        User opera = userService.get(rc.getUid());
        if (opera == null)
            throw UserException.ILLEGAL_USER;
        User agentUser = userService.get(agentId);
        if (agentUser == null)
            throw UserException.ILLEGAL_USER;
        if (status == agentUser.getStatus().value())
            throw UserException.USER_STATUS_UPDATE_FAIL;
        if (!userService.disable(agentId, status)) {
            throw UserException.USER_STATUS_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }


    @Override
    public String login(RequestContext rc, String agent, String url, String username, String password, String code) {
        if (!checkLoginReq(username, password)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        //todo 根据请求的url拿取到业主id
        long ownerId = 14L;
        long userId = accountIdMappingService.getUid(ownerId, username);
        if (userId <= 0)
            throw UserException.USERNAME_NOT_EXIST;
        //todo 从redis里面获取ownerId_username下的验证码，如果存在验证码，与code对比，如果相符，进行下一步操作
        String proCode = "";
//        if (!proCode.equals(code)) {
//            throw UserException.PROCODE_ERROR;
//        }
        User loginUser = userService.get(userId);
        if (loginUser == null)
            throw UserException.ILLEGAL_USER;
        if (loginUser.getStatus() == AccountStatus.disable
                || loginUser.getIsDelete() == DeleteStatus.del
                || loginUser.getType() == AccountType.member)
            throw UserException.ILLEGAL_USER;
        Login loginInfo = loginService.get(userId);
        if (loginInfo == null)
            throw UserException.ILLEGAL_USER;
        //todo 此处做加密功能
        if (!loginInfo.getPassword().equals(password)) {
            throw UserException.ILLEDGE_USERNAME_PASSWORD;
        }

        int ip = IPUtil.ipToInt(rc.getIp());
        sendLoginHistory(userId, System.currentTimeMillis(), ip, LoginType.login, agent);
        String token = MauthUtil.createOld(userId, System.currentTimeMillis());
        JSONObject result = new JSONObject();
        result.put("token", token);
        result.put("userId", userId);
        return result.toJSONString();
    }

    @Override
    public String logout(RequestContext rc, String agent, String username) {
        if (username == null || username.length() < 6 || username.length() > 16) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        User user = userService.get(rc.getUid());
        if (user == null)
            throw UserException.ILLEGAL_USER;
        sendLoginHistory(rc.getUid(), System.currentTimeMillis(), IPUtil.ipToInt(rc.getIp()), LoginType.logout, agent);
        return UserContants.EMPTY_STRING;
    }

    private void sendLoginHistory(Long userId, Long createTime, Integer requestIp, LoginType loginType, String platform) {
        JSONObject object = new JSONObject();
        object.put("userId", userId);
        object.put("createTime", createTime);
        object.put("requestIp", requestIp);
        object.put("loginType", loginType.value());
        object.put("platform", platform);
        if (loginType == LoginType.login)
            producer.send(Topic.USER_LOGIN_SUCCESS, userId + "", object.toJSONString());
        if (loginType == LoginType.logout)
            producer.send(Topic.USER_LOGOUT_SUCCESS, userId + "", object.toJSONString());
    }


    private RegisterReq assembleRegister(String usernmae, String password) {
        RegisterReq req = new RegisterReq();
        req.setUsername(usernmae);
        req.setPassword(password);
        return req;
    }

    /**
     * 检查注册请求数据合法性
     *
     * @param req
     * @return
     */
    private boolean checkRegisterAgentParam(RegisterReq req) {
        return Optional.ofNullable(req)
                .filter(request -> request.getUsername() != null && req.getUsername().length() >= 6 && req.getUsername().length() <= 16)
                .filter(request -> request.getPassword() != null && request.getPassword().length() == 32)
                .isPresent();
    }

    private boolean checkLoginReq(String username, String password) {
        if (username.length() >= 6 && username.length() <= 16 && password.length() == 32) {
            return true;
        }
        return false;
    }
}
