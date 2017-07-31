package com.zbsp.wepaysp.manage.web.util;


import java.io.File;
import java.io.FileInputStream;  
import java.util.ArrayList;  
import java.util.Iterator;  
import java.util.Map;
  
import org.apache.poi.xssf.usermodel.XSSFSheet;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook;  
  
/** 
 * Excel 读取（2007+新格式） 
 * @author  shibeilei  
 * @date    2015-10-2
 * @note    PoiExcel2k7Helper 
 */  
public class PoiExcel2k7Helper extends PoiExcelHelper {  
    /** 获取sheet列表 */  
    public ArrayList<String> getSheetList(String filePath) {  
        ArrayList<String> sheetList = new ArrayList<String>(0);  
        try {  
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));  
            Iterator<XSSFSheet> iterator = wb.iterator();  
            while (iterator.hasNext()) {  
                sheetList.add(iterator.next().getSheetName());  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return sheetList;  
    }  
  
    /** 读取Excel文件内容 */  
    public ArrayList<ArrayList<String>> readExcel(String filePath, int sheetIndex, String rows, String columns,int startRow) {  
        ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>> ();  
        try {  
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));  
            XSSFSheet sheet = wb.getSheetAt(sheetIndex);  
            if(sheet.getRow(sheet.getLastRowNum())!=null)
            	 dataList = readExcel(sheet, rows, getColumnNumber(sheet, columns,startRow)); 
            
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return dataList;  
    }  
    
    /** 读取Excel文件内容 */  
    public ArrayList<ArrayList<String>> readExcel(File file, int sheetIndex, String rows, String columns,int startRow) {  
        ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>> ();  
        try {  
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));  
            XSSFSheet sheet = wb.getSheetAt(sheetIndex);  
            if(sheet.getRow(sheet.getLastRowNum())!=null)
            	 dataList = readExcel(sheet, rows, getColumnNumber(sheet, columns,startRow)); 
            
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return dataList;  
    }  
    
    /** 读取Excel文件内容   MAP*/  
    public ArrayList<Map<String,String>> readExcelMap(String filePath, int sheetIndex, String title[],String rows, String columns,int startRow) {  
        ArrayList<Map<String,String>> dataList = new ArrayList<Map<String,String>> ();  
        try {  
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));  
            XSSFSheet sheet = wb.getSheetAt(sheetIndex);  
            if(sheet.getRow(sheet.getLastRowNum())!=null)
            	 dataList = readExcelMap(sheet, rows, getColumnNumber(sheet, columns,startRow),title); 
            
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return dataList;  
    }  
      
    /** 读取Excel文件内容 */  
    public ArrayList<ArrayList<String>> readExcel(String filePath, int sheetIndex, String rows, int[] cols) {  
        ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>> ();  
        try {  
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));  
            XSSFSheet sheet = wb.getSheetAt(sheetIndex);  
            if(sheet.getRow(sheet.getLastRowNum())!=null)
            	dataList = readExcel(sheet, rows, cols);  
            
            
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return dataList;  
    }  
    /** 读取Excel文件内容 Map */  
    public ArrayList<Map<String,String>> readExcelMap(String filePath, int sheetIndex,String title[], String rows, int[] cols,int startRow) {  
        ArrayList<Map<String,String>> dataList = new ArrayList<Map<String,String>> ();  
        try {  
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));  
            XSSFSheet sheet = wb.getSheetAt(sheetIndex);  
            if(sheet.getRow(sheet.getLastRowNum())!=null)
            	dataList = readExcelMap(sheet, rows, cols,title);  
            
            
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return dataList;  
    }  
}  
