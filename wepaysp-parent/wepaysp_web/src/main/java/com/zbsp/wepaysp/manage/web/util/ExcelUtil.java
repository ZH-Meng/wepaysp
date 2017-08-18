/*
 * ExcelHandle.java
 * 创建者：李成帅
 * 创建日期：2016年7月14日 下午3:08:36
 *
 * 版权所有(C) 2015-2018。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 对excel进行操作工具类
 * @author 李成帅
 * @date： 2016年7月14日 下午3:08:36
 */
public class ExcelUtil {
    protected Logger logger = LogManager.getLogger(getClass());
    private static final String LOG_PREFIX = "[ExcelUtil]-";
    
    /**
     * 向Excel中输入相同title的多条数据
     * @param temWorkbook 模板数据版
     * @param tempData 模板数据占位符相关数据
     * @param cellList 需要填充的数据（模板$0$后的字符串）
     * @param dataList 填充的数据
     * @param sheet 填充的excel sheet,从0开始
     * @throws IOException 
     */
    @SuppressWarnings("unchecked")
    private void writeResultData(Workbook tempWorkBook, HashMap<String, Object> tempData, List<?> dataList,int sheet, boolean moreTableFlag){
        //数据填充的sheet
        Sheet wsheet = tempWorkBook.getSheetAt(sheet);
        
        if(dataList != null && dataList.size() > 0){
            //判断结果集中是一个表格还是多个表格
            if(moreTableFlag){
                //所有表格数据下移
                int allRowNum = 0;
                for(int i = 0 ;i<dataList.size();i++){
                    String tempKey = "STARTROW"+(i+1);
                    if(tempData.get(tempKey) != null){
                        //获取下一行的行号
                        int rowNum = Integer.parseInt(tempData.get(tempKey).toString())+allRowNum;
                        tempData.put(tempKey, rowNum);
                    }
                    
                    //没有数据合并单元格
                    int startRow = Integer.parseInt(tempData.get("STARTROW"+i).toString());
                    if(dataList.get(i) == null || ((List<?>) dataList.get(i)).size() < 1){
                        addMergedRegion(wsheet, startRow, tempData, i);
                        continue;
                    }
                    
                    if(tempData.get(tempKey) != null && dataList.get(i) != null && ((List<?>) dataList.get(i)).size() > 0){
                        //获取下一行的行号
                        int rowNum = Integer.parseInt(tempData.get(tempKey).toString());
                        int size = ((List<?>) dataList.get(i)).size()-1;
                        
                        if(size < 1 ){
                            continue;
                        }
                        
                        int last = wsheet.getLastRowNum();
                        //startRow、endRow可以指定一个待移动的区域，n 表示要移动的行数
                        wsheet.shiftRows(rowNum-3, last, size, true, false);
                        allRowNum += size;
                        tempData.put(tempKey, rowNum+size);
                    }
                }
                
                for(int i = 0 ;i<dataList.size();i++){
                    //获取数据填充开始行
                    int startRow = Integer.parseInt(tempData.get("STARTROW"+i).toString());
                    
                    List<?> list = (List<?>) dataList.get(i); 
                    
                    if(list == null || list.size() < 1 ){
                        continue;
                    }
                    
                    List<CellRangeAddress> rowRangs = new ArrayList<CellRangeAddress>();
                    //获取所有合并的单元格
                    for (int j=0; j<wsheet.getNumMergedRegions();j++) {
                        CellRangeAddress range = wsheet.getMergedRegion(j);
                        if(range.getFirstRow() == startRow){
                            rowRangs.add(range);
                        }
                    }
                    
                    List<String> cellList = (List<String>) tempData.get("resultNameList"+i);
                    
                    for(int k = 0; k<list.size(); k++){
                        //是否需要新建一行
                        boolean createrRow = true;
                        
                        //第一行不需要创建行和合并单元格
                        if(k == 0){
                            createrRow = false;
                        }
                        
                        for(String cell:cellList){
                            //获取对应单元格数据
                            Cell c = getCell(cell,tempData,tempWorkBook);
                            
                            Object value = null;
                            if("indexNumber".equals(cell)){
                                value = (k+1);
                            }else{
                                value = getFieldValueByChainedMode(cell, list.get(k));
                            }
                            
                            //写入数据
                            ExcelHandle.setResultValue(wsheet, startRow, c.getColumn(), value, c.getCellStyle(),rowRangs,createrRow);
                            createrRow = false;
                        }
                        startRow++;
                    }
                }
            }else{
                //获取数据填充开始行
                int startRow = Integer.parseInt(tempData.get("STARTROW0").toString());
                
                List<CellRangeAddress> rowRangs = new ArrayList<CellRangeAddress>();
                //获取所有合并的单元格
                for (int j=0; j<wsheet.getNumMergedRegions();j++) {
                    CellRangeAddress range = wsheet.getMergedRegion(j);
                    if(range.getFirstRow() == startRow){
                        rowRangs.add(range);
                    }
                }
                
                List<String> cellList = (List<String>) tempData.get("resultNameList0");
                
                for(int i = 0 ;i<dataList.size();i++){
                  //是否需要新建一行
                    boolean createrRow = true;
                    
                    //第一行不需要创建行和合并单元格
                    if(i == 0){
                        createrRow = false;
                    }
                    
                    for(String cell:cellList){
                        //获取对应单元格数据
                        Cell c = getCell(cell,tempData,tempWorkBook);
                        
                        Object value = null;
                        if("indexNumber".equals(cell)){
                            value = (i+1);
                        }else{
                            value = getFieldValueByChainedMode(cell, dataList.get(i));
                        }
                        
                        //写入数据
                        ExcelHandle.setResultValue(wsheet, startRow, c.getColumn(), value, c.getCellStyle(),rowRangs,createrRow);
                        createrRow = false;
                    }
                    startRow++;
                }
            }
        }else{
            //单表格没有数据时处理
            int startRow = Integer.parseInt(tempData.get("STARTROW0").toString());
            addMergedRegion(wsheet, startRow, tempData, 0);
        }
    }

