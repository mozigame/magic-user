package com.magic.user.vo;

/**
 * User: joey
 * Date: 2017/5/11
 * Time: 21:17
 */
public class AgentInfoVo {

    private Long id;//代理ID
    private Long holder;//股东ID
    private String holderName;//股东名称
    private String realname;//真实姓名
    private String account;//代理帐号
    private String registerTime;//注册时间
    private Integer members;//会员数量
    private Integer storeMembers;//储值会员数量
    private Long depositTotalMoney;//存款金额
    private Long withdrawTotalMoney;//取款金额
    private String promotionCode;//推广代码
    private Integer status;//状态   1启用中    2已停用 ？？？
    private String showStatus;//状态描述
    private String reviewer;//审核人
    private String reviewTime;    //审核时间
    private String registerIp;  //注册ip
    private String lastLoginIp; //最近登录ip
    private String domain;  //绑定域名
    private String domains[];  //绑定域名列表
    private String telephone;   //手机号码
    private String email;   //电子邮箱
    private String bankCardNo;//银行卡号
    private String bank;    //银行名称
    private String bankDeposit; //开户行
    private Integer type;   //账号类型

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHolder() {
        return holder;
    }

    public void setHolder(Long holder) {
        this.holder = holder;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }


    public Integer getMembers() {
        return members;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }

    public Integer getStoreMembers() {
        return storeMembers;
    }

    public void setStoreMembers(Integer storeMembers) {
        this.storeMembers = storeMembers;
    }

    public Long getDepositTotalMoney() {
        return depositTotalMoney;
    }

    public void setDepositTotalMoney(Long depositTotalMoney) {
        this.depositTotalMoney = depositTotalMoney;
    }

    public Long getWithdrawTotalMoney() {
        return withdrawTotalMoney;
    }

    public void setWithdrawTotalMoney(Long withdrawTotalMoney) {
        this.withdrawTotalMoney = withdrawTotalMoney;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String[] getDomains() {
        return domains;
    }

    public void setDomains(String[] domains) {
        this.domains = domains;
    }

    public String getBankDeposit() {
        return bankDeposit;
    }

    public void setBankDeposit(String bankDeposit) {
        this.bankDeposit = bankDeposit;
    }
}
