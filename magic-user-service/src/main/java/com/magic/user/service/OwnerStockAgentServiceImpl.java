package com.magic.user.service;

import com.magic.user.entity.OwnerStockAgentMember;
import com.magic.user.storage.OwnerStockAgentDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    public long add(OwnerStockAgentMember ownerStockAgentMember) {
        return ownerStockAgentDbService.insert(ownerStockAgentMember);
    }
}
