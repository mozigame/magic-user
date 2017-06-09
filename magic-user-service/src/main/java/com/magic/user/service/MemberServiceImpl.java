package com.magic.user.service;

import com.magic.user.entity.Member;
import com.magic.user.storage.CountRedisStorageService;
import com.magic.user.storage.MemberDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 1:34
 */
@Service("memberService")
public class MemberServiceImpl implements MemberService {

    @Resource(name = "memberDbService")
    private MemberDbService memberDbService;
    @Resource
    private CountRedisStorageService countRedisStorageService;

    @Override
    public Member getMemberById(Long id) {

        return memberDbService.get(id);
    }

    @Override
    public List<Member> findMemberByIds(Collection<Long> ids) {
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
        Long result = memberDbService.insert(member);
        boolean temp = Optional.ofNullable(result).filter(resultValue -> resultValue > 0).isPresent();
        if (temp){
            countRedisStorageService.incrMember(member.getOwnerId());
        }
        return temp;
    }

}
