package com.zbsp.wepaysp.common.util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class EAN13CodeUtil {

    private EAN13CodeUtil() {
    }

    /**
     * 生成条形码至指定路径
     * 
     * @param content
     *            条形码码内容
     * @param path
     *            生成路径
     * @param fileName
     *            文件名
     * @throws IOException
     * @throws WriterException
     */
    public static void writeToFile(String content, String path, String fileName) throws IOException, WriterException {
        Map<EncodeHintType, String> hints = new HashMap<EncodeHintType, String>();
        // 内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_39, 250, 50, hints);

        path+="/";
       // MatrixToImageWriter.writeToFile(bitMatrix, "png", new File(path + fileName + ".png"));
        MatrixToImageWriter.writeToPath(bitMatrix, "png", Paths.get(path, fileName));
    }
    
    public static void main(String[] args) throws IOException, WriterException {
        // 益达无糖口香糖的条形码
        //String contents = "6923450657713";
        
        String contents = "201701090000000004";
        EAN13CodeUtil.writeToFile(contents, "C:/Users/mengzh/Desktop", "zxing_EAN13.png");
        System.out.println("Michael ,you have finished zxing EAN13 encode.");
    }

}
