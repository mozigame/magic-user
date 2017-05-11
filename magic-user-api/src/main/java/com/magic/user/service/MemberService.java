package com.magic.user.service;

import com.magic.user.entity.Member;
import com.magic.user.entity.OnlineMemberConditon;
import com.magic.user.enums.AccountStatus;
import com.magic.api.commons.model.Page;
import com.magic.user.entity.User;
import com.magic.user.po.OnLineMember;
import com.magic.user.vo.UserCondition;

import java.util.List;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:21
 */
public interface MemberService {
    /**
     * 查询会员数据
     * @param id 会员ID
     * @return
     */
    Member getMemberById(long id);

    /**
     * 更新会员数据
     * @param member
     * @return
     */
    boolean updateMember(Member member);

    /**
     * 更新会员状态
     *
     * @param id
     * @param oldStatus
     * @param newStatus
     * @return
     */
    boolean updateStatus(Long id, AccountStatus oldStatus, AccountStatus newStatus);

    Page<User> findByPage(UserCondition userCondition);

    /**
     * 修改会员数据
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param bankDeposit
     * @return
     */
    boolean updateMember(Long id, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit);

    /**
     * 保存会员数据
     * @param member
     * @return
     */
    boolean saveMember(Member member);
}
