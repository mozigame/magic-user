package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.IPUtil;
import com.magic.api.commons.tools.UUIDUtil;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.Login;
import com.magic.user.entity.OwnerAccountUser;
import com.magic.user.entity.OwnerStockAgentMember;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.AccountType;
import com.magic.user.service.*;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import com.magic.user.util.PasswordCapture;
import com.magic.user.vo.AgentConditionVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/19
 * Time: 16:52
 */
@Service("masterAddOwnerSuccessConsumer")
@ConsumerConfig(consumerName = "v1masterAddOwnerSuccessConsumer", topic = Topic.BC_COMPANY_ADD_SUCCESS)
public class MasterAddOwnerSuccessConsumer implements Consumer {

    @Resource
    private UserService userService;
    @Resource
    private LoginService loginService;
    @Resource
    private AccountIdMappingService accountIdMappingService;
    @Resource
    private DubboOutAssembleServiceImpl dubboOutAssembleService;
    @Resource
    private OwnerStockAgentService ownerStockAgentService;
    @Resource
    private AgentMongoService agentMongoService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("master control add owner success mq consumer start. key:%s, msg:%s", key, msg));
        boolean flag = true;
        try {
            JSONObject object = JSONObject.parseObject(msg);
            long ownerId = object.getLongValue("id");
            String ownerName = object.getString("account");
            String password = object.getString("password");
            String realname = object.getString("realName");
            String telephone = object.getString("telephone");
            String email = object.getString("email");
            Integer registerIp = IPUtil.ipToInt(object.getString("ip"));
            String bankName = object.getString("bankName");
            String bankDeposit = object.getString("bankDeposit");
            String bankCardNo = object.getString("bankCardNo");
            long registerTime = object.getLongValue("createTime");
            /*股东*/
            OwnerAccountUser stockAccountUser = assembleOwnerAccountUser(ownerId, ownerName, ownerId);
            if (accountIdMappingService.add(stockAccountUser) <= 0) {
                if (accountIdMappingService.getUid(ownerId, ownerName) <= 0) {
                    flag = false;
                }
            }
            User stockUser = assembleUser(ownerId, ownerName, ownerId, ownerName, realname, telephone, email, AccountType.stockholder, registerTime, registerIp, null, AccountStatus.enable, bankCardNo, bankName, bankDeposit);
            if (!userService.addStock(stockUser)) {
                if (userService.getUserById(stockUser.getUserId()) == null) {
                    flag = false;
                }
            }
            Login stockLogin = new Login(ownerId, ownerName, PasswordCapture.getSaltPwd(password));
            if (loginService.add(stockLogin) <= 0) {
                if (loginService.get(stockLogin.getUserId()) == null) {
                    flag = false;
                }
            }
            /*代理*/
            long agentId = dubboOutAssembleService.assignUid();
            String agentName = ownerName + "_dl";
            String generalizeCode = UUIDUtil.getCode();
            String agentRealname = realname;
            OwnerAccountUser agentAccountUser = assembleOwnerAccountUser(ownerId, agentName, agentId);
            if (accountIdMappingService.add(agentAccountUser) <= 0) {
                if (accountIdMappingService.getUid(agentId, agentName) <= 0) {
                    flag = false;
                }
            }
            User agentUser = assembleUser(agentId, agentName, ownerId, realname, agentRealname, telephone, email, AccountType.agent, registerTime, registerIp, generalizeCode, AccountStatus.enable, bankCardNo, bankName, bankDeposit);
            if (!userService.addAgent(agentUser)) {
                if (userService.get(agentId) == null) {
                    flag = false;
                }
            } else {
                AgentConditionVo agentConditionVo = assembleAgentConditionVo(agentUser);
                if (!agentMongoService.saveAgent(agentConditionVo)) {
                    ApiLogger.error(String.format("agent add success mq consumer add agentConditionVo failed.agentId:%d", agentConditionVo.getAgentId()));
                    if (agentMongoService.get(agentConditionVo.getAgentId()) == null) {
                        return false;
                    }
                }
            }
            Login agentLogin = new Login(agentId, agentName, PasswordCapture.getSaltPwd(password));
            if (loginService.add(agentLogin) <= 0) {
                if (loginService.get(agentId) == null) {
                    flag = false;
                }
            }
            OwnerStockAgentMember ownerStockAgentMember = assembleOwnerStockAgentMember(agentId, ownerId, ownerId);
            if (!ownerStockAgentService.add(ownerStockAgentMember)) {
                if (ownerStockAgentService.findById(ownerStockAgentMember) == null) {
                    flag = false;
                }
            }
            return flag;
        } catch (Exception e) {
            ApiLogger.error(String.format("master control add owner success mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

    /**
     * @Doc 组装业主股东代理id映射
     * @param agentId
     * @param stockId
     * @param ownerId
     * @return
     */
    private OwnerStockAgentMember assembleOwnerStockAgentMember(Long agentId, Long stockId, Long ownerId) {
        OwnerStockAgentMember ownerStockAgentMember = new OwnerStockAgentMember();
        ownerStockAgentMember.setAgentId(agentId);
        ownerStockAgentMember.setStockId(stockId);
        ownerStockAgentMember.setOwnerId(ownerId);
        ownerStockAgentMember.setMemNumber(0);
        return ownerStockAgentMember;
    }

    /**
     * @param ownerId
     * @param account
     * @param userId
     * @return
     * @doc 组装业主id与用户账号映射对象
     */
    private OwnerAccountUser assembleOwnerAccountUser(Long ownerId, String account, Long userId) {
        OwnerAccountUser ownerAccountUser = new OwnerAccountUser();
        ownerAccountUser.setAssemAccount(ownerId + UserContants.SPLIT_LINE + account);
        ownerAccountUser.setUserId(userId);
        return ownerAccountUser;
    }

    private User assembleUser(Long userId, String username, Long ownerId, String ownerName, String realname, String telephone, String email,
                              AccountType type, Long registerTime, Integer registerIp, String generalizeCode, AccountStatus status, String bankCardNo, String bankName, String bankDeposit) {
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
        user.setBank(bankName);
        user.setBankDeposit(bankDeposit);
        return user;
    }

    private AgentConditionVo assembleAgentConditionVo(User user) {
        AgentConditionVo vo = new AgentConditionVo();
        vo.setAgentId(user.getUserId());
        vo.setAgentName(user.getUsername());
        vo.setStatus(AccountStatus.enable.value());
        vo.setDepositMoney(0L);
        vo.setWithdrawMoney(0L);
        vo.setMembers(0);
        vo.setGeneralizeCode(user.getGeneralizeCode());
        vo.setStockId(user.getOwnerId());
        vo.setOwnerId(user.getOwnerId());
        vo.setRegisterTime(System.currentTimeMillis());
        vo.setUpdateTime(vo.getRegisterTime());
        return vo;
    }


}
