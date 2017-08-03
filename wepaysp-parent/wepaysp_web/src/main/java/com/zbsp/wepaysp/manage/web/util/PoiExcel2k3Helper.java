package com.zbsp.wepaysp.manage.web.util;


import java.io.File;
import java.io.FileInputStream;  
import java.util.ArrayList;  
import java.util.Map;
  
import org.apache.poi.hssf.usermodel.HSSFSheet;  
import org.apache.poi.hssf.usermodel.HSSFWorkbook;  
  
/** 
 * Excel 读取（97-2003格式） 
 * @author  shibeilei  
 * @date    2015-10-2
 * @note    PoiExcel2k3Helper 
 */  
public class PoiExcel2k3Helper extends PoiExcelHelper {  
    /** 获取sheet列表 */  
    public ArrayList<String> getSheetList(String filePath) {  
        ArrayList<String> sheetList = new ArrayList<String>(0);  
        try {  
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(filePath));  
            int i = 0;  
            while (true) {  
                try {  
                    String name = wb.getSheetName(i);  
                    sheetList.add(name);  
                    i++;  
                } catch (Exception e) {  
                    break;  
                }  
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
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(filePath));  
            HSSFSheet sheet = wb.getSheetAt(sheetIndex);  
            if(sheet.getLastRowNum()<=0){
            	dataList = new ArrayList<ArrayList<String>> (); 
            }else{
            	 dataList = readExcel(sheet, rows, getColumnNumber(sheet, columns,startRow)); 
            }
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return dataList;  
    }  
    /** 读取Excel文件内容 */  
    public ArrayList<ArrayList<String>> readExcel(File file, int sheetIndex, String rows, String columns,int startRow) {  
        ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>> ();  
        try {  
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));  
            HSSFSheet sheet = wb.getSheetAt(sheetIndex);  
            if(sheet.getLastRowNum()<=0){
            	dataList = new ArrayList<ArrayList<String>> (); 
            }else{
            	 dataList = readExcel(sheet, rows, getColumnNumber(sheet, columns,startRow)); 
            }
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return dataList;  
    }  
      
    /** 读取Excel文件内容 */  
    public ArrayList<ArrayList<String>> readExcel(String filePath, int sheetIndex, String rows, int[] cols) {  
        ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>> ();  
        try {  
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(filePath));  
            HSSFSheet sheet = wb.getSheetAt(sheetIndex);  
            if(sheet.getLastRowNum()<=0){
            	dataList = new ArrayList<ArrayList<String>> (); 
            }else{
            	dataList = readExcel(sheet, rows, cols);  
            }
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return dataList;  
    }

	@Override
	public ArrayList<Map<String, String>> readExcelMap(String filePath,
			int sheetIndex, String[] title, String rows, String columns,int startRow) {
		 ArrayList<Map<String, String>> dataList = new ArrayList<Map<String, String>> ();  
	        try {  
	            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(filePath));  
	            HSSFSheet sheet = wb.getSheetAt(sheetIndex);  
	            if(sheet.getRow(sheet.getLastRowNum())!=null)
	            	 dataList = readExcelMap(sheet, rows, getColumnNumber(sheet, columns,startRow),title); 
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return dataList;  
	}

	@Override
	public ArrayList<Map<String, String>> readExcelMap(String filePath,
			int sheetIndex, String[] title, String rows, int[] cols,int startRow) {
		 ArrayList<Map<String, String>> dataList = new ArrayList<Map<String, String>> ();  
	        try {  
	            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(filePath));  
	            HSSFSheet sheet = wb.getSheetAt(sheetIndex);  
	            if(sheet.getRow(sheet.getLastRowNum())!=null)
	            	dataList = readExcelMap(sheet, rows, cols,title);  
	            
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return dataList;  
	}  
}  

