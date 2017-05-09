package com.magic.user.util;

import com.magic.api.commons.ApiLogger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;

/**
 * ExcelUtil
 * Excel工具
 * @author zj
 * @date 2015/12/4
 */
public class ExcelUtil {
    /**
     * 生成Excel
     * @param data
     * @param filename
     */
    public static byte[] listToExcel(List<OrderDetailReport> data,String filename){
        ApiLogger.info("开始生成Excel!fileName:".concat(filename));
        try{
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
            Cell orderID = head.createCell(0);
            orderID.setCellStyle(style);
            orderID.setCellType(XSSFCell.CELL_TYPE_STRING);
            orderID.setCellValue("订单编号");
            Cell orderType = head.createCell(1);
            orderType.setCellStyle(style);
            orderType.setCellType(XSSFCell.CELL_TYPE_STRING);
            orderType.setCellValue("业务类型");
            Cell paymentNo = head.createCell(2);
            paymentNo.setCellStyle(style);
            paymentNo.setCellType(XSSFCell.CELL_TYPE_STRING);
            paymentNo.setCellValue("支付宝或微信交易号");
            Cell transId = head.createCell(3);
            transId.setCellStyle(style);
            transId.setCellType(XSSFCell.CELL_TYPE_STRING);
            transId.setCellValue("交易ID");
            Cell userId = head.createCell(4);
            userId.setCellStyle(style);
            userId.setCellType(XSSFCell.CELL_TYPE_STRING);
            userId.setCellValue("用户ID");
            Cell userName = head.createCell(5);
            userName.setCellStyle(style);
            userName.setCellType(XSSFCell.CELL_TYPE_STRING);
            userName.setCellValue("用户名称");
            Cell merchantId = head.createCell(6);
            merchantId.setCellStyle(style);
            merchantId.setCellType(XSSFCell.CELL_TYPE_STRING);
            merchantId.setCellValue("商户ID");
            Cell merchantName = head.createCell(7);
            merchantName.setCellStyle(style);
            merchantName.setCellType(XSSFCell.CELL_TYPE_STRING);
            merchantName.setCellValue("商户名称");
            Cell goodsId = head.createCell(8);
            goodsId.setCellStyle(style);
            goodsId.setCellType(XSSFCell.CELL_TYPE_STRING);
            goodsId.setCellValue("商品ID");
            Cell goodsName = head.createCell(9);
            goodsName.setCellStyle(style);
            goodsName.setCellType(XSSFCell.CELL_TYPE_STRING);
            goodsName.setCellValue("商品名称");
            Cell paymentType = head.createCell(10);
            paymentType.setCellStyle(style);
            paymentType.setCellType(XSSFCell.CELL_TYPE_STRING);
            paymentType.setCellValue("支付方式");
            Cell price = head.createCell(11);
            price.setCellStyle(style);
            price.setCellType(XSSFCell.CELL_TYPE_STRING);
            price.setCellValue("用户支付金额");
            Cell time = head.createCell(12);
            time.setCellStyle(style);
            time.setCellType(XSSFCell.CELL_TYPE_STRING);
            time.setCellValue("时间");
            Cell orderStatus = head.createCell(13);
            orderStatus.setCellStyle(style);
            orderStatus.setCellType(XSSFCell.CELL_TYPE_STRING);
            orderStatus.setCellValue("支付订单状态");
            //表头创建完成
            for(int i=0 ;i<data.size();i++){
                OrderDetailReport report = data.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(report.getOrderId());
                row.createCell(1).setCellValue(report.getOrderType());
                row.createCell(2).setCellValue(report.getPaymentNo());
                row.createCell(3).setCellValue(report.getTransId());
                row.createCell(4).setCellValue(report.getUserId());
                row.createCell(5).setCellValue(report.getUserName());
                row.createCell(6).setCellValue(report.getMerchantId());
                row.createCell(7).setCellValue(report.getMerchantName());
                row.createCell(8).setCellValue(report.getGoodsId());
                row.createCell(9).setCellValue(report.getGoodsName());
                row.createCell(10).setCellValue(report.getPaymentType());
                row.createCell(11).setCellValue(report.getPrice());
                row.createCell(12).setCellValue(report.getPaymentTime());
                row.createCell(13).setCellValue(report.getOrderStatus());
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            byte[] bytes = os.toByteArray();
            return bytes;
        }catch (Exception e){
            ApiLogger.error("生成订单详细表失败！data size:".concat(String.valueOf(data.size())).concat(",fileName:").concat(filename), e);
            return null;
        }
    }
}