    private void removeMergedRegion(Sheet wsheet,int startRow, int cellCount) throws Exception{
        
        for(int i = 0; i< wsheet.getNumMergedRegions(); i++ ){
            CellRangeAddress range = wsheet.getMergedRegion(i);
            if(range.getFirstRow() == startRow){
                wsheet.removeMergedRegion(i);
                cellCount += range.getLastColumn() - range.getFirstColumn();
                removeMergedRegion(wsheet,startRow,cellCount);
            }
        }
        throw new Exception(cellCount+"");
    }
    
    @SuppressWarnings("unchecked")
    private void addMergedRegion(Sheet wsheet,int startRow,HashMap<String, Object> tempData,int num){
        List<String> cellList = (List<String>) tempData.get("resultNameList"+num);
        
        int count = 0;
        
        try {
            removeMergedRegion(wsheet,startRow,count);
        } catch (Exception e) {
            count = Integer.parseInt(e.getMessage());
        }
        
        count += cellList.size() - 1;
        
        CellStyle cellStyle = (CellStyle) tempData.get(cellList.get(0)+"CellStyle");
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        
        Row rowIn = wsheet.getRow(startRow);
        
        rowIn.getCell(0).setCellValue(new XSSFRichTextString("没有符合条件的查询结果。"));
        rowIn.getCell(0).setCellStyle(cellStyle);
        
        CellRangeAddress region = new CellRangeAddress(startRow, startRow, 0, count);
        wsheet.addMergedRegion(region);
        
    }

    /**
     * 按模板向Excel中相应地方填充数据
     * @param temWorkbook 模板数据版
     * @param tempData 模板数据占位符相关数据
     * @param cellList 需要填充的数据（模板$$后的字符串）
     * @param dataMap 填充的数据
     * @param sheet 填充的excel sheet,从0开始
     * @throws IOException 
     */
    @SuppressWarnings("unchecked")
    private void writeParamData(Workbook tempWorkBook, HashMap<String, Object> tempData,Map<String,Object> dataMap,int sheet){
        //数据填充的sheet
        Sheet wsheet = tempWorkBook.getSheetAt(sheet);
        
        //获取查询条件占位符名称
        List<String> cellList = (List<String>) tempData.get("termNameList");
        
        if(dataMap!=null&&dataMap.size()>0){
            for(String cell:cellList){
                //获取对应单元格数据
                Cell c = getCell(cell,tempData,tempWorkBook);
                ExcelHandle.setValue(wsheet, c.getLine(), c.getColumn(), dataMap.get(cell), c.getCellStyle());
            }
        }
    }
    
    /**
     * 获得输入工作区
     * @param tempFilePath
     * @return
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private Workbook getTempWorkbook(String tempFilePath){
        Workbook workbook = null;
        InputStream inputStream = null;
        try {
            System.out.println(tempFilePath);
            inputStream = new FileInputStream(tempFilePath);
            System.out.println(inputStream);
            if(tempFilePath.endsWith(".xlsx")){
                workbook = new XSSFWorkbook(inputStream);
            }else if(tempFilePath.endsWith(".xls")){
                workbook = new HSSFWorkbook(inputStream);
            }
        } catch (FileNotFoundException e) {
            logger.error(LOG_PREFIX + "[模板文件不存在]", e);
        } catch (IOException e) {
            logger.error(LOG_PREFIX + "[读取模板数据错误]", e);
        } finally {
            try {
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error(LOG_PREFIX + "[关闭读取模板文件输入流错误]", e);
            }
        }
        return workbook;
    }
    
    /**
     * 获取对应单元格样式等数据数据
     * @param cell
     * @param tem
     * @param wbModule 
     * @param tempFilePath
     * @return
     */
    private Cell getCell(String cellName, HashMap<String, Object> tempData, Workbook tempWorkBook) {
        Cell cell = new Cell();
        
        int[] pos = ExcelHandle.getPosition(tempData, cellName);
        if(pos.length>1){
            cell.setLine(pos[1]);
        }
        cell.setColumn(pos[0]);
        cell.setCellStyle((ExcelHandle.getStyle(tempData, cellName, tempWorkBook)));
        return cell;
    }
    
