package com.magic.user.consumer;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
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

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        //todo 1、解析业主数据
        //todo 2、根据业主的id和account 创建一个默认的股东账号，业主ID与账号映射表添加数据，stockId=ownerId,stockName=ownerName
        //todo 3、创建代理账号，agentId自动生成，agentName=stockName+"_dl"
        //todo 4、股东、代理的密码为 PasswordCapture.getSaltPwd(ownerPwd)


        ApiLogger.info(String.format("master control add owner success mq consumer start. key:%s, msg:%s", key, msg));
        boolean flag = true;
        try {
            JSONObject object = JSONObject.parseObject(msg);
            long ownerId = object.getLongValue("ownerId");
            String ownerName = object.getString("ownerName");
            String password = object.getString("password");
            String realname = object.getString("realname");
            String telephone = object.getString("telephone");
            String email = object.getString("email");
            Integer registerIp = object.getInteger("registerIp");
            String bankCardNo = object.getString("bankCardNo");
            long registerTime = System.currentTimeMillis();
            /*股东*/
            OwnerAccountUser stockAccountUser = assembleOwnerAccountUser(ownerId, ownerName, ownerId);
            if (accountIdMappingService.add(stockAccountUser) <= 0) {
                if (accountIdMappingService.getUid(ownerId, ownerName) <= 0) {
                    flag = false;
                }
            }
            Login stockLogin = new Login(ownerId, ownerName, PasswordCapture.getSaltPwd(password));
            if (loginService.add(stockLogin) <= 0) {
                if (loginService.get(stockLogin.getUserId()) == null) {
                    flag = false;
                }
            }
            User stockUser = assembleUser(ownerId, ownerName, ownerId, ownerName, realname, telephone, email, AccountType.stockholder, registerTime, registerIp, null, AccountStatus.enable, bankCardNo);
            if (!userService.addStock(stockUser)) {
                if (userService.getUserById(stockUser.getUserId()) == null) {
                    flag = false;
                }
            }
            /*股东*/
            /*代理*/
            long agentId = dubboOutAssembleService.assignUid();
            String agentName = ownerName + "_dl";
            String generalizeCode = UUIDUtil.getCode();
            String agentRealname = realname + "_dl";
            OwnerAccountUser agentAccountUser = assembleOwnerAccountUser(ownerId, ownerName + "_dl", agentId);
            if (accountIdMappingService.add(agentAccountUser) <= 0) {
                if (accountIdMappingService.getUid(agentId, agentName) <= 0) {
                    flag = false;
                }
            }
            Login agentLogin = new Login(agentId, agentName, PasswordCapture.getSaltPwd(password));
            if (loginService.add(agentLogin) <= 0) {
                if (loginService.get(agentId) == null) {
                    flag = false;
                }
            }
            User agentUser = assembleUser(agentId, agentName, ownerId, realname, agentRealname, telephone, email, AccountType.agent, registerTime, registerIp, generalizeCode, AccountStatus.enable, null);
            if (!userService.addAgent(agentUser)) {
                if (userService.get(agentId) == null) {
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
                              AccountType type, Long registerTime, Integer registerIp, String generalizeCode, AccountStatus status, String bankCardNo) {
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


}
