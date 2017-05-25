package com.magic.user.resource.service;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.entity.User;
import com.magic.user.po.DownLoadFile;

import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/3
 * Time: 15:00
 */
public interface StockResourceService {

    /**
     * @Doc 查询所有股东
     * @param rc
     * @return
     */
    String findAllStock(RequestContext rc);


    /**
     * @Doc 导出股东列表
     * @param rc
     * @return
     */
    DownLoadFile listExport(RequestContext rc);

    /**
     * 查询股东名称id列表
     * @param rc
     * @return
     */
    String simpleList(RequestContext rc );

    /**
     * @doc 获取股东详情
     * @param rc
     * @param id
     * @return
     */
    String getStockDetail(RequestContext rc,Long id);

    /**
     * @Doc 重置密码
     * @param rc
     * @param id
     * @param pwd
     * @return
     */
    String updatePwd(RequestContext rc,Long id, String pwd);

    /**
     * @Doc 修改基础信息
     * @param rc
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param status
     * @return
     */
    String update(RequestContext rc, Long id,String realname, String telephone, String email, String bankCardNo, String bank, Integer status);

    /**
     * 添加股东
     * @param rc
     * @param account
     * @param password
     * @param realname
     * @param bankCardNo
     * @param bankDeposit
     * @param bank
     * @param telephone
     * @param currencyType
     * @param email
     * @param sex
     * @return
     */
    String add(RequestContext rc, String account, String password, String realname,String bankCardNo, String bankDeposit, String bank, String telephone, Integer currencyType, String email, Integer sex);

    /**
     * @Doc 修改状态
     * @param rc
     * @param id
     * @param status
     * @return
     */
    String updateStatus(RequestContext rc, Long id, Integer status);
}
