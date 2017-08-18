/*
 * ExcelUtils.java
 * 创建者：李成帅
 * 创建日期：2016年7月14日 下午3:06:33
 *
 * 版权所有(C) 2015-2018。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.util;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/**
 * 处理Excel数据
 * @author 李成帅
 * @date： 2016年7月14日 下午3:06:33
 */
public class ExcelHandle {
    
    /**
     * 取得指定单元格行和列
     * @param keyMap 所有单元格行、列集合
     * @param key 单元格标识
     * @return 0：列 1：行（列表型数据不记行，即1无值）
     */
    public static int[] getPosition(HashMap<String, Object> keyMap, String key){
        int[] ret = new int[0];
         
        String val = (String)keyMap.get(key);
         
        if(val == null || val.length() == 0)
            return ret;
         
        String pos[] = val.split(",");
         
        if(pos.length == 1 || pos.length == 2){
            ret = new int[pos.length];
            for(int i0 = 0; i0 < pos.length; i0++){
                if(pos[i0] != null && pos[i0].trim().length() > 0){
                    ret[i0] = Integer.parseInt(pos[i0].trim());
                } else {
                    ret[i0] = 0;
                }
            }
        }
        return ret;
    }
     
    /**
     * 取对应格子的值
     * @param sheet
     * @param rowNo 行
     * @param cellNo 列
     * @return
     * @throws IOException
     */
    public static String getCellValue(Sheet sheet,int rowNo,int cellNo) {
        String cellValue = null;
        Row row = sheet.getRow(rowNo);
        
        if(row != null){
            Cell cell = row.getCell(cellNo);
            if (cell != null) {
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    DecimalFormat df = new DecimalFormat("0");
                    cellValue = getCutDotStr(df.format(cell.getNumericCellValue()));
                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    cellValue = cell.getStringCellValue();
                }
                if (cellValue != null) {
                    cellValue = cellValue.trim();
                }           
            }
        }
        return cellValue;
    }
      
    /**
     * 取整数
     * @param srcString
     * @return
     */
    private static String getCutDotStr(String srcString) {
        String newString = "";
        if (srcString != null && srcString.endsWith(".0")) {
            newString = srcString.substring(0,srcString.length()-2);
        } else {
            newString = srcString;
        }
        return newString;
    }   
     
    /**
     * 读取模板数据
     * @param tempWorkBook 模板
     * @throws IOException
     */
    public static List<HashMap<String, Object>> getTempDataMapList(Workbook tempWorkBook){  
        List<HashMap<String, Object>> templateMap = new ArrayList<HashMap<String, Object>>();
        if(tempWorkBook != null){
            int sheetNumber = tempWorkBook.getNumberOfSheets();
            
            for(int i = 0; i < sheetNumber; i++){
                Sheet sheet = tempWorkBook.getSheetAt(i);
                HashMap<String, Object> sheetMap = new HashMap<String, Object>();
                readSheet(sheetMap, sheet);
                templateMap.add(sheetMap);
            }
        }
       
        return templateMap;
    }
     
    /**
     * 读模板数据的样式值置等信息
     * @param keyMap
     * @param sheet
     */
    @SuppressWarnings("unchecked")
    private static void readSheet(HashMap<String, Object> keyMap, Sheet sheet){
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        
        for (int j = firstRowNum; j <= lastRowNum; j++) {
            Row rowIn = sheet.getRow(j);
            if(rowIn == null) {
                continue;
            }
            int firstCellNum = rowIn.getFirstCellNum();
            int lastCellNum = rowIn.getLastCellNum();
            for (int k = firstCellNum; k <= lastCellNum; k++) {
                Cell cellIn = rowIn.getCell(k);
                if(cellIn == null) {
                    continue;
                }
                 
                int cellType = cellIn.getCellType();
                if(Cell.CELL_TYPE_STRING != cellType) {
                    continue;
                }
                String cellValue = cellIn.getStringCellValue();
                if(cellValue == null) {
                    continue;
                }
                cellValue = cellValue.trim();
                if(cellValue.length() > 2 && cellValue.substring(0,2).equals("$$")) {
                    String key = cellValue.substring(2, cellValue.length());
                    String keyPos = Integer.toString(k)+","+Integer.toString(j);
                    keyMap.put(key, keyPos);
                    keyMap.put(key+"CellStyle", cellIn.getCellStyle());
                    
                    List<String> conditionList = null;
                    if(!keyMap.containsKey("termNameList")){
                        conditionList = new ArrayList<String>();//条件单元格占位符名称集合
                    }else{
                        conditionList = (List<String>) keyMap.get("termNameList");
                    }
                    conditionList.add(key);
                    keyMap.put("termNameList", conditionList);
                } else if(cellValue.length() > 3 && cellValue.substring(0,3).equals("$0$")) {
                    int num = 0;
                    setKeyMap(keyMap, cellValue, cellIn, k, j, num);
                } else if(cellValue.length() > 3 && cellValue.substring(0,3).equals("$1$")) {
                    int num = 1;
                    setKeyMap(keyMap, cellValue, cellIn, k, j, num);
                } else if(cellValue.length() > 3 && cellValue.substring(0,3).equals("$2$")) {
                    int num = 2;
                    setKeyMap(keyMap, cellValue, cellIn, k, j, num);
                } else if(cellValue.length() > 3 && cellValue.substring(0,3).equals("$3$")) {
                    int num = 3;
                    setKeyMap(keyMap, cellValue, cellIn, k, j, num);
                } else if(cellValue.length() > 3 && cellValue.substring(0,3).equals("$4$")) {
                    int num = 4;
                    setKeyMap(keyMap, cellValue, cellIn, k, j, num);
                } else if(cellValue.length() > 3 && cellValue.substring(0,3).equals("$5$")) {
                    int num = 5;
                    setKeyMap(keyMap, cellValue, cellIn, k, j, num);
                }
                
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void setKeyMap(HashMap<String, Object> keyMap,String cellValue,Cell cellIn,int k,int j,int num){
        String key = cellValue.substring(3, cellValue.length());
        keyMap.put("STARTROW"+num, Integer.toString(j));
        keyMap.put(key, Integer.toString(k));
        keyMap.put(key+"CellStyle", cellIn.getCellStyle());
        
        List<String> resultNameList = null;
        if(!keyMap.containsKey("resultNameList"+num)){
            resultNameList = new ArrayList<String>(); //表格占位符名称集合
        }else{
            resultNameList = (List<String>) keyMap.get("resultNameList"+num);
        }
        resultNameList.add(key);
        keyMap.put("resultNameList"+num, resultNameList);
    }
    /**
     * 获取格式
     * @param keyMap
     * @param key
     * @return
     */
    public static CellStyle getStyle(HashMap<String, Object> keyMap, String key,Workbook tempWorkBook) {
        CellStyle cellStyle = null;      
         
        cellStyle = (CellStyle) keyMap.get(key+"CellStyle");
        //当字符超出时换行
        cellStyle.setWrapText(true);
        CellStyle newStyle = tempWorkBook.createCellStyle();
        newStyle.cloneStyleFrom(cellStyle);
        return newStyle;
    }
    
    /**
     * Excel单元格输出
     * @param sheet
     * @param row 行
     * @param cell 列
     * @param value 值
     * @param cellStyle 样式
     * @param oldRanges 所有合并的单元格
     */
    public static void setResultValue(Sheet sheet, int row,int cell, Object value, CellStyle cellStyle,List<CellRangeAddress> rowRangs, boolean createrRow){
        Row rowIn = sheet.getRow(row);
        
        if(createrRow == true) {
            rowIn = sheet.createRow(row);
            
            if(rowRangs != null && rowRangs.size()>0){
                for(CellRangeAddress range : rowRangs){
                    int firstColumn = range.getFirstColumn();
                    int lastColumn = range.getLastColumn();
                    
                    CellRangeAddress newRange = new CellRangeAddress(row, row,firstColumn, lastColumn);
                    sheet.addMergedRegion(newRange);
                    
                    Cell cellFirstRange = rowIn.createCell(firstColumn);
                    cellFirstRange.setCellStyle(cellStyle);
                    Cell cellLastRange = rowIn.createCell(lastColumn);
                    cellLastRange.setCellStyle(cellStyle);
                }
            }
        }
        
        Cell cellIn = rowIn.getCell(cell);
        if(cellIn == null) {
            cellIn = rowIn.createCell(cell);
        }
        if(cellStyle != null) {  
            cellStyle.setWrapText(false);// 不换行
            cellIn.setCellStyle(cellStyle);
        }
        
        if(value==null){
            cellIn.setCellValue("");
        }else{
            if (isCellDateFormatted(cellStyle)) {//对时间格式进行单独处理
                cellIn.setCellValue((Date) value);
            } else {
                if(value instanceof Integer || value instanceof Double || value instanceof Float || value instanceof Long){
                    cellIn.setCellValue(Double.parseDouble(value.toString())); 
                } else if(value instanceof Date){
                    cellIn.setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value));
                }else{
                    cellIn.setCellValue(new XSSFRichTextString(value.toString()));
                }
            }
        }
    }
    
    /**
     * Excel单元格输出
     * @param sheet
     * @param row 行
     * @param cell 列
     * @param value 值
     * @param cellStyle 样式
     */
    public static void setValue(Sheet sheet, int row, int cell, Object value, CellStyle cellStyle){
        Row rowIn = sheet.getRow(row);
        if(rowIn == null) {
            rowIn = sheet.createRow(row);
        }
        
        Cell cellIn = rowIn.getCell(cell);
        if(cellIn == null) {
            cellIn = rowIn.createCell(cell);
        }
        if(cellStyle != null) {  
            cellIn.setCellStyle(cellStyle);
        }
        
        if(value==null){
            cellIn.setCellValue("");
        }else{
            if (isCellDateFormatted(cellStyle)) {//对时间格式进行单独处理
                cellIn.setCellValue((Date) value);
            } else {
                if(value instanceof Integer || value instanceof Double || value instanceof Float || value instanceof Long){
                    cellIn.setCellValue(Double.parseDouble(value.toString())); 
                }else{
                    cellIn.setCellValue(new XSSFRichTextString(value.toString()));
                }
            }
        }
    }
     
    /**
     * 根据表格样式判断是否为日期格式
     * @param cellStyle
     * @return
     */
    public static boolean isCellDateFormatted(CellStyle cellStyle){
        if(cellStyle == null){
            return false;
        }
        int i = cellStyle.getDataFormat();
        String f = cellStyle.getDataFormatString();
        
        return DateUtil.isADateFormat(i, f);
    }
    
    /**
     * 适用于导出的数据Excel格式样式重复性较少
     * 不适用于循环方法中使用
     * @param wbModule
     * @param sheet
     * @param pos 模板文件信息
     * @param startRow 开始的行
     * @param value 要填充的数据
     * @param cellStyle 表格样式
     */
    public static void createCell(Workbook wbModule, Sheet sheet,HashMap<String, Object> pos, int startRow,Object value,String cellStyle){
        int[] excelPos = getPosition(pos, cellStyle);
        setValue(sheet, startRow, excelPos[0], value, getStyle(pos, cellStyle,wbModule));
    }
}
