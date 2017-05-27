package com.magic.user.service;

import com.magic.user.entity.Member;
import com.magic.user.enums.AccountStatus;
import com.magic.user.storage.MemberDbService;
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

    @Resource(name = "memberDbService")
    private MemberDbService memberDbService;

    @Override
    public Member getMemberById(Long id) {

        return memberDbService.get(id);
    }

    @Override
    public List<Member> findMemberByIds(List<Long> ids) {
        return memberDbService.find("findMemberByIds", new String[]{"list"}, ids);
    }

    @Override
    public boolean updateMember(Member member) {
        return memberDbService.update(member) > 0;
    }

    @Override
    public boolean updateStatus(Member member) {
        return memberDbService.update("updateStatus", new String[]{"status", "memberId"}, new Object[]{member.getStatus().value(), member.getMemberId()}) > 0;
    }

    @Override
    public boolean saveMember(Member member) {
        return memberDbService.insert(member) > 0;
    }

}
