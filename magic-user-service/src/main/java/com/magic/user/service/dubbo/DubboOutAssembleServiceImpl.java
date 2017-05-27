package com.magic.user.service.dubbo;

import com.magic.config.service.DomainDubboService;
import com.magic.config.vo.OwnerInfo;
import com.magic.passport.po.SubAccount;
import com.magic.passport.service.dubbo.PassportDubboService;
import com.magic.service.java.UuidService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/19
 * Time: 16:33
 *
 * @Doc 统一管理对外部dubbo接口的调用
 */
@Service("dubboOutAssembleService")
public class DubboOutAssembleServiceImpl {

    @Resource
    private UuidService uuidService;

    @Resource
    private PassportDubboService passportDubboService;

    @Resource
    private DomainDubboService domainDubboService;

    /**
     * 分配ID 13位时间戳+2位机器识别码+4位随机数
     *
     * @return
     */
    public long assignUid() {
        try {
            return uuidService.assignUid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 通过域名获取业主信息
     * @param sourceUrl
     * @return
     */
    public OwnerInfo getOwnerInfoByDomain(String sourceUrl) {
        try {
            return domainDubboService.getOwnerInfoByDomain(sourceUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // todo 自己固定一个业主信息
        OwnerInfo ownerInfo = new OwnerInfo();
        ownerInfo.setOwnerId(10001L);
        ownerInfo.setOwnerName("owner2");
        return ownerInfo;
    }

    /**
     * 获取用户最近登陆数据，如登录ip和登录时间
     *
     * @param uid
     * @return
     */
    public SubAccount getSubLoginById(Long uid) {
        try {
            return passportDubboService.getSubLoginById(uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @doc 获取多个用户的最近登录时间
     * @param ids
     * @return
     */
    public Map<Long, SubAccount> getSubLogins(List<Long> ids) {
        try {
            return passportDubboService.getSubLogins(ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取用户ID
     *
     * @param ownerId
     * @param username
     * @return
     */
    public long getUid(Long ownerId, String username) {
        try {
            return passportDubboService.getUid(ownerId, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}