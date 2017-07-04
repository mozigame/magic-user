package com.magic.user.service;

import com.magic.user.entity.Member;
import com.magic.user.entity.OwnerStockAgentMember;
import com.magic.user.enums.AccountType;
import com.magic.user.storage.OwnerStockAgentDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * User: joey
 * Date: 2017/5/9
 * Time: 10:43
 */
@Service("ownerStockAgentService")
public class OwnerStockAgentServiceImpl implements OwnerStockAgentService {


    @Resource(name = "ownerStockAgentDbService")
    private OwnerStockAgentDbService ownerStockAgentDbService;


    @Override
    public boolean add(OwnerStockAgentMember ownerStockAgentMember) {
        long result = ownerStockAgentDbService.insert(ownerStockAgentMember);
        return result > 0;
    }

    @Override
    public OwnerStockAgentMember findById(OwnerStockAgentMember ownerStockAgentMember) {
        return (OwnerStockAgentMember) ownerStockAgentDbService.get("findById", new String[]{"ownerMapper"}, ownerStockAgentMember);
    }

    @Override
    public boolean updateMemNumber(Member member) {
        long result = ownerStockAgentDbService.update("updateMemNumber",new String[]{"member"},member);
        return result > 0;
    }

    @Override
    public OwnerStockAgentMember countMembersById(Long id, AccountType type) {
        if(id == null || type == null){
            return null;
        }
        String name = null;
        if(type == AccountType.stockholder)name = "stockId";
        if(type == AccountType.proprietor)name = "ownerId";
        if(type == AccountType.agent) name = "agentId";
        if(name == null) return null;
        return (OwnerStockAgentMember) ownerStockAgentDbService.get("countMembersById", new String[]{name}, id);
    }

    @Override
    public List<OwnerStockAgentMember> countMembersByIds(List<Long> ids, AccountType type) {
        if(ids == null || type == null){
            return null;
        }
        String name = null;
        if(type == AccountType.stockholder){
            name = "stockIds";
        }
        if(type == AccountType.proprietor){
            name = "ownerIds";
        }
        if(type == AccountType.agent){
            name = "agentIds";
        }
        return ownerStockAgentDbService.find("countMembersByIds",new String[]{name},ids);
    }

}
