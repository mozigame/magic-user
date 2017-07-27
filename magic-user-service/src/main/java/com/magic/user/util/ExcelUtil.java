package com.magic.user.util;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.tools.LocalDateTimeUtil;
import com.magic.user.vo.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * ExcelUtil
 * Excel工具
 *
 * @author zj
 * @date 2015/12/4
 */
public class ExcelUtil {


    public static final String MEMBER_LIST = "会员列表";
    public static final String ONLINE_MEMBER_LIST = "在线会员列表";
    public static final String MEMBER_LEVEL_LIST = "会员层级列表";
    public static final String AGENT_LIST = "代理列表";
    public static final String AGENT_REVIEW_LIST = "代理审核列表";
    public static final String STOCK_LIST = "股东列表";
    public static final String MODIFY_LIST = "资料修改记录";
    public static final String WORKER_LIST = "子账号列表";

    /**
     * @param list
     * @param filename
     * @return
     * @Doc 组装代理审核列表导出数据
     */
    public static byte[] agentReviewListExport(List<AgentApplyVo> list, String filename) {
        ApiLogger.info("开始生成Excel!fileName:".concat(filename));
        try {

            //创建Excel工作薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            //创建工作表，可指定工作表名称
            Sheet sheet = workbook.createSheet();
            //工作表表头
            Row head = sheet.createRow(0);
            //设置居中
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            //head.setRowStyle(style);
            //创建单元格,并设置单元格格式和内容
            Cell reviewID = head.createCell(0);
            reviewID.setCellStyle(style);
            reviewID.setCellType(XSSFCell.CELL_TYPE_STRING);
            reviewID.setCellValue("序号");
            Cell holderName = head.createCell(1);
            holderName.setCellStyle(style);
            holderName.setCellType(XSSFCell.CELL_TYPE_STRING);
            holderName.setCellValue("股东");
            Cell agentName = head.createCell(2);
            agentName.setCellStyle(style);
            agentName.setCellType(XSSFCell.CELL_TYPE_STRING);
            agentName.setCellValue("代理账号");
            Cell telephone = head.createCell(3);
            telephone.setCellStyle(style);
            telephone.setCellType(XSSFCell.CELL_TYPE_STRING);
            telephone.setCellValue("联系电话");
            Cell email = head.createCell(4);
            email.setCellStyle(style);
            email.setCellType(XSSFCell.CELL_TYPE_STRING);
            email.setCellValue("email");
            Cell sourceUrl = head.createCell(5);
            sourceUrl.setCellStyle(style);
            sourceUrl.setCellType(XSSFCell.CELL_TYPE_STRING);
            sourceUrl.setCellValue("加入来源");
            Cell registerIp = head.createCell(6);
            registerIp.setCellStyle(style);
            registerIp.setCellType(XSSFCell.CELL_TYPE_STRING);
            registerIp.setCellValue("注册IP");
            Cell reviewName = head.createCell(7);
            reviewName.setCellStyle(style);
            reviewName.setCellType(XSSFCell.CELL_TYPE_STRING);
            reviewName.setCellValue("审核人");
            Cell reviewTime = head.createCell(8);
            reviewTime.setCellStyle(style);
            reviewTime.setCellType(XSSFCell.CELL_TYPE_STRING);
            reviewTime.setCellValue("审核时间");
            Cell status = head.createCell(9);
            status.setCellStyle(style);
            status.setCellType(XSSFCell.CELL_TYPE_STRING);
            status.setCellValue("状态");
            //表头创建完成
            for (int i = 0; i < list.size(); i++) {
                AgentApplyVo vo = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(vo.getHolderName());
                row.createCell(2).setCellValue(vo.getAccount());
                row.createCell(3).setCellValue(vo.getTelephone());
                row.createCell(4).setCellValue(vo.getEmail());
                row.createCell(5).setCellValue(vo.getSource());
                row.createCell(6).setCellValue(vo.getRegisterIp());
                row.createCell(7).setCellValue(vo.getOperUserName());
                row.createCell(8).setCellValue(vo.getOperatorTime());
                row.createCell(9).setCellValue(vo.getShowStatus());
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            byte[] bytes = os.toByteArray();
            return bytes;
        } catch (Exception e) {
            ApiLogger.error("生成代理审核列表失败！data size:".concat(String.valueOf(10)).concat(",fileName:").concat(filename), e);
            return null;
        }
    }


    /**
     * @param list
     * @param filename
     * @return
     * @Doc 组装股东列表导出数据
     */
    public static byte[] stockListExport(List<StockInfoVo> list, String filename) {
        ApiLogger.info("开始生成Excel!fileName:".concat(filename));
        try {

            //创建Excel工作薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            //创建工作表，可指定工作表名称
            Sheet sheet = workbook.createSheet();
            //工作表表头
            Row head = sheet.createRow(0);
            //设置居中
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            //head.setRowStyle(style);
            //创建单元格,并设置单元格格式和内容
            Cell reviewID = head.createCell(0);
            reviewID.setCellStyle(style);
            reviewID.setCellType(XSSFCell.CELL_TYPE_STRING);
            reviewID.setCellValue("序号");
            Cell holderName = head.createCell(1);
            holderName.setCellStyle(style);
            holderName.setCellType(XSSFCell.CELL_TYPE_STRING);
            holderName.setCellValue("股东账号");
            Cell agentName = head.createCell(2);
            agentName.setCellStyle(style);
            agentName.setCellType(XSSFCell.CELL_TYPE_STRING);
            agentName.setCellValue("代理人数");
            Cell telephone = head.createCell(3);
            telephone.setCellStyle(style);
            telephone.setCellType(XSSFCell.CELL_TYPE_STRING);
            telephone.setCellValue("注册时间");
            Cell email = head.createCell(4);
            email.setCellStyle(style);
            email.setCellType(XSSFCell.CELL_TYPE_STRING);
            email.setCellValue("状态");
            //表头创建完成
            for (int i = 0; i < list.size(); i++) {
                StockInfoVo vo = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(vo.getAccount());
                row.createCell(2).setCellValue(vo.getAgentNumber());
                row.createCell(3).setCellValue(vo.getRegisterTime());
                row.createCell(4).setCellValue(vo.getShowStatus());
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            byte[] bytes = os.toByteArray();
            return bytes;
        } catch (Exception e) {
            ApiLogger.error("生成股东列表失败！data size:".concat(String.valueOf(10)).concat(",fileName:").concat(filename), e);
            return null;
        }
    }


    /**
     * @param list
     * @param filename
     * @return
     * @Doc 组装代理列表导出数据
     */
    public static byte[] agentListExport(List<AgentInfoVo> list, String filename) {
        ApiLogger.info("开始生成Excel!fileName:".concat(filename));
        try {

            //创建Excel工作薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            //创建工作表，可指定工作表名称
            Sheet sheet = workbook.createSheet();
            //工作表表头
            Row head = sheet.createRow(0);
            //设置居中
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            //head.setRowStyle(style);
            //创建单元格,并设置单元格格式和内容
            Cell stockName = head.createCell(0);
            stockName.setCellStyle(style);
            stockName.setCellType(XSSFCell.CELL_TYPE_STRING);
            stockName.setCellValue("股东");
            Cell agentName = head.createCell(1);
            agentName.setCellStyle(style);
            agentName.setCellType(XSSFCell.CELL_TYPE_STRING);
            agentName.setCellValue("代理账号");
            Cell registerTime = head.createCell(2);
            registerTime.setCellStyle(style);
            registerTime.setCellType(XSSFCell.CELL_TYPE_STRING);
            registerTime.setCellValue("注册时间");
            Cell members = head.createCell(3);
            members.setCellStyle(style);
            members.setCellType(XSSFCell.CELL_TYPE_STRING);
            members.setCellValue("会员数量");
            Cell storeMembers = head.createCell(4);
            storeMembers.setCellStyle(style);
            storeMembers.setCellType(XSSFCell.CELL_TYPE_STRING);
            storeMembers.setCellValue("储值会员数量");
            Cell depositTotalMoney = head.createCell(5);
            depositTotalMoney.setCellStyle(style);
            depositTotalMoney.setCellType(XSSFCell.CELL_TYPE_STRING);
            depositTotalMoney.setCellValue("存款金额");
            Cell withdrawTotalMoney = head.createCell(6);
            withdrawTotalMoney.setCellStyle(style);
            withdrawTotalMoney.setCellType(XSSFCell.CELL_TYPE_STRING);
            withdrawTotalMoney.setCellValue("取款金额");
            Cell promotionCode = head.createCell(7);
            promotionCode.setCellStyle(style);
            promotionCode.setCellType(XSSFCell.CELL_TYPE_STRING);
            promotionCode.setCellValue("推广代码");
            Cell reviewer = head.createCell(8);
            reviewer.setCellStyle(style);
            reviewer.setCellType(XSSFCell.CELL_TYPE_STRING);
            reviewer.setCellValue("审核人");
            Cell reviewTime = head.createCell(9);
            reviewTime.setCellStyle(style);
            reviewTime.setCellType(XSSFCell.CELL_TYPE_STRING);
            reviewTime.setCellValue("审核时间");
            Cell status = head.createCell(10);
            status.setCellStyle(style);
            status.setCellType(XSSFCell.CELL_TYPE_STRING);
            status.setCellValue("状态");
            //表头创建完成
            for (int i = 0; i < list.size(); i++) {
                AgentInfoVo vo = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(vo.getHolderName());
                row.createCell(1).setCellValue(vo.getAccount());
                row.createCell(2).setCellValue(vo.getRegisterTime());
                row.createCell(3).setCellValue(vo.getMembers());
                row.createCell(4).setCellValue(vo.getStoreMembers());
                row.createCell(5).setCellValue(vo.getDepositTotalMoney());
                row.createCell(6).setCellValue(vo.getWithdrawTotalMoney());
                row.createCell(7).setCellValue(vo.getPromotionCode());
                row.createCell(8).setCellValue(vo.getReviewer());
                row.createCell(9).setCellValue(vo.getReviewTime());
                row.createCell(10).setCellValue(vo.getShowStatus());
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            byte[] bytes = os.toByteArray();
            return bytes;
        } catch (Exception e) {
            ApiLogger.error("生成代理列表失败！data size:".concat(String.valueOf(10)).concat(",fileName:").concat(filename), e);
            return null;
        }
    }

    /**
     * @param list
     * @param filename
     * @param downloadSource
     * @return
     * @Doc 组装会员列表导出数据
     */
    public static byte[] memberListExport(List<MemberListVo> list, String filename, int downloadSource) {
        ApiLogger.info("开始生成Excel!fileName:".concat(filename));
        try {
            //创建Excel工作薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            //创建工作表，可指定工作表名称
            Sheet sheet = workbook.createSheet();
            //工作表表头
            Row head = sheet.createRow(0);
            //设置居中
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            //head.setRowStyle(style);
            //创建单元格,并设置单元格格式和内容
            byte[] bytes = null;
            switch (downloadSource) {
                case 0:
                    bytes = downloadSource0(list, workbook, sheet, head, style);
                    break;
                case 1:
                    bytes = downloadSource1(list, workbook, sheet, head, style);
                    break;
                default:
                    ApiLogger.error("生成在线会员列表失败！downloadSource = " + downloadSource);
                    bytes = null;
                    break;
            }
            return bytes;
        } catch (Exception e) {
            ApiLogger.error("生成在线会员列表失败！data size:".concat(String.valueOf(10)).concat(",fileName:").concat(filename), e);
            return null;
        }
    }

    private final static String[] ELEMENT_FOR_SOURCE1 =
            new String[]{
                    "序号",
                    "会员帐号",
                    "所属代理",
                    "会员层级",
                    "存款次数",
                    "存款总额",
                    "最大存款数额",
                    "取款次数",
                    "取款总额",
                    "余额",
                    "最近登录"
            };

    private static byte[] downloadSource1(List<MemberListVo> list, XSSFWorkbook workbook, Sheet sheet, Row head, CellStyle style) throws IOException {

        formatFirstRow(head, style,ELEMENT_FOR_SOURCE1);
        //表头创建完成
        for (int i = 0; i < list.size(); i++) {
            MemberListVo vo = list.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(vo.getAccount());
            row.createCell(2).setCellValue(vo.getAgent());
            row.createCell(3).setCellValue(vo.getLevel());
            row.createCell(4).setCellValue(vo.getDepositCount());
            row.createCell(5).setCellValue(vo.getDepositMoney());
            row.createCell(6).setCellValue(vo.getMaxDepositMoney());
            row.createCell(7).setCellValue(vo.getWithdrawCount());
            row.createCell(8).setCellValue(vo.getWithdrawMoney());
            row.createCell(9).setCellValue(vo.getBalance());
            row.createCell(10).setCellValue(vo.getLastLoginTime());
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        workbook.write(os);
        byte[] bytes = os.toByteArray();
        return bytes;
    }

    private static void formatFirstRow(Row head, CellStyle style, String[] elementForSource) {
        for (int i=0; i<elementForSource.length; ++i){
            Cell id = head.createCell(i);
            id.setCellStyle(style);
            id.setCellType(XSSFCell.CELL_TYPE_STRING);
            id.setCellValue(elementForSource[i]);
        }
    }

    private static byte[] downloadSource0(List<MemberListVo> list, XSSFWorkbook workbook, Sheet sheet, Row head, CellStyle style) throws IOException {
        Cell id = head.createCell(0);
        id.setCellStyle(style);
        id.setCellType(XSSFCell.CELL_TYPE_STRING);
        id.setCellValue("序号");
        Cell memberName = head.createCell(1);
        memberName.setCellStyle(style);
        memberName.setCellType(XSSFCell.CELL_TYPE_STRING);
        memberName.setCellValue("会员账号");
        Cell agentName = head.createCell(2);
        agentName.setCellStyle(style);
        agentName.setCellType(XSSFCell.CELL_TYPE_STRING);
        agentName.setCellValue("所屬代理");
        Cell levle = head.createCell(3);
        levle.setCellStyle(style);
        levle.setCellType(XSSFCell.CELL_TYPE_STRING);
        levle.setCellValue("会员层级");
        Cell balance = head.createCell(4);
        balance.setCellStyle(style);
        balance.setCellType(XSSFCell.CELL_TYPE_STRING);
        balance.setCellValue("余额");
        Cell returnWater = head.createCell(5);
        returnWater.setCellStyle(style);
        returnWater.setCellType(XSSFCell.CELL_TYPE_STRING);
        returnWater.setCellValue("当前反水方案");
        Cell registerTime = head.createCell(6);
        registerTime.setCellStyle(style);
        registerTime.setCellType(XSSFCell.CELL_TYPE_STRING);
        registerTime.setCellValue("注册时间");
        Cell lastLoginTime = head.createCell(7);
        lastLoginTime.setCellStyle(style);
        lastLoginTime.setCellType(XSSFCell.CELL_TYPE_STRING);
        lastLoginTime.setCellValue("最近登录时间");
        Cell status = head.createCell(8);
        status.setCellStyle(style);
        status.setCellType(XSSFCell.CELL_TYPE_STRING);
        status.setCellValue("状态");

        //表头创建完成
        for (int i = 0; i < list.size(); i++) {
            MemberListVo vo = list.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(vo.getAccount());
            row.createCell(2).setCellValue(vo.getAgent());
            row.createCell(3).setCellValue(vo.getLevel());
            row.createCell(4).setCellValue(vo.getBalance());
            row.createCell(5).setCellValue(vo.getReturnWaterName());
            row.createCell(6).setCellValue(vo.getRegisterTime());
            row.createCell(7).setCellValue(vo.getLastLoginTime());
            row.createCell(8).setCellValue(vo.getShowStatus());
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        workbook.write(os);
        byte[] bytes = os.toByteArray();
        return bytes;
    }

    /**
     * @param list
     * @param filename
     * @return
     * @Doc 组装在线会员列表导出数据
     */
    public static byte[] onLineMemberListExport(List<OnLineMemberVo> list, String filename) {
        ApiLogger.info("开始生成Excel!fileName:".concat(filename));
        try {
            //创建Excel工作薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            //创建工作表，可指定工作表名称
            Sheet sheet = workbook.createSheet();
            //工作表表头
            Row head = sheet.createRow(0);
            //设置居中
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            //head.setRowStyle(style);
            //创建单元格,并设置单元格格式和内容
            Cell id = head.createCell(0);
            id.setCellStyle(style);
            id.setCellType(XSSFCell.CELL_TYPE_STRING);
            id.setCellValue("序号");

            Cell memberName = head.createCell(1);
            memberName.setCellStyle(style);
            memberName.setCellType(XSSFCell.CELL_TYPE_STRING);
            memberName.setCellValue("会员账号");

            Cell loginTime = head.createCell(2);
            loginTime.setCellStyle(style);
            loginTime.setCellType(XSSFCell.CELL_TYPE_STRING);
            loginTime.setCellValue("登入时间");

            Cell loginIp = head.createCell(3);
            loginIp.setCellStyle(style);
            loginIp.setCellType(XSSFCell.CELL_TYPE_STRING);
            loginIp.setCellValue("登入IP");

            Cell registerTime = head.createCell(4);
            registerTime.setCellStyle(style);
            registerTime.setCellType(XSSFCell.CELL_TYPE_STRING);
            registerTime.setCellValue("注册时间");

            Cell registerIp = head.createCell(5);
            registerIp.setCellStyle(style);
            registerIp.setCellType(XSSFCell.CELL_TYPE_STRING);
            registerIp.setCellValue("注册IP");

//            Cell info = head.createCell(6);
//            info.setCellStyle(style);
//            info.setCellType(XSSFCell.CELL_TYPE_STRING);
//            info.setCellValue("备注");

            //表头创建完成
            for (int i = 0; i < list.size(); i++) {
                OnLineMemberVo vo = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(vo.getAccount());
                if (vo.getLoginTime() != null) {
                    row.createCell(2).setCellValue(vo.getLoginTime());
                }
                if (vo.getLoginIp() != null) {
                    row.createCell(3).setCellValue(vo.getLoginIp());
                }
                if (vo.getRegisterTime() != null) {
                    row.createCell(4).setCellValue(vo.getRegisterTime());
                }
                if (vo.getRegisterIp() != null) {
                    row.createCell(5).setCellValue(vo.getRegisterIp());
                }

            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            byte[] bytes = os.toByteArray();
            return bytes;
        } catch (Exception e) {
            ApiLogger.error("生成会员列表失败！data size:".concat(String.valueOf(10)).concat(",fileName:").concat(filename), e);
            return null;
        }
    }

    /**
     * @param list
     * @param filename
     * @return
     * @Doc 组装会员层级列表导出数据
     */
    public static byte[] memberLevelListExport(List<MemberLevelListVo> list, String filename) {
        ApiLogger.info("开始生成Excel!fileName:".concat(filename));
        try {
            //创建Excel工作薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            //创建工作表，可指定工作表名称
            Sheet sheet = workbook.createSheet();
            //工作表表头
            Row head = sheet.createRow(0);
            //设置居中
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            //head.setRowStyle(style);
            //创建单元格,并设置单元格格式和内容
            Cell id = head.createCell(0);
            id.setCellStyle(style);
            id.setCellType(XSSFCell.CELL_TYPE_STRING);
            id.setCellValue("序号");
            Cell name = head.createCell(1);
            name.setCellStyle(style);
            name.setCellType(XSSFCell.CELL_TYPE_STRING);
            name.setCellValue("层及名称");
            Cell createTime = head.createCell(2);
            createTime.setCellStyle(style);
            createTime.setCellType(XSSFCell.CELL_TYPE_STRING);
            createTime.setCellValue("创建时间");
            Cell members = head.createCell(3);
            members.setCellStyle(style);
            members.setCellType(XSSFCell.CELL_TYPE_STRING);
            members.setCellValue("会员人数");
            Cell depositNumbers = head.createCell(4);
            depositNumbers.setCellStyle(style);
            depositNumbers.setCellType(XSSFCell.CELL_TYPE_STRING);
            depositNumbers.setCellValue("存款次数");
            Cell depositTotalMoney = head.createCell(5);
            depositTotalMoney.setCellStyle(style);
            depositTotalMoney.setCellType(XSSFCell.CELL_TYPE_STRING);
            depositTotalMoney.setCellValue("存款总额");
            Cell maxDepositMoney = head.createCell(6);
            maxDepositMoney.setCellStyle(style);
            maxDepositMoney.setCellType(XSSFCell.CELL_TYPE_STRING);
            maxDepositMoney.setCellValue("最大存款数额");
            Cell withdrawNumbers = head.createCell(7);
            withdrawNumbers.setCellStyle(style);
            withdrawNumbers.setCellType(XSSFCell.CELL_TYPE_STRING);
            withdrawNumbers.setCellValue("取款次数");
            Cell withdrawTotalMoney = head.createCell(8);
            withdrawTotalMoney.setCellStyle(style);
            withdrawTotalMoney.setCellType(XSSFCell.CELL_TYPE_STRING);
            withdrawTotalMoney.setCellValue("取款总额");
            Cell returnWaterName = head.createCell(8);
            returnWaterName.setCellStyle(style);
            returnWaterName.setCellType(XSSFCell.CELL_TYPE_STRING);
            returnWaterName.setCellValue("反水方案");
            Cell discountName = head.createCell(8);
            discountName.setCellStyle(style);
            discountName.setCellType(XSSFCell.CELL_TYPE_STRING);
            discountName.setCellValue("出入款优惠方案");
            //表头创建完成
            for (int i = 0; i < list.size(); i++) {
                MemberLevelListVo vo = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(vo.getName());
                row.createCell(2).setCellValue(vo.getCreateTime());
                row.createCell(3).setCellValue(vo.getMembers());
                row.createCell(4).setCellValue(vo.getCondition().getDepositNumbers());
                row.createCell(5).setCellValue(vo.getCondition().getDepositTotalMoney());
                row.createCell(6).setCellValue(vo.getCondition().getMaxDepositMoney());
                row.createCell(7).setCellValue(vo.getCondition().getWithdrawNumbers());
                row.createCell(8).setCellValue(vo.getCondition().getWithdrawTotalMoney());
                row.createCell(9).setCellValue(vo.getReturnWaterName());
                row.createCell(10).setCellValue(vo.getDiscountName());
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            byte[] bytes = os.toByteArray();
            return bytes;
        } catch (Exception e) {
            ApiLogger.error("生成会员列表失败！data size:".concat(String.valueOf(10)).concat(",fileName:").concat(filename), e);
            return null;
        }
    }

    /**
     * @param list
     * @param filename
     * @return
     * @Doc 组装资料修改记录列表导出数据
     */
    public static byte[] modifyListExport(List<AccountModifyListVo> list, String filename) {
        ApiLogger.info("开始生成Excel!fileName:".concat(filename));
        try {
            //创建Excel工作薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            //创建工作表，可指定工作表名称
            Sheet sheet = workbook.createSheet();
            //工作表表头
            Row head = sheet.createRow(0);
            //设置居中
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            //head.setRowStyle(style);
            //创建单元格,并设置单元格格式和内容
            Cell id = head.createCell(0);
            id.setCellStyle(style);
            id.setCellType(XSSFCell.CELL_TYPE_STRING);
            id.setCellValue("序号");
            Cell stockName = head.createCell(1);
            stockName.setCellStyle(style);
            stockName.setCellType(XSSFCell.CELL_TYPE_STRING);
            stockName.setCellValue("股东");
            Cell accountType = head.createCell(2);
            accountType.setCellStyle(style);
            accountType.setCellType(XSSFCell.CELL_TYPE_STRING);
            accountType.setCellValue("账号类型");
            Cell account = head.createCell(3);
            account.setCellStyle(style);
            account.setCellType(XSSFCell.CELL_TYPE_STRING);
            account.setCellValue("账号");
            Cell before = head.createCell(4);
            before.setCellStyle(style);
            before.setCellType(XSSFCell.CELL_TYPE_STRING);
            before.setCellValue("修改前信息");
            Cell after = head.createCell(5);
            after.setCellStyle(style);
            after.setCellType(XSSFCell.CELL_TYPE_STRING);
            after.setCellValue("修改后信息");
            Cell operaUserName = head.createCell(6);
            operaUserName.setCellStyle(style);
            operaUserName.setCellType(XSSFCell.CELL_TYPE_STRING);
            operaUserName.setCellValue("操作人");
            Cell operaTime = head.createCell(7);
            operaTime.setCellStyle(style);
            operaTime.setCellType(XSSFCell.CELL_TYPE_STRING);
            operaTime.setCellValue("操作时间");
            //表头创建完成
            for (int i = 0; i < list.size(); i++) {
                AccountModifyListVo vo = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(vo.getOwnerName());
                row.createCell(2).setCellValue(vo.getShowType());
                row.createCell(3).setCellValue(vo.getAccount());
                row.createCell(4).setCellValue(vo.getBefore().toJSONString());
                row.createCell(5).setCellValue(vo.getAfter().toJSONString());
                row.createCell(6).setCellValue(vo.getOperatorName());
                row.createCell(7).setCellValue(vo.getOperatorTime());
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            byte[] bytes = os.toByteArray();
            return bytes;
        } catch (Exception e) {
            ApiLogger.error("资料修改记录列表失败！data size:".concat(String.valueOf(10)).concat(",fileName:").concat(filename), e);
            return null;
        }
    }

    /**
     * @param list
     * @param filename
     * @return
     * @Doc 组装子账号列表导出数据
     */
    public static byte[] workerListExport(List<WorkerVo> list, String filename) {
        ApiLogger.info("开始生成Excel!fileName:".concat(filename));
        try {
            //创建Excel工作薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            //创建工作表，可指定工作表名称
            Sheet sheet = workbook.createSheet();
            //工作表表头
            Row head = sheet.createRow(0);
            //设置居中
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            //head.setRowStyle(style);
            //创建单元格,并设置单元格格式和内容
            Cell id = head.createCell(0);
            id.setCellStyle(style);
            id.setCellType(XSSFCell.CELL_TYPE_STRING);
            id.setCellValue("序号");
            Cell accunt = head.createCell(1);
            accunt.setCellStyle(style);
            accunt.setCellType(XSSFCell.CELL_TYPE_STRING);
            accunt.setCellValue("子账号");
            Cell realName = head.createCell(2);
            realName.setCellStyle(style);
            realName.setCellType(XSSFCell.CELL_TYPE_STRING);
            realName.setCellValue("真实姓名");
            Cell roleName = head.createCell(3);
            roleName.setCellStyle(style);
            roleName.setCellType(XSSFCell.CELL_TYPE_STRING);
            roleName.setCellValue("所属角色");
            Cell showStatus = head.createCell(4);
            showStatus.setCellStyle(style);
            showStatus.setCellType(XSSFCell.CELL_TYPE_STRING);
            showStatus.setCellValue("状态");
            Cell createTime = head.createCell(5);
            createTime.setCellStyle(style);
            createTime.setCellType(XSSFCell.CELL_TYPE_STRING);
            createTime.setCellValue("创建时间");
            Cell lastLoginTime = head.createCell(6);
            lastLoginTime.setCellStyle(style);
            lastLoginTime.setCellType(XSSFCell.CELL_TYPE_STRING);
            lastLoginTime.setCellValue("最后登录时间");
            //表头创建完成
            for (int i = 0; i < list.size(); i++) {
                WorkerVo vo = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(vo.getAccount());
                row.createCell(2).setCellValue(vo.getRealname());
                row.createCell(3).setCellValue(vo.getRoleName());
                row.createCell(4).setCellValue(vo.getShowStatus());
                row.createCell(5).setCellValue(vo.getCreateTime());
                row.createCell(6).setCellValue(vo.getLastLoginTime());
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            byte[] bytes = os.toByteArray();
            return bytes;
        } catch (Exception e) {
            ApiLogger.error("子账号列表导出失败！data size:".concat(String.valueOf(list.size())).concat(",fileName:").concat(filename), e);
            return null;
        }
    }


    /**
     * 组装导出excel的文件名
     *
     * @param uid
     * @param name
     * @return
     */
    public static String assembleFileName(long uid, String name) {
        StringBuilder filename = new StringBuilder();
        filename.append(LocalDateTimeUtil.toAmerica(System.currentTimeMillis(), LocalDateTimeUtil.YYYYMMDD));
        filename.append("(");
        filename.append(uid);
        filename.append("-");
        filename.append(name);
        filename.append(")");
        filename.append(".xlsx");
        return filename.toString();
    }
}
