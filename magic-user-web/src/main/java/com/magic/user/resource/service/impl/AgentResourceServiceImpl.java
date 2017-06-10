package com.magic.user.resource.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.tools.MauthUtil;
import com.magic.api.commons.model.PageBean;
import com.magic.api.commons.model.SimpleListResult;
import com.magic.api.commons.mq.Producer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.DateUtil;
import com.magic.api.commons.tools.IPUtil;
import com.magic.api.commons.tools.UUIDUtil;
import com.magic.api.commons.utils.StringUtils;
import com.magic.bc.query.service.AgentSchemeService;
import com.magic.config.vo.OwnerDomainVo;
import com.magic.config.vo.OwnerInfo;
import com.magic.passport.enums.LoginStatus;
import com.magic.user.bean.AgentCondition;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.*;
import com.magic.user.enums.*;
import com.magic.user.exception.UserException;
import com.magic.user.po.DownLoadFile;
import com.magic.user.po.RegisterReq;
import com.magic.user.resource.service.AgentResourceService;
import com.magic.user.service.*;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import com.magic.user.util.ExcelUtil;
import com.magic.user.util.PasswordCapture;
import com.magic.user.vo.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 20:10
 */
@Service("agentResourceService")
public class AgentResourceServiceImpl implements AgentResourceService {

