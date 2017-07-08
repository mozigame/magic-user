package com.magic.user.resource.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.PageBean;
import com.magic.api.commons.model.SimpleListResult;
import com.magic.api.commons.mq.Producer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.*;
import com.magic.api.commons.utils.StringUtils;
import com.magic.config.thrift.base.EGResp;
import com.magic.config.vo.OwnerDomainVo;
import com.magic.config.vo.OwnerInfo;
import com.magic.oceanus.entity.Summary.ProxyCurrentOperaton;
import com.magic.oceanus.service.OceanusProviderDubboService;
import com.magic.user.bean.AgentCondition;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.*;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.AccountType;
import com.magic.user.enums.ReviewStatus;
import com.magic.user.exception.UserException;
import com.magic.user.po.DownLoadFile;
import com.magic.user.po.RegisterReq;
import com.magic.user.resource.service.AgentResourceService;
import com.magic.user.service.*;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import com.magic.user.service.thrift.ThriftOutAssembleServiceImpl;
import com.magic.user.util.ExcelUtil;
import com.magic.user.util.PasswordCapture;
import com.magic.user.util.UserUtil;
import com.magic.user.vo.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    @Resource
    private ThriftOutAssembleServiceImpl thriftOutAssembleService;
    @Resource
    private OceanusProviderDubboService oceanusProviderDubboService;
    @Resource
    private OwnerStockAgentService ownerStockAgentService;
    @Resource
    private MemberMongoService memberMongoService;
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
        long totalCount = agentMongoService.getCount(userCondition);
        if (totalCount <= 0) {
            return JSON.toJSONString(assemblePageBean(count, page, 0L, null));
        }
        //3、条件查询mongo中的代理，组装id
        List<AgentConditionVo> agentConditionVoList = agentMongoService.queryByPage(userCondition, page, count);
        if(agentConditionVoList == null) return JSON.toJSONString(assemblePageBean(count, page, 0L, null));

        //将mongo中查询到的代理列表组装一下，调用其他系统获取代理列表
        List<Long> agentIds = Lists.newArrayList();

        Map<Long,AgentConditionVo> map = new HashMap<Long,AgentConditionVo>();
        for (AgentConditionVo vo : agentConditionVoList) {
            agentIds.add(vo.getAgentId());
            vo.setDepositMoney(NumberUtil.fenToYuan(vo.getDepositMoney()).longValue());
            vo.setWithdrawMoney(NumberUtil.fenToYuan(vo.getWithdrawMoney()).longValue());
            map.put(vo.getAgentId(),vo);

        }

        Map<Long,Integer> listm = memberMongoService.countDepositMembers(agentIds);
        if(listm == null){
            listm = new HashMap<>();
        }
        //根据代理ID列表查询代理的会员数量信息
        List<OwnerStockAgentMember> OwnerStockAgentMemberList = ownerStockAgentService.countMembersByIds(agentIds,AccountType.agent);

        Map<Long,OwnerStockAgentMember> osamMap = new HashMap<>();

        if(OwnerStockAgentMemberList != null){
            for (OwnerStockAgentMember osam:OwnerStockAgentMemberList) {
                osamMap.put(osam.getAgentId(),osam);
            }
        }

        List<AgentInfoVo> users = userService.findAgents(agentIds);
        if(users == null)return JSON.toJSONString(assemblePageBean(count, page, 0L, null));
        List<AgentInfoVo> list = assembleAgentList(users,map,osamMap,listm);
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
    private List<AgentInfoVo> assembleAgentList(List<AgentInfoVo> users,Map<Long,AgentConditionVo> map,Map<Long,OwnerStockAgentMember> osamMap,Map<Long,Integer> listm) {
        for (AgentInfoVo vo : users) {
            AgentConditionVo av = map.get(vo.getId());
            OwnerStockAgentMember osam = osamMap.get(vo.getId());
            Integer v = listm.get(vo.getId());
            if(av != null){
                vo.setShowStatus(AccountStatus.parse(vo.getStatus()).desc());
                // 会员数量，存款金额，取款金额
                if (osam != null) {
                    vo.setMembers(osam.getMemNumber());
                }else{
                    vo.setMembers(0);
                }
                vo.setDepositTotalMoney(av.getDepositMoney());
                vo.setWithdrawTotalMoney(av.getWithdrawMoney());
            }else{
                vo.setShowStatus("");
                // 会员数量，存款金额，取款金额
                vo.setMembers(0);
                vo.setDepositTotalMoney(0L);
                vo.setWithdrawTotalMoney(0L);
            }
            if(vo.getReviewTime() != null){
                vo.setReviewTime(LocalDateTimeUtil.toAmerica(new Long(vo.getReviewTime())));
            }else{
                vo.setReviewTime("");
            }
            if(v != null){
                vo.setStoreMembers(v);
            }else{
                vo.setStoreMembers(0);
            }
            if(vo.getReviewer() == null){
                vo.setReviewer("");
            }
            vo.setRegisterTime(LocalDateTimeUtil.toAmerica(new Long(vo.getRegisterTime())));
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
        //3、条件查询mongo中的代理，组装id
        List<AgentConditionVo> agentConditionVoList = agentMongoService.queryByPage(userCondition, null, null);
        if (!Optional.ofNullable(agentConditionVoList).isPresent()){
            downLoadFile.setContent(content);
            return downLoadFile;
        }
        //todo 将mongo中查询到的代理列表组装一下，调用其他系统获取代理列表
        List<Long> agentIds = Lists.newArrayList();
        Map<Long,AgentConditionVo> map = new HashMap<Long,AgentConditionVo>();
        for (AgentConditionVo vo : agentConditionVoList) {
            agentIds.add(vo.getAgentId());
            map.put(vo.getAgentId(),vo);
        }

        //根据代理ID列表查询代理的会员数量信息
        List<OwnerStockAgentMember> OwnerStockAgentMemberList = ownerStockAgentService.countMembersByIds(agentIds,AccountType.agent);
        Map<Long,OwnerStockAgentMember> osamMap = new HashMap<Long,OwnerStockAgentMember>();
        for (OwnerStockAgentMember osam:OwnerStockAgentMemberList) {
            osamMap.put(osam.getAgentId(),osam);
        }

        Map<Long,Integer> listm = memberMongoService.countDepositMembers(agentIds);
        if(listm == null) listm = new HashMap<Long,Integer>();
        List<AgentInfoVo> list = assembleAgentList(userService.findAgents(agentIds),map,osamMap,listm);
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
        } else {
            sendAddUserSuccess(agentUser);
        }
        //3、添加代理登录信息
        Login login = new Login(userId, account, PasswordCapture.getSaltPwd(password));
        if (loginService.add(login) <= 0) {
            ApiLogger.error("add agent login failed,userId:" + userId);
            throw UserException.REGISTER_FAIL;
        }
        String domainSpit = StringUtils.arrayToStrSplit(domain);

        //mq 处理 4、添加代理配置
        AgentConfig agentConfig = assembleAgentConfig(opera.getOwnerId(), userId, returnScheme, adminCost, feeScheme, domainSpit, discount, cost);
        //mq 处理 5、添加业主股东代理id映射信息
        OwnerStockAgentMember ownerStockAgentMember = assembleOwnerStockAgent(holderUser.getOwnerId(), holder, userId);
        //mq 处理 6、将代理基础信息放入mongo
        AgentConditionVo agentConditionVo = assembleAgentConfigVo(userId, account, generalizeCode, holder, agentUser.getOwnerId());
        sendAgentAddSuccessMq(agentConfig, ownerStockAgentMember, agentConditionVo);
        /*用户添加成功发送mq*/
        sendAddUserSuccess(agentUser);
        JSONObject result = new JSONObject();
        result.put("id", userId);
        return result.toJSONString();
    }

    /**
     * 用户添加成功发送mq
     * @param agentUser
     * @return
     */
    private boolean sendAddUserSuccess(User agentUser) {
        try {
            return producer.send(Topic.MAGIC_OWNER_USER_ADD_SUCCESS, agentUser.getUserId() + "", JSON.toJSONString(agentUser));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        vo.setRegisterTime(System.currentTimeMillis());
        vo.setUpdateTime(vo.getRegisterTime());
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
    private AgentConfig assembleAgentConfig(long ownerId,Long agentId, Integer returnSchemeId, Integer adminCostId, Integer feeId, String domain, Integer discount, Integer cost) {
        AgentConfig agentConfig = new AgentConfig();
        agentConfig.setOwnerId(ownerId);
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
        return getAgentInfoVo(id,opera.getOwnerId(), false);
    }

    /**
     * 获取代理详情
     * @param agentId
     * @param ownerId
     * @param isReview 是否是审核通过的信息
     * @return
     */
    private String getAgentInfoVo(Long agentId,Long ownerId, boolean isReview) {
        AgentInfoVo agentVo = userService.getAgentDetail(agentId);
        if (agentVo == null) {
            throw UserException.ILLEGAL_USER;
        }
        assembleAgentDetail(agentVo, isReview);
        AgentDetailVo agentDetailVo = new AgentDetailVo();

        agentDetailVo.setBaseInfo(agentVo);
        AgentConfigVo setting = thriftOutAssembleService.getAgentConfig(agentId);
        setting = initAgentConfigVo(setting);

        agentDetailVo.setSettings(setting);

        if (!isReview) {
            ProxyCurrentOperaton p = dubboOutAssembleService.getProxyOperation(agentVo.getId(),ownerId);
            FundProfile<AgentFundInfo> profile = new FundProfile<>();
            profile.setSyncTime(LocalDateTimeUtil.toAmerica(System.currentTimeMillis()));
            AgentFundInfo info = assembleFundProfile(p);
            long depositMembers =  memberMongoService.getDepositMembers(agentVo.getId());
            info.setDepositMembers((int)depositMembers);
            OwnerStockAgentMember osam = ownerStockAgentService.countMembersById(agentVo.getId(),AccountType.agent);
            if(osam != null){
                info.setMembers(osam.getMemNumber());
            }else{
                info.setMembers(0);
            }
            profile.setInfo(info);

            agentDetailVo.setFundProfile(profile);
        }
        return JSON.toJSONString(agentDetailVo);
    }


    /**
     * 初始化setting
     * @param setting
     * @return
     */
    private AgentConfigVo initAgentConfigVo(AgentConfigVo setting) {
        if(setting == null){
            setting = new AgentConfigVo();
        }
        if(setting.getAdminCost() == null) setting.setAdminCost(0);
        if(setting.getAdminCostName() == null) setting.setAdminCostName("");
        if(setting.getCost() == null) setting.setCost(0);
        if(setting.getDiscount() == null) setting.setDiscount(0);
        if (setting.getFeeScheme() == null) setting.setFeeScheme(0);
        if(setting.getFeeSchemeName() == null) setting.setFeeSchemeName("");
        if(setting.getReturnScheme() == null) setting.setReturnScheme(0);
        if(setting.getReturnSchemeName() == null)setting.setReturnSchemeName("");

        return setting;
    }

    /**
     * 组装数据
     *
     * @param operaton
     * @return
     */
    private AgentFundInfo assembleFundProfile(ProxyCurrentOperaton operaton) {
        AgentFundInfo agentFundInfo = new AgentFundInfo();
        int members = 0;//会员数量
        //int depositMembers = 0;//存款会员数量
        String depositTotalMoney = "0";//存款金额
        String withdrawTotalMoney = "0";//取款金额
        String betTotalMoney = "0";//总投注额
        String betEffMoney = "0";//总有效投注额
        String gains = "0";//损益
        if (Optional.ofNullable(operaton).filter(membersValue -> membersValue.getMembers() != null && membersValue.getMembers() > 0).isPresent()){
            members = operaton.getMembers().intValue();
        }
//        if (Optional.ofNullable(operaton).filter(depositMembersValue -> depositMembersValue.getDepositMembers() != null && depositMembersValue.getDepositMembers() > 0).isPresent()){
//            depositMembers = operaton.getDepositMembers().intValue();
//        }
        if (Optional.ofNullable(operaton).filter(depositTotalMoneyValue -> depositTotalMoneyValue.getDepositTotalMoney() != null && depositTotalMoneyValue.getDepositTotalMoney() > 0).isPresent()){
            depositTotalMoney = String.valueOf(NumberUtil.fenToYuan(operaton.getDepositTotalMoney()));
        }
        if (Optional.ofNullable(operaton).filter(withdrawTotalMoneyValue -> withdrawTotalMoneyValue.getWithdrawTotalMoney() != null && withdrawTotalMoneyValue.getWithdrawTotalMoney() > 0).isPresent()){
            withdrawTotalMoney = String.valueOf(NumberUtil.fenToYuan(operaton.getWithdrawTotalMoney()));
        }
        if (Optional.ofNullable(operaton).filter(betTotalMoneyValue -> betTotalMoneyValue.getBetTotalMoney() != null && betTotalMoneyValue.getBetTotalMoney() > 0).isPresent()){
            betTotalMoney = String.valueOf(NumberUtil.fenToYuan(operaton.getBetTotalMoney()));
        }
        if (Optional.ofNullable(operaton).filter(betEffMoneyValue -> betEffMoneyValue.getBetEffMoney() != null && betEffMoneyValue.getBetEffMoney() > 0).isPresent()){
            betEffMoney = String.valueOf(NumberUtil.fenToYuan(operaton.getBetEffMoney()));
        }
        if (Optional.ofNullable(operaton).filter(gainsValue -> gainsValue.getGains() != null && gainsValue.getGains() > 0).isPresent()){
            gains = String.valueOf(NumberUtil.fenToYuan(operaton.getGains()));
        }
        agentFundInfo.setMembers(members);
        //agentFundInfo.setDepositMembers(depositMembers);
        agentFundInfo.setDepositTotalMoney(depositTotalMoney);
        agentFundInfo.setWithdrawTotalMoney(withdrawTotalMoney);
        agentFundInfo.setBetTotalMoney(betTotalMoney);
        agentFundInfo.setBetEffMoney(betEffMoney);
        agentFundInfo.setGains(gains);
        return agentFundInfo;
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
        vo.setRegisterTime(LocalDateTimeUtil.toAmerica(Long.parseLong(vo.getRegisterTime())));
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
        if(vo.getDomain() == null){
            vo.setDomain("");
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
     *修改代理参数配置
     * @param rc
     * @param agentId
     * @param returnScheme
     * @param adminCost
     * @param feeScheme
     * @return
     */
    @Override
    public String updateAgentConfig(RequestContext rc, Long agentId, Integer returnScheme, Integer adminCost, Integer feeScheme, Integer discount, Integer cost, String domain) {
        if (!checkUpdateAgentConfig(returnScheme, adminCost, feeScheme, discount, cost, domain)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        User agentUser = userService.get(agentId);
        if (agentUser == null) {
            throw UserException.ILLEGAL_USER;
        }

        AgentConfig agentConfig = assembleUpdateAgentConfig(agentId, returnScheme, adminCost, feeScheme, discount,cost, domain);
        if (StringUtils.isNotEmpty(domain) && !agentConfigService.update(agentConfig)) {
            throw UserException.AGENT_CONFIG_UPDATE_FAIL;
        }
        if (!(returnScheme < 0 && adminCost < 0 && feeScheme < 0 && discount < 0 && cost < 0)) {
            EGResp resp = thriftOutAssembleService.updateAgentConfig(assembleConfigUpdBody(agentId, returnScheme, adminCost, feeScheme, discount,cost), "account");
            if (resp == null || resp.getCode() != 0) {
                throw UserException.AGENT_CONFIG_UPDATE_FAIL;
            }
        }
        return UserContants.EMPTY_STRING;
    }

    private boolean checkUpdateAgentConfig(Integer returnScheme, Integer adminCost, Integer feeScheme, Integer discount, Integer cost, String domain) {
        if (returnScheme < 0 && adminCost < 0 && feeScheme < 0 && discount < 0 && cost < 0 && StringUtils.isBlank(domain)) {
            return false;
        }
        return true;
    }

    /**
     * 组装代理参数配置
     * @param agentId
     * @param returnScheme
     * @param adminCost
     * @param feeScheme
     * @param discount
     * @param cost
     * @param domain
     * @return
     */
    private AgentConfig assembleUpdateAgentConfig(Long agentId, Integer returnScheme, Integer adminCost, Integer feeScheme, Integer discount, Integer cost, String domain) {
        AgentConfig config = new AgentConfig();
        config.setAgentId(agentId);
        config.setReturnSchemeId(returnScheme);
        config.setAdminCostId(adminCost);
        config.setFeeId(feeScheme);
        config.setDiscount(discount);
        config.setCost(cost);
        config.setDomain(domain);
        return config;
    }

    /**
     * 组装代理参数修改的thrift body
     * @param agentId
     * @param returnScheme
     * @param adminCost
     * @param feeScheme
     * @param discount
     * @param cost
     * @return
     */
    private String assembleConfigUpdBody(Long agentId, Integer returnScheme, Integer adminCost, Integer feeScheme, Integer discount, Integer cost) {
        JSONObject object = new JSONObject();
        object.put("agentId", agentId);
        if (returnScheme >0) {
            object.put("returnScheme", returnScheme);
        }
        if (adminCost > 0) {
            object.put("adminCost", adminCost);
        }
        if (feeScheme > 0) {
            object.put("feeScheme",feeScheme);
        }
        if (discount > 0) {
            object.put("discount",discount);
        }
        if (cost > 0) {
            object.put("cost",cost);
        }
        return object.toJSONString();
    }

    //TODO 流程有待确认
    //1.url获取ownerId
    //2.ownerId + ownername 获取 stockId
    @Override
    public String agentApply(RequestContext rc, HttpServletRequest request, String account, String password,//String paymentPassword,
                             String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit, String province, String city, String weixin, String qq) {
        String resourceUrl = rc.getOrigin();
        OwnerInfo ownerInfo = dubboOutAssembleService.getOwnerInfoByDomain(resourceUrl);
        if (ownerInfo == null || ownerInfo.getOwnerId() < 0) {
            throw UserException.ILLEGAL_SOURCE_URL;
        }

        //校验用户名、密码、支付密码的格式及其他非空参数
        if (!checkRegisterParam(account, password, //paymentPassword,
                ownerInfo.getOwnerId(), AccountType.agent.value(), email, province, city, weixin, qq)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }

        //校验用户名是否包含非法字符
        if(UserUtil.checkoutUserName(account)){
            throw UserException.ILLEGAL_USERNAME;
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
                System.currentTimeMillis(), bankCardNo, bank, bankDeposit, province, city, weixin, qq);
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
     * @param ownerId
     * @param type
     * @param email
     * @param province
     * @param city
     * @param weixin
     * @param qq
     * @return
     */
    private boolean checkRegisterParam(String account, String password,// String paymentPassword,
                                       long ownerId, int type, String email, String province, String city, String weixin, String qq) {
        //校验用户名和密码
        if (account.length() < 6 || account.length() > 16 || password.length() != 32) {
            return false;
        }
        //校验其他注册参数
        List<String> list = dubboOutAssembleService.getMustRegisterarameters(ownerId, type);
        if (list != null && list.size() > 0) {
            if (list.contains("email")) {
                if (!StringUtils.isNotEmpty(email)) {
                    return false;
                }
            }
            if (list.contains("province")) {
                if (!StringUtils.isNotEmpty(province)) {
                    return false;
                }
            }
            if (list.contains("city")) {
                if (!StringUtils.isNotEmpty(city)) {
                    return false;
                }
            }
            if (list.contains("weixin")) {
                if (!StringUtils.isNotEmpty(weixin)) {
                    return false;
                }
            }
            if (list.contains("qq")) {
                if (!StringUtils.isNotEmpty(qq)) {
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
                                          String bankCardNo, String bank, String bankDeposit, String province, String city, String weixin, String qq) {
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
                vo.setOperatorTime(LocalDateTimeUtil.toAmerica(Long.parseLong(vo.getOperatorTime())));
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
                return getAgentInfoVo(agentId, operaUser.getOwnerId(),true);
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
            AgentReview agentReview = assembleAgentReview(id, 0L, agentApply.getUsername(), rc.getUid(), opera.getUsername(), opera.getOwnerId(), ReviewStatus.parse(reviewStatus), System.currentTimeMillis());
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
            long userId = dubboOutAssembleService.assignUid();
            AgentReview agentReview = assembleAgentReview(id, userId, agentApply.getUsername(), rc.getUid(), opera.getUsername(), opera.getOwnerId(), ReviewStatus.parse(reviewStatus), System.currentTimeMillis());
            sendAgentReviewMq(agentReview);

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
            AgentConfig agentConfig = assembleAgentConfig(opera.getOwnerId(), userId, returnScheme, adminCost, feeScheme, domain, discount, cost);
            //mq 处理 5、添加业主股东代理id映射信息
            OwnerStockAgentMember ownerStockAgentMember = assembleOwnerStockAgent(holderUser.getOwnerId(), holder, userId);
            //mq 处理 6、将代理基础信息放入mongo
            AgentConditionVo agentConditionVo = assembleAgentConfigVo(userId, agentUser.getUsername(), generalizeCode, holder, agentUser.getOwnerId());
            sendAgentAddSuccessMq(agentConfig, ownerStockAgentMember, agentConditionVo);
            /*用户添加成功发送mq*/
            sendAddUserSuccess(agentUser);
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
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(UserContants.CUSTOMER_MQ_TAG.AGENT_CONFIG_ADD.name(), agentConfig);
            jsonObject.put(UserContants.CUSTOMER_MQ_TAG.USER_ID_MAPPING_ADD.name(), ownerStockAgentMember);
            jsonObject.put(UserContants.CUSTOMER_MQ_TAG.AGENT_CONDITION_ADD.name(), agentConditionVo);
            producer.send(Topic.AGENT_ADD_SUCCESS, agentConfig.getAgentId() + "", jsonObject.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param review
     * @Doc 发送代理审核历史
     */
    private void sendAgentReviewMq(AgentReview review) {
        try {
            producer.send(Topic.AGENT_REVIEW_HISTORY, review.getAgentApplyId() + "", JSONObject.toJSONString(review));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @param agentApplyId
     * @param agentId
     * @param agentName
     * @param operUserId
     * @param operUserName
     * @param status
     * @param createTime
     * @return
     * @Doc 组装代理审核对象
     */
    private AgentReview assembleAgentReview(Long agentApplyId, Long agentId, String agentName, Long operUserId, String operUserName, Long ownerId, ReviewStatus status, Long createTime) {
        AgentReview review = new AgentReview();
        review.setAgentApplyId(agentApplyId);
        review.setAgentId(agentId);
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
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("agentId", id);
            jsonObject.put("status", status);
            producer.send(Topic.AGENT_STATUS_UPDATE_SUCCESS, id + "", jsonObject.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public String configs(RequestContext rc) {
        User user = userService.get(rc.getUid());
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        JSONObject result = new JSONObject();
        Map<String, Object> map = dubboOutAssembleService.agentSchemeList(user.getOwnerId());
        result.put("agentConfig", map);
        return result.toJSONString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String fundProfileRefresh(RequestContext requestContext, Long id) {
        if (!Optional.ofNullable(id).filter(value -> value > 0).isPresent()) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        String fundProfile = "{\n" +
                "    \"syncTime\": \"2017-04-18 09:29:33\",\n" +
                "    \"info\": {\n" +
                "        \"members\": 550,\n" +
                "        \"depositMembers\": 420,\n" +
                "        \"depositTotalMoney\": \"290065901\",\n" +
                "        \"withdrawTotalMoney\": \"245001201\",\n" +
                "        \"betTotalMoney\": \"209000671\",\n" +
                "        \"betEffMoney\": \"190076891\",\n" +
                "        \"gains\": \"49087633\"\n" +
                "    }\n" +
                "}";
        JSONObject object = JSONObject.parseObject(fundProfile);
        return JSON.toJSONString(object);
    }

    @Override
    public String getAgentRegisterMustParam(RequestContext rc) {
        User user = userService.get(rc.getUid());
        List<String> result = dubboOutAssembleService.getMustRegisterarameters(user.getOwnerId(),AccountType.agent.value());
        if(result == null){
            return "{}";
        }
        return JSONObject.toJSONString(result);
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