    /**
     * 获得读取数据工作间
     * @param excelFile 要读取的文件
     * @return
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public Workbook getDataWorkbook(File excelFile){
        Workbook workbook = null;
        InputStream inputStream = null;
        
        if(excelFile == null){
            return null;
        }
        
        try {
            inputStream = new FileInputStream(excelFile);
            
            if(excelFile.getPath().endsWith(".xlsx")){
                workbook = new XSSFWorkbook(inputStream);
            }else if(excelFile.getPath().endsWith(".xls")){
                workbook = new HSSFWorkbook(inputStream);
            }
        } catch (FileNotFoundException e) {
            logger.error(LOG_PREFIX + "[模板文件不存在]", e);
        } catch (IOException e) {
            logger.error(LOG_PREFIX + "[读取模板数据错误]", e);
        } finally {
            try {
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error(LOG_PREFIX + "[关闭读取模板文件输入流错误]", e);
            }
        }
        return workbook;
    }
    
    /** 
     * 使用反射根据属性名称获取属性值  
     * @param  fieldName 属性名称 
     * @param  object 操作对象 
     * @return Object 属性值 
     */  
    private Object getFieldValueByName(String fieldName, Object object)   {      
        try {      
            String firstLetter = fieldName.substring(0, 1).toUpperCase();      
            String getter = "get" + firstLetter + fieldName.substring(1);      
            Method method = object.getClass().getMethod(getter, new Class[] {});      
            Object value = method.invoke(object, new Object[] {});      
            return value;      
        } catch (Exception e) {
            logger.error(LOG_PREFIX + "[反射获取属性值错误]", e);
            return null;      
        }      
    } 
    
    /**
     * 根据链式属性名获取属性值
     * @param declareName
     *        属性名 格式可以为object.property.property
     * @param object
     *        操作对象
     * @return
     *       Object 属性值 
     */ 
    private Object getFieldValueByChainedMode(String declareName, Object object){
        String[] fieldNames = declareName.split("\\.");
        Object value = object;
        for (String fieldName : fieldNames) {
            if(StringUtils.isNotBlank(fieldName))
            value = getFieldValueByName(fieldName,value);
        }
        return value;
    }
    
    /** 
     * 将数据写入Excel文件并生成输入流  
     * @param  dataList 导出的数据
     * @param  tempFilePath 模板路径
     * @param  paramMap 查询条件集合 
     * @param  moreTableFlag 是否是多表格，true、false
     * @return InputStream 导出文件输入流 
     */ 
    public InputStream writeData(List<?> dataList, String tempFilePath, Map<String,Object> paramMap, boolean moreTableFlag){
        int sheet = 0;//默认取第一个sheet的数据
        String sheetName = MapUtils.getString(paramMap, "sheetName");
        if(null != paramMap){
            paramMap.remove("sheetName");
        }
        
        //按模板为写入板
        Workbook tempWorkBook = getTempWorkbook(tempFilePath);
        if(StringUtils.isNotBlank(sheetName)){
            int sheetNum = tempWorkBook.getNumberOfSheets();
            tempWorkBook.setSheetOrder(sheetName, 0);
            for(int i =1 ;i<sheetNum; i++ ){
                tempWorkBook.removeSheetAt(1);
            }
        }
        
        //获取模板所有sheet的数据
        List<HashMap<String, Object>> tempDataMapList = ExcelHandle.getTempDataMapList(tempWorkBook);
        
        if(tempDataMapList == null || tempDataMapList.size()<1){
            return null;
        }
        
        //获取模板填充格式位置等数据
        HashMap<String, Object> tempData = tempDataMapList.get(sheet);
        
        ByteArrayOutputStream outputStream = null;
        InputStream InputStream = null;
        try {
            if(tempWorkBook == null){
                return null;
            }
            //写入条件数据
            writeParamData(tempWorkBook, tempData, paramMap, sheet);
            
            //写入表格数据
            writeResultData(tempWorkBook, tempData, dataList, sheet,moreTableFlag);
            
            outputStream = new ByteArrayOutputStream();
            
            //将填充好的模板数据写到输出流
            tempWorkBook.write(outputStream);
            
            //将输出流转化成输入流
            InputStream = new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            logger.error(LOG_PREFIX + "[写入表格数据错误]", e);
        } finally {
            try {
                if(outputStream != null){
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                logger.error(LOG_PREFIX + "[关闭输出流错误]", e);
            }
        }
        return InputStream;
    }
    
    /**
     * 单元格类
     * @author 李成帅
     *
     */
    private class Cell{
        private int column;//列
        private int line;//行
        private CellStyle cellStyle;

        public int getColumn() {
            return column;
        }
        public void setColumn(int column) {
            this.column = column;
        }
        public int getLine() {
            return line;
        }
        public void setLine(int line) {
            this.line = line;
        }
        public CellStyle getCellStyle() {
            return cellStyle;
        }
        public void setCellStyle(CellStyle cellStyle) {
            this.cellStyle = cellStyle;
        }
    }
}
