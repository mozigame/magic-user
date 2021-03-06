package com.magic.user.service;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.utils.StringUtils;
import com.magic.user.entity.Member;
import com.magic.user.storage.CountRedisStorageService;
import com.magic.user.storage.MemberDbService;
import com.magic.user.storage.MemberRedisStorageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

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
    @Resource
    private MemberRedisStorageService memberRedisStorageService;

    @Override
    public Member getMemberById(Long id) {
        Member member = memberRedisStorageService.getMember(id);
        if (member == null) {
            member = memberDbService.get(id);
            if (member != null) {
                memberRedisStorageService.setMember(member);
            }
        }
        return member;
    }

    @Override
    public Map<Long, Member> findMemberByIds(Collection<Long> ids) {
        List<Member> members = memberDbService.find("findMemberByIds", new String[]{"list"}, ids);
        if (Optional.ofNullable(members).isPresent()){
            return members.stream().collect(Collectors.toMap(Member::getMemberId, (p) -> p));
        }
        return null;
    }

    @Override
    public boolean updateMember(Member member) {
        if (!memberRedisStorageService.delsetMember(member.getMemberId())) {
            ApiLogger.warn("delete redis member failed, memberId : "+member.getMemberId());
            memberRedisStorageService.delsetMember(member.getMemberId());
        }
        return memberDbService.update(member) > 0;
    }

    @Override
    public boolean updateStatus(Member member) {
        if (!memberRedisStorageService.delsetMember(member.getMemberId())) {
            ApiLogger.warn("delete redis member failed, memberId : "+member.getMemberId());
            memberRedisStorageService.delsetMember(member.getMemberId());
        }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean refreshCode(String clientId, String code) {
        return memberRedisStorageService.refreshCode(clientId, code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVerifyCode(String clientId) {
        String result = memberRedisStorageService.getVerifyCode(clientId);
        ApiLogger.info(String.format("get verify code. clientId: %s, result: %s", clientId, result));
        return result;
    }

    @Override
    public void addRegisterIpCount(String ip) {
        countRedisStorageService.addRegisterIpCount(ip);
    }

    @Override
    public long getRegisterIpCount(String ip) {
        return countRedisStorageService.getRegisterIpCount(ip);
    }

    @Override
    public void setPeriodLoginCount(Long ownerId, String userName, String ip) {
        countRedisStorageService.setPeriodLoginCount(ownerId, userName, ip);
    }

    @Override
    public long getPeriodLoginCount(Long ownerId, String userName, String ip) {
        return countRedisStorageService.getPeriodLoginCount(ownerId, userName, ip);
    }

    @Override
    public boolean delPeriodLoginCount(Long ownerId, String userName, String ip) {
        return countRedisStorageService.delPeriodLoginCount(ownerId, userName, ip);
    }

}