    @Resource(name = "agentConfigService")
    private AgentConfigService agentConfigService;
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "loginService")
    private LoginService loginService;
    @Resource(name = "agentApplyService")
    private AgentApplyService agentApplyService;
    @Resource
    private AccountIdMappingService accountIdMappingService;
    @Resource
    private Producer producer;
    @Resource
    private AgentMongoService agentMongoService;
    @Resource
    private DubboOutAssembleServiceImpl dubboOutAssembleService;


    /**
     * {@inheritDoc}
     *
     * @param condition
     * @param page
     * @param count
     * @return
     */
    @Override
    public String findByPage(RequestContext rc, String condition, int page, int count) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        AgentCondition userCondition = AgentCondition.valueOf(condition);
        if (userCondition == null) {
            userCondition = new AgentCondition();
        }
        userCondition.setOwnerId(operaUser.getOwnerId());
        if (!checkAgentCondition(userCondition)) {
            return JSON.toJSONString(assemblePageBean(count, page, 0L, null));
        }
        //1、如果代理账号不为空，直接查询代理信息
        if (StringUtils.isNotBlank(userCondition.getAccount())) {
            long agentId = accountIdMappingService.getUid(operaUser.getOwnerId(), userCondition.getAccount());
            if (agentId <= 0) {
                return JSON.toJSONString(assemblePageBean(count, page, 0L, null));
            }
            //1.2、如果推广码不为空，验证推广码是否正确
            if (StringUtils.isNotBlank(userCondition.getPromotionCode())) {
                User agentUser = userService.get(agentId);
                if (!agentUser.getGeneralizeCode().equals(userCondition.getPromotionCode())) {
                    return JSON.toJSONString(assemblePageBean(count, page, 0L, null));
                }
            }
        } else if (StringUtils.isNotBlank(userCondition.getPromotionCode())) {  //2、如果代理账号为空，推广代码不为空，查询代理
            User agentUser = userService.getUserByCode(userCondition.getPromotionCode());
            if (agentUser == null || agentUser.getOwnerId().longValue() != operaUser.getOwnerId().longValue()) {
                return JSON.toJSONString(assemblePageBean(count, page, 0L, null));
            }
        }

        long totalCount = agentMongoService.getCount(userCondition);
        if (totalCount <= 0) {
            return JSON.toJSONString(assemblePageBean(count, page, 0L, null));
        }
        //3、条件查询mongo中的代理，组装id
        List<AgentConditionVo> agentConditionVoList = agentMongoService.queryByPage(userCondition, page, count);
        //todo 将mongo中查询到的代理列表组装一下，调用其他系统获取代理列表
        List<Long> agentIds = Lists.newArrayList();
        for (AgentConditionVo vo : agentConditionVoList) {
            agentIds.add(vo.getAgentId());
        }
        List<AgentInfoVo> list = assembleAgentList(userService.findAgents(agentIds));
        if (list != null && list.size() > 0) {
            return JSON.toJSONString(assemblePageBean(count, page, totalCount, list));
        }
        return JSON.toJSONString(assemblePageBean(count, page, totalCount, null));
    }


    /**
     * @param users
     * @return
     * @Doc 封装代理列表
     */
    private List<AgentInfoVo> assembleAgentList(List<AgentInfoVo> users) {
        for (AgentInfoVo vo : users) {
            vo.setShowStatus(AccountStatus.parse(vo.getStatus()).desc());
            //todo 会员数量，储值会员数量，存款金额，取款金额，审核人，审核时间
            vo.setMembers(1000);
            vo.setStoreMembers(1000);
            vo.setDepositTotalMoney(1000L);
            vo.setWithdrawTotalMoney(2450L);
            vo.setReviewer("jess");
            vo.setReviewTime("2017-03-01 16:43:22");
            vo.setRegisterTime(DateUtil.formatDateTime(new Date(new Long(vo.getRegisterTime())), DateUtil.formatDefaultTimestamp));
        }
        return users;
    }

    /**
     * @param count
     * @param page
     * @param total
     * @param list
     * @return
     * @Doc 组装PageBean
     */
    private PageBean assemblePageBean(Integer count, Integer page, Long total, List list) {
        PageBean pageBean = new PageBean<>();
        pageBean.setCount(count);
        pageBean.setPage(page);
        pageBean.setTotal(total);
        pageBean.setList(list);
        return pageBean;
    }

    /**
     * @param condition
     * @return
     * @Doc 检查代理的condition条件是否合法
     */
    private boolean checkAgentCondition(AgentCondition condition) {
        Integer status = condition.getStatus();
        if (status != null && AccountStatus.parse(status) == null) {
            return false;
        }
        if (condition.getRegister() != null) {
            if (condition.getRegister().getStart() != null && condition.getRegister().getEnd() != null
                    && condition.getRegister().getStart() > condition.getRegister().getEnd()) {
                return false;
            }
        }
        if (condition.getMembers() != null) {
            if (condition.getMembers().getMin() != null && condition.getMembers().getMax() != null
                    && condition.getMembers().getMin() > condition.getMembers().getMax()) {
                return false;
            }
        }
        if (condition.getDepositMoney() != null) {
            if (condition.getDepositMoney().getMin() != null && condition.getDepositMoney().getMax() != null
                    && condition.getDepositMoney().getMin() > condition.getDepositMoney().getMax()) {
                return false;
            }
        }
        if (condition.getWithdrawMoney() != null) {
            if (condition.getWithdrawMoney().getMin() != null && condition.getWithdrawMoney().getMax() != null
                    && condition.getWithdrawMoney().getMin() > condition.getWithdrawMoney().getMax()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param rc        RequestContext
     * @param condition 查询条件
     * @return
     * @Doc 导出代理列表信息
     */
    public DownLoadFile agentListExport(RequestContext rc, String condition) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        byte[] content = new byte[0];
        String filename = ExcelUtil.assembleFileName(operaUser.getUserId(), ExcelUtil.AGENT_LIST);
        DownLoadFile downLoadFile = new DownLoadFile();
        downLoadFile.setFilename(filename);

        AgentCondition userCondition = AgentCondition.valueOf(condition);
        userCondition.setOwnerId(operaUser.getOwnerId());
        if (!checkAgentCondition(userCondition)) {
            downLoadFile.setContent(content);
            return downLoadFile;
        }
        //1、如果代理账号不为空，直接查询代理信息
        if (StringUtils.isNotBlank(userCondition.getAccount())) {
            long agentId = accountIdMappingService.getUid(operaUser.getOwnerId(), userCondition.getAccount());
            if (agentId <= 0) {
                downLoadFile.setContent(content);
                return downLoadFile;
            }
            //1.2、如果推广码不为空，验证推广码是否正确
            if (StringUtils.isNotBlank(userCondition.getPromotionCode())) {
                User agentUser = userService.get(agentId);
                if (!agentUser.getGeneralizeCode().equals(userCondition.getPromotionCode())) {
                    downLoadFile.setContent(content);
                    return downLoadFile;
                }
            }
        } else if (StringUtils.isNotBlank(userCondition.getPromotionCode())) {  //2、如果代理账号为空，推广代码不为空，查询代理
            User agentUser = userService.getUserByCode(userCondition.getPromotionCode());
            if (agentUser == null || agentUser.getOwnerId() != operaUser.getOwnerId()) {
                downLoadFile.setContent(content);
                return downLoadFile;
            }
        }
        //3、条件查询mongo中的代理，组装id
        List<AgentConditionVo> agentConditionVoList = agentMongoService.queryByPage(userCondition, null, null);
        //todo 将mongo中查询到的代理列表组装一下，调用其他系统获取代理列表
        List<Long> agentIds = Lists.newArrayList();
        for (AgentConditionVo vo : agentConditionVoList) {
            agentIds.add(vo.getAgentId());
        }
        List<AgentInfoVo> list = assembleAgentList(userService.findAgents(agentIds));
        //TODO 查询表数据，生成excel的zip，并返回zip byte[]
        content = ExcelUtil.agentListExport(list, filename);
        downLoadFile.setContent(content);
        return downLoadFile;
    }

    /**
     * {@inheritDoc}
     *
     * @param rc
     * @param request
     * @param holder
     * @param account
     * @param password
     * @param realname
     * @param telephone
     * @param bankCardNo
     * @param email
     * @param returnScheme
     * @param adminCost
     * @param feeScheme
     * @param domain
     * @param discount
     * @param cost
     * @return
     */
    @Override
    public String add(RequestContext rc, HttpServletRequest request, Long holder, String account, String password, String realname, String telephone,
                      String bankCardNo, String bank, String bankDeposit, String email, Integer returnScheme,
                      Integer adminCost, Integer feeScheme, String[] domain, Integer discount, Integer cost) {
        String generalizeCode = UUIDUtil.getCode();
        RegisterReq req = assembleRegister(account, password);
        if (!checkRegisterAgentParam(req)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
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

        long userId = dubboOutAssembleService.assignUid();
        if (userId <= 0) {
            throw UserException.ILLEGAL_USER;
        }
        //1、添加业主id账号映射消息
        OwnerAccountUser ownerAccountUser = new OwnerAccountUser(holderUser.getOwnerId() + UserContants.SPLIT_LINE + account, userId);
        if (accountIdMappingService.add(ownerAccountUser) <= 0) {
            ApiLogger.error(String.format("add ownerAccountUser failed,ownerId:%d,account:%d,agentId:%d", holderUser.getOwnerId(), account, userId));
            throw UserException.REGISTER_FAIL;
        }
        //2、添加代理基础信息
        User agentUser = assembleAgent(userId, holderUser.getOwnerId(), holderUser.getOwnerName(), realname, account, telephone, email, AccountType.agent, System.currentTimeMillis(), IPUtil.ipToInt(rc.getIp()), generalizeCode, AccountStatus.enable, bankCardNo, bank, bankDeposit);
        if (!userService.addAgent(agentUser)) {
            ApiLogger.error("add agent info failed,userId:" + userId);
            throw UserException.REGISTER_FAIL;
        }
        //3、添加代理登录信息
        Login login = new Login(userId, account, PasswordCapture.getSaltPwd(password));
        if (loginService.add(login) <= 0) {
            ApiLogger.error("add agent login failed,userId:" + userId);
            throw UserException.REGISTER_FAIL;
        }
        String domainSpit = StringUtils.arrayToStrSplit(domain);

        //mq 处理 4、添加代理配置
        AgentConfig agentConfig = assembleAgentConfig(userId, returnScheme, adminCost, feeScheme, domainSpit, discount, cost);
        //mq 处理 5、添加业主股东代理id映射信息
        OwnerStockAgentMember ownerStockAgentMember = assembleOwnerStockAgent(holderUser.getOwnerId(), holder, userId);
        //mq 处理 6、将代理基础信息放入mongo
        AgentConditionVo agentConditionVo = assembleAgentConfigVo(userId, account, generalizeCode, holder, agentUser.getOwnerId());
        sendAgentAddSuccessMq(agentConfig, ownerStockAgentMember, agentConditionVo);

        JSONObject result = new JSONObject();
        result.put("id", userId);
        return result.toJSONString();
    }


    /**
     * @param userId
     * @param ownerId
     * @param ownerName
     * @param realname
     * @param username
     * @param telephone
     * @param email
     * @param type
     * @param registerTime
     * @param registerIp
     * @param generalizeCode
     * @param status
     * @param bankCardNo
     * @return
     * @Doc 组装添加的代理对象
     */
    private User assembleAgent(Long userId, long ownerId, String ownerName, String realname, String username, String telephone, String email, AccountType type, Long registerTime,
                               Integer registerIp, String generalizeCode, AccountStatus status, String bankCardNo, String bank, String bankDeposit) {
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
        user.setBank(bank);
        user.setBankDeposit(bankDeposit);
        return user;

    }

    private AgentConditionVo assembleAgentConfigVo(Long agentId, String agentName, String generalizeCode, Long stockId, Long ownerId) {
        AgentConditionVo vo = new AgentConditionVo();
        vo.setAgentId(agentId);
        vo.setAgentName(agentName);
        vo.setStatus(AccountStatus.enable.value());
        vo.setDepositMoney(0L);
        vo.setWithdrawMoney(0L);
        vo.setMembers(0);
        vo.setGeneralizeCode(generalizeCode);
        vo.setStockId(stockId);
        vo.setOwnerId(ownerId);
        return vo;
    }


    /**
     * @param ownerId
     * @param stockId
     * @param agentId
     * @return
     * @Doc 组装添加的业主股东代理映射对象
     */
    private OwnerStockAgentMember assembleOwnerStockAgent(Long ownerId, Long stockId, Long agentId) {
        OwnerStockAgentMember ownerStockAgentMember = new OwnerStockAgentMember();
        ownerStockAgentMember.setOwnerId(ownerId);
        ownerStockAgentMember.setStockId(stockId);
        ownerStockAgentMember.setAgentId(agentId);
        ownerStockAgentMember.setMemNumber(0);
        return ownerStockAgentMember;
    }

    /**
     * @param agentId
     * @param returnSchemeId
     * @param adminCostId
     * @param feeId
     * @param domain
     * @param discount
     * @param cost
     * @return
     * @Doc 组装添加的代理配置对象
     */
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

    /**
     * {@inheritDoc}
     *
     * @param rc
     * @param id
     * @return
     */
    @Override
    public String getDetail(RequestContext rc, Long id) {
        User opera = userService.get(rc.getUid());
        if (opera == null) {
            throw UserException.ILLEGAL_USER;
        }
        User agentUser = userService.get(id);
        if (agentUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        JSONObject result = getAgentInfoVo(id, false);
        return result.toJSONString();
    }

    /**
     * 获取代理详情
     *
     * @param id
     * @param isReview 是否是审核通过的信息
     * @return
     */
    private JSONObject getAgentInfoVo(Long id, boolean isReview) {
        JSONObject result = new JSONObject();
        AgentInfoVo agentVo = userService.getAgentDetail(id);
        if (agentVo == null) {
            throw UserException.ILLEGAL_USER;
        }
        assembleAgentDetail(agentVo, isReview);
        //todo 代理参数配置名称获取 andy 调用接口
        AgentConfigVo agentConfig = agentConfigService.findByAgentId(id);
        assembleAgentConfigVo(agentConfig);
        result.put("baseInfo", agentVo);
        result.put("settings", agentConfig);
        if (!isReview) {
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
        }
        return result;
    }

    /**
     * 组装代理配置信息
     *
     * @param vo
     */
    private void assembleAgentConfigVo(AgentConfigVo vo) {
        //todo 代理参数配置名称通过jason thrift调用
        vo.setReturnSchemeName("退佣方案1");
        vo.setAdminCostName("行政成本1");
        vo.setFeeSchemeName("手续费1");
    }

    @Override
    public String getDomain(RequestContext rc, Long agentId) {
        List<String> domains = agentConfigService.getAgentDomain(agentId);
        SimpleListResult simpleListResult = new SimpleListResult<>();
        simpleListResult.setList(domains);
        return JSON.toJSONString(simpleListResult);
    }

    /**
     * @param vo
     * @Doc 组装代理详情
     */
    private void assembleAgentDetail(AgentInfoVo vo, boolean isReview) {
        vo.setType(AccountType.agent.value());
        vo.setShowStatus(AccountStatus.parse(vo.getStatus()).desc());
        vo.setRegisterTime(DateUtil.formatDateTime(new Date(Long.parseLong(vo.getRegisterTime())), DateUtil.formatDefaultTimestamp));
        vo.setRegisterIp(IPUtil.intToIp(Integer.parseInt(vo.getRegisterIp())));
        vo.setLastLoginIp(IPUtil.intToIp(Integer.parseInt(vo.getLastLoginIp())));
        if (!isReview) {
            if (StringUtils.isNotBlank(vo.getDomain())) {
                String[] domains = vo.getDomain().split(",");
                vo.setDomain(domains[0]);
            }
        } else {
            if (StringUtils.isNotBlank(vo.getDomain())) {
                String[] domains = vo.getDomain().split(",");
                vo.setDomains(domains);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param rc
     * @param id
     * @param password
     * @return
     */
    @Override
    public String resetPwd(RequestContext rc, Long id, String password) {
        User opera = userService.get(rc.getUid());
        if (opera == null) {
            throw UserException.ILLEGAL_USER;
        }
        User agentUser = userService.get(id);
        if (agentUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        Login login = loginService.get(id);
        if (login == null) {
            throw UserException.ILLEGAL_USER;
        }
        if (!loginService.resetPassword(id, PasswordCapture.getSaltPwd(password))) {
            ApiLogger.error("update agent password failed,userId:" + id);
            throw UserException.PASSWORD_RESET_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     *
     * @param rc
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @return
     */
    @Override
    public String update(RequestContext rc, Long id, String realname, String telephone, String email, String bankCardNo, String bank) {
        User opera = userService.get(rc.getUid());
        if (opera == null) {
            throw UserException.ILLEGAL_USER;
        }
        User agentUser = userService.get(id);
        if (agentUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        User updateUser = userService.get(id);
        assembleUpdateAgentInfo(updateUser, realname, telephone, email, bankCardNo, bank);
        if (!userService.update(updateUser)) {
            ApiLogger.error("update agent info error,userId:" + id);
            throw UserException.USER_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @return
     * @Doc 组装修改的代理数据
     */
    private void assembleUpdateAgentInfo(User user, String realname, String telephone, String email, String bankCardNo, String bank) {
        user.setRealname(realname);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setBankCardNo(bankCardNo);
        user.setBank(bank);
    }

    /**
     * {@inheritDoc}
     *
     * @param rc
     * @param agentId
     * @param returnScheme
     * @param adminCost
     * @param feeScheme
     * @return
     */
    @Override
    public String updateAgentConfig(RequestContext rc, Long agentId, Integer returnScheme, Integer adminCost, Integer feeScheme) {
        User agentUser = userService.get(agentId);
        if (agentUser == null) {
            throw UserException.ILLEGAL_USER;
        }

        AgentConfig agentConfig = new AgentConfig(agentId, returnScheme, adminCost, feeScheme);
        if (!agentConfigService.update(agentConfig)) {
            throw UserException.AGENT_CONFIG_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    //TODO 流程有待确认
    //1.url获取ownerId
    //2.ownerId + ownername 获取 stockId
    @Override
    public String agentApply(RequestContext rc, HttpServletRequest request, String account, String password, String paymentPassword, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit, String province, String city, String weixin, String qq) {
        StringBuffer url = request.getRequestURL();
        String resourceUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
        OwnerInfo ownerInfo = dubboOutAssembleService.getOwnerInfoByDomain(resourceUrl);
        if (ownerInfo == null || ownerInfo.getOwnerId() < 0) {
            throw UserException.ILLEGAL_SOURCE_URL;
        }

        //校验用户名、密码、支付密码的格式及其他非空参数
        if (!checkRegisterParam(account,password,paymentPassword,ownerInfo.getOwnerId(),AccountType.agent.value(),email,province,city,weixin,qq)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }

        long stockId = accountIdMappingService.getUid(ownerInfo.getOwnerId(), ownerInfo.getOwnerName());
        User stockUser = userService.get(stockId);
        if (stockUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        if (accountIdMappingService.getUid(stockUser.getOwnerId(), account) > 0) {
            throw UserException.USERNAME_EXIST;
        }
        int ip = IPUtil.ipToInt(rc.getIp());
        AgentApply agentApply = assembleAgentApply(account, realname, PasswordCapture.getSaltPwd(password), stockId,
                stockUser.getUsername(), ownerInfo.getOwnerId(), telephone, email, ReviewStatus.noReview, resourceUrl, ip,
                System.currentTimeMillis(), bankCardNo, bank, bankDeposit,province,city,weixin,qq);
        if (agentApplyService.add(agentApply) <= 0) {
            throw UserException.AGENT_APPLY_ADD_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * 校验用户名、密码、支付密码的格式及其他非空参数
     *
     * @param account
     * @param password
     * @param paymentPassword
     * @param ownerId
     * @param type
     * @param email
     * @param province
     * @param city
     * @param weixin
     * @param qq
     * @return
     */
    private boolean checkRegisterParam(String account, String password, String paymentPassword, long ownerId,int type,String email,String province,String city,String weixin,String qq) {
        //校验用户名和密码
        if(account.length() < 6 || account.length() > 16 || password.length() != 32){
            return false;
        }
        //校验其他注册参数
        List<String> list = dubboOutAssembleService.getMustRegisterarameters(ownerId,type);
        if(list != null && list.size() > 0){
            if(list.contains("email")){
                if(!StringUtils.isNotEmpty(email)){
                    return false;
                }
            }
            if(list.contains("province")){
                if(!StringUtils.isNotEmpty(province)){
                    return false;
                }
            }
            if(list.contains("city")){
                if(!StringUtils.isNotEmpty(city)){
                    return false;
                }
            }
            if(list.contains("weixin")){
                if(!StringUtils.isNotEmpty(weixin)){
                    return false;
                }
            }
            if(list.contains("qq")){
                if(!StringUtils.isNotEmpty(qq)){
                    return false;
                }
            }
        }
        return true;
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
    private AgentApply assembleAgentApply(String username, String realname, String password, Long stockId, String stockName, Long ownerId, String telephone, String email,
                                          ReviewStatus status, String resourceUrl, Integer registerIp, Long createTime,
                                          String bankCardNo, String bank, String bankDeposit,String province, String city, String weixin, String qq) {
        AgentApply apply = new AgentApply();
        apply.setUsername(username);
        apply.setRealname(realname);
        apply.setPassword(password);
        apply.setStockId(stockId);
        apply.setStockName(stockName);
        apply.setOwnerId(ownerId);
        apply.setTelephone(telephone);
        apply.setEmail(email);
        apply.setStatus(status);
        apply.setResourceUrl(resourceUrl);
        apply.setRegisterIp(registerIp);
        apply.setCreateTime(createTime);
        apply.setBankCardNo(bankCardNo);
        apply.setBank(bank);
        apply.setBankDeposit(bankDeposit);
        apply.setProvince(province);
        apply.setCity(city);
        apply.setWeixin(weixin);
        apply.setQq(qq);
        return apply;
    }

    /**
     * {@inheritDoc}
     *
     * @param rc
     * @param account
     * @param status
     * @param page
     * @param count
     * @return
     */
    @Override
    public String agentApplyList(RequestContext rc, String account, Integer status, Integer page, Integer count) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        long totalCount = agentApplyService.getCount(operaUser.getOwnerId(), account, status);
        if (totalCount <= 0) {
            return JSON.toJSONString(assemblePageBean(count, page, 0L, null));
        }
        List<AgentApplyVo> agentApplyVos = agentApplyService.findByPage(operaUser.getOwnerId(), account, status, page, count);
        assembleAgentApplyList(agentApplyVos);
        return JSON.toJSONString(assemblePageBean(count, page, totalCount, agentApplyVos));
    }

    /**
     * @param rc
     * @param account
     * @param status
     * @return
     * @Doc 导出代理审核列表
     */
    @Override
    public DownLoadFile reviewListExport(RequestContext rc, String account, Integer status) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }

        byte[] content = new byte[0];
        String filename = ExcelUtil.assembleFileName(operaUser.getUserId(), ExcelUtil.AGENT_REVIEW_LIST);
        DownLoadFile downLoadFile = new DownLoadFile();
        downLoadFile.setFilename(filename);
        List<AgentApplyVo> agentApplyVos = agentApplyService.findByPage(operaUser.getOwnerId(), account, status, null, null);
        if (agentApplyVos == null || agentApplyVos.size() <= 0) {
            downLoadFile.setContent(content);
            return downLoadFile;
        }
        assembleAgentApplyList(agentApplyVos);
        //TODO 查询表数据，生成excel的zip，并返回zip byte[]
        content = ExcelUtil.agentReviewListExport(agentApplyVos, filename);
        downLoadFile.setContent(content);
        return downLoadFile;

    }

    /**
     * @param list
     * @Doc 组装代理申请列表数据
     */
    private void assembleAgentApplyList(List<AgentApplyVo> list) {
        for (AgentApplyVo vo : list) {
            vo.setShowStatus(ReviewStatus.parse(vo.getStatus()).desc());
            vo.setRegisterIp(IPUtil.intToIp(Integer.parseInt(vo.getRegisterIp())));
            if (vo.getOperatorTime() != null) {
                vo.setOperatorTime(DateUtil.formatDateTime(new Date(Long.parseLong(vo.getOperatorTime())), DateUtil.formatDefaultTimestamp));
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param rc
     * @param applyId
     * @return
     */
    @Override
    public String agentApplyInfo(RequestContext rc, Long applyId) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        AgentApplyVo baseInfo = agentApplyService.agentReviewInfo(applyId);
        JSONObject result = new JSONObject();
        result.put("baseInfo", baseInfo);
        Map<String, Object> map = dubboOutAssembleService.agentSchemeList(operaUser.getOwnerId());
        result.put("agentConfig", map);
        return result.toJSONString();
    }

    @Override
    public String reviewDetail(RequestContext rc, Long applyId) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        AgentApplyVo baseInfo = agentApplyService.agentReviewInfo(applyId);
        if (baseInfo != null && (baseInfo.getStatus() == ReviewStatus.noPass.value() || baseInfo.getStatus() == ReviewStatus.noReview.value())) {
            JSONObject result = new JSONObject();
            result.put("baseInfo", baseInfo);
            return result.toJSONString();
        } else if (baseInfo.getStatus() == ReviewStatus.pass.value()) {
            long agentId = accountIdMappingService.getUid(operaUser.getOwnerId(), baseInfo.getAccount());
            if (agentId > 0) {
                JSONObject jsonObject = getAgentInfoVo(agentId, true);
                return jsonObject.toJSONString();
            }
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     *
     * @param rc
     * @param id
     * @param reviewStatus
     * @param holder
     * @param realname
     * @param telephone
     * @param bankCardNo
     * @param email
     * @param returnScheme
     * @param adminCost
     * @param feeScheme
     * @param domain
     * @param discount
     * @param cost
     * @return
     */
    @Override
    public String agentReview(RequestContext rc, Long id, Integer reviewStatus, Long holder, String realname, String telephone, String bankCardNo, String bank, String bankDeposit, String email, Integer returnScheme, Integer adminCost, Integer feeScheme, String domain, Integer discount, Integer cost) {
        User opera = userService.get(rc.getUid());
        if (opera == null) {
            throw UserException.ILLEGAL_USER;
        }

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
            AgentReview agentReview = assembleAgentReview(id, realname, rc.getUid(), opera.getUsername(), opera.getOwnerId(), ReviewStatus.parse(reviewStatus), System.currentTimeMillis());
            sendAgentReviewMq(agentReview);
        } else if (reviewStatus == ReviewStatus.pass.value()) {//2、通过，修改申请状态，增加审核信息，增加代理信息
            //TODO 校验是参数
            User holderUser = userService.get(holder);
            if (holderUser == null) {
                throw UserException.ILLEGAL_USER;
            }
            RegisterReq req = assembleRegister(agentApply.getUsername(), agentApply.getPassword());
            if (!checkRegisterAgentParam(req)) {
                throw UserException.ILLEGAL_PARAMETERS;
            }
            if (accountIdMappingService.getUid(holderUser.getOwnerId(), agentApply.getUsername()) > 0) {
                throw UserException.USERNAME_EXIST;
            }
            if (agentApplyService.updateStatus(id, reviewStatus) <= 0) {
                throw UserException.AGENT_REVIEW_FAIL;
            }
            AgentReview agentReview = assembleAgentReview(id, realname, rc.getUid(), opera.getUsername(), opera.getOwnerId(), ReviewStatus.parse(reviewStatus), System.currentTimeMillis());
            sendAgentReviewMq(agentReview);

            long userId = dubboOutAssembleService.assignUid();
            if (userId <= 0) {
                throw UserException.ILLEGAL_USER;
            }
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
            User agentUser = assembleAgent(userId, holderUser.getOwnerId(), holderUser.getOwnerName(), realname, agentApply.getUsername(), telephone, email, AccountType.agent, System.currentTimeMillis(), IPUtil.ipToInt(rc.getIp()), generalizeCode, AccountStatus.enable, bankCardNo, bank, bankDeposit);
            if (!userService.addAgent(agentUser)) {
                ApiLogger.error("add agent info failed,userId:" + userId);
                throw UserException.REGISTER_FAIL;
            }
            //mq 处理 4、添加代理配置
            AgentConfig agentConfig = assembleAgentConfig(userId, returnScheme, adminCost, feeScheme, domain, discount, cost);
            //mq 处理 5、添加业主股东代理id映射信息
            OwnerStockAgentMember ownerStockAgentMember = assembleOwnerStockAgent(holderUser.getOwnerId(), holder, userId);
            //mq 处理 6、将代理基础信息放入mongo
            AgentConditionVo agentConditionVo = assembleAgentConfigVo(userId, agentUser.getUsername(), generalizeCode, holder, agentUser.getOwnerId());
            sendAgentAddSuccessMq(agentConfig, ownerStockAgentMember, agentConditionVo);
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * @param agentConfig
     * @param ownerStockAgentMember
     * @Doc 代理添加成功发送mq消息
     * 1、代理配置信息
     * 2、业主股东代理映射数据
     */
    private void sendAgentAddSuccessMq(AgentConfig agentConfig, OwnerStockAgentMember ownerStockAgentMember, AgentConditionVo agentConditionVo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(UserContants.CUSTOMER_MQ_TAG.AGENT_CONFIG_ADD.name(), agentConfig);
        jsonObject.put(UserContants.CUSTOMER_MQ_TAG.USER_ID_MAPPING_ADD.name(), ownerStockAgentMember);
        jsonObject.put(UserContants.CUSTOMER_MQ_TAG.AGENT_CONDITION_ADD.name(), agentConditionVo);
        producer.send(Topic.AGENT_ADD_SUCCESS, agentConfig.getAgentId() + "", jsonObject.toJSONString());
    }

    /**
     * @param review
     * @Doc 发送代理审核历史
     */
    private void sendAgentReviewMq(AgentReview review) {
        producer.send(Topic.AGENT_REVIEW_HISTORY, review.getAgentApplyId() + "", JSONObject.toJSONString(review));

    }


    /**
     * @param agentApplyId
     * @param agentName
     * @param operUserId
     * @param operUserName
     * @param status
     * @param createTime
     * @return
     * @Doc 组装代理审核对象
     */
    private AgentReview assembleAgentReview(Long agentApplyId, String agentName, Long operUserId, String operUserName, Long ownerId, ReviewStatus status, Long createTime) {
        AgentReview review = new AgentReview();
        review.setAgentApplyId(agentApplyId);
        review.setAgentName(agentName);
        review.setOperUserId(operUserId);
        review.setOperUserName(operUserName);
        review.setOwnerId(ownerId);
        review.setStatus(status);
        review.setCreateTime(createTime);
        return review;
    }

    /**
     * {@inheritDoc}
     *
     * @param rc
     * @param agentId
     * @param status
     * @return
     */
    @Override
    public String updateStatus(RequestContext rc, Long agentId, Integer status) {
        User opera = userService.get(rc.getUid());
        if (opera == null) {
            throw UserException.ILLEGAL_USER;
        }
        User agentUser = userService.get(agentId);
        if (agentUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        if (status == agentUser.getStatus().value()) {
            throw UserException.USER_STATUS_UPDATE_FAIL;
        }
        agentUser.setStatus(AccountStatus.parse(status));
        if (!userService.disable(agentUser)) {
            throw UserException.USER_STATUS_UPDATE_FAIL;
        }
        sendAgentStatusUpdateMq(agentId, status);
        return UserContants.EMPTY_STRING;
    }

    /**
     * @param id
     * @param status
     * @Doc 发送代理状态修改
     */
    private void sendAgentStatusUpdateMq(Long id, Integer status) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("agentId", id);
        jsonObject.put("status", status);
        producer.send(Topic.AGENT_STATUS_UPDATE_SUCCESS, id + "", jsonObject.toJSONString());
    }

    @Override
    public String allConfigs(RequestContext rc) {
        User user = userService.get(rc.getUid());
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        JSONObject result = new JSONObject();
        List<StockInfoVo> list = userService.findAllStock(user.getOwnerId());
        list = assembleSimple(list);
        result.put("stocks", list);
        Map<String, Object> map = dubboOutAssembleService.agentSchemeList(user.getOwnerId());
        result.put("agentConfig", map);
        List<OwnerDomainVo> allDomains = dubboOutAssembleService.queryAllDomainList(user.getOwnerId());
        result.put("domains", allDomains);
        return result.toJSONString();
    }

    /**
     * @param list
     * @return
     * @Doc 组装股东名称列表
     */
    private List<StockInfoVo> assembleSimple(List<StockInfoVo> list) {
        List<StockInfoVo> infos = new ArrayList<>();
        for (StockInfoVo info : list) {
            StockInfoVo info1 = new StockInfoVo();
            info1.setId(info.getId());
            info1.setAccount(info.getAccount());
            infos.add(info1);
        }
        return infos;
    }

    /**
     * @param usernmae
     * @param password
     * @return
     * @Doc 组装注册需要验证的对象
     */
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

}
