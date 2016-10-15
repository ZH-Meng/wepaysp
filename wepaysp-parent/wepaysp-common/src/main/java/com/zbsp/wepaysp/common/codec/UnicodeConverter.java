package com.zbsp.wepaysp.common.codec;

import java.lang.Character.UnicodeBlock;

public class UnicodeConverter {

    public static String utf8ToUnicode(String str) {
        char[] myBuffer = str.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
            if (ub == UnicodeBlock.BASIC_LATIN) {
                // 英文及数字等
                sb.append(myBuffer[i]);
            } else {
                // 汉字
                String hexS = Integer.toHexString(myBuffer[i]);
                String unicode = "\\" + 'u' + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }
}
