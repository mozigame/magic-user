package com.magic.user.service;

import com.google.common.collect.Lists;
import com.magic.api.commons.model.Page;
import com.magic.api.commons.utils.StringUtils;
import com.magic.user.entity.Member;
import com.magic.user.entity.OnlineMemberConditon;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.storage.MemberDbService;
import com.magic.user.po.OnLineMember;
import com.magic.user.storage.UserMongoService;
import com.magic.user.util.UserVo;
import com.magic.user.commons.OperaSign;
import com.magic.user.vo.UserCondition;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 1:34
 */
@Service("memberService")
public class MemberServiceImpl implements MemberService {

//    @Resource(name = "userMongoService")
    private UserMongoService userMongoService;
    @Resource(name = "memberDbService")
    private MemberDbService memberDbService;

    @Override
    public Member getMemberById(long id) {
        return memberDbService.get(id);
    }

    @Override
    public boolean updateMember(Member member) {
        return false;
    }

    @Override
    public boolean updateStatus(Long id, AccountStatus oldStatus, AccountStatus newStatus) {
        return false;
    }

    @Override
    public Page<User> findByPage(UserCondition condition) {
        List<String> paramNames = Lists.newArrayList();
        List<String> sign = Lists.newArrayList();
        List<Object> values = Lists.newArrayList();
        if (condition != null) {
            if (condition.getLevel() > 0) {
                paramNames.add("level");
                sign.add(OperaSign.eq);
                values.add(condition.getLevel());
            }
            if (condition.getStatus() > 0) {
                paramNames.add("status");
                sign.add(OperaSign.eq);
                values.add(condition.getStatus());
            }
            if (condition.getAccount() != null) {
                String type = condition.getAccount().getString("type");
                if (StringUtils.isNotBlank(type) && Integer.parseInt(type) > 0) {
                    paramNames.add("type");
                    sign.add(OperaSign.eq);
                    values.add(type);
                }
                String username = condition.getAccount().getString("username");
                if (StringUtils.isNotEmpty(username)) {
                    paramNames.add("username");
                    sign.add(OperaSign.eq);
                    values.add(username);
                }
            }
            if (condition.getRegister() != null) {
                String start = condition.getRegister().getString("start");
                if (StringUtils.isNotBlank(start) && Long.parseLong(start) > 0) {
                    paramNames.add("register_time");
                    sign.add(OperaSign.gt);
                    values.add(Long.parseLong(start));
                }
                String end = condition.getRegister().getString("end");
                if (StringUtils.isNotBlank(end) && Long.parseLong(end) > 0) {
                    paramNames.add("register_time");
                    sign.add(OperaSign.lt);
                    values.add(Long.parseLong(end));
                }
            }
            if (condition.getDepositCount() != null) {
                String min = condition.getRegister().getString("min");
                if (StringUtils.isNotBlank(min) && Long.parseLong(min) > 0) {
                    paramNames.add("depositCount");
                    sign.add(OperaSign.gt);
                    values.add(Long.parseLong(min));
                }
                String max = condition.getRegister().getString("max");
                if (StringUtils.isNotBlank(max) && Long.parseLong(max) > 0) {
                    paramNames.add("depositCount");
                    sign.add(OperaSign.lt);
                    values.add(Long.parseLong(max));
                }
            }
            if (condition.getWithdrawCount() != null) {
                String min = condition.getRegister().getString("min");
                if (StringUtils.isNotBlank(min) && Long.parseLong(min) > 0) {
                    paramNames.add("withdrawCount");
                    sign.add(OperaSign.gt);
                    values.add(Long.parseLong(min));
                }
                String max = condition.getRegister().getString("max");
                if (StringUtils.isNotBlank(max) && Long.parseLong(max) > 0) {
                    paramNames.add("withdrawCount");
                    sign.add(OperaSign.lt);
                    values.add(Long.parseLong(max));
                }
            }
            if (condition.getDepositMoney() != null) {
                String min = condition.getRegister().getString("min");
                if (StringUtils.isNotBlank(min) && Long.parseLong(min) > 0) {
                    paramNames.add("depositMoney");
                    sign.add(OperaSign.gt);
                    values.add(Long.parseLong(min));
                }
                String max = condition.getRegister().getString("max");
                if (StringUtils.isNotBlank(max) && Long.parseLong(max) > 0) {
                    paramNames.add("depositMoney");
                    sign.add(OperaSign.lt);
                    values.add(Long.parseLong(max));
                }
            }
            if (condition.getWithdrawMoney() != null) {
                String min = condition.getRegister().getString("min");
                if (StringUtils.isNotBlank(min) && Long.parseLong(min) > 0) {
                    paramNames.add("withdrawMoney");
                    sign.add(OperaSign.gt);
                    values.add(Long.parseLong(min));
                }
                String max = condition.getRegister().getString("max");
                if (StringUtils.isNotBlank(max) && Long.parseLong(max) > 0) {
                    paramNames.add("withdrawMoney");
                    sign.add(OperaSign.lt);
                    values.add(Long.parseLong(max));
                }
            }
            Sort sort = null;
            if (condition.getSort() != null) {
                String name = condition.getSort().getString("name");
                String type = condition.getSort().getString("type");
                sort = new Sort(Sort.Direction.fromString(type), name);
            }
            PageRequest pageable = new PageRequest(condition.getPageNo(), condition.getPageSize(), sort);
            Page page = userMongoService.findPage(pageable, (String[]) paramNames.toArray(), (String[]) sign.toArray(), values.toArray());
            Page<User> pageUser = page;
            List<User> users = Lists.newArrayList();
            for (Object userVo : page.getResult()) {
                User user = UserVo.parseUser((UserVo) userVo);
                users.add(user);
            }
            pageUser.setResult(users);
            return pageUser;
        }
        return null;
    }

    @Override
    public boolean updateMember(Long id, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit) {
        return false;
    }

    @Override
    public boolean saveMember(Member member) {
        return memberDbService.insert(member) > 0;
    }

}
