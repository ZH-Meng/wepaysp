package com.zbsp.wepaysp.api.hibernate.dialect;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StringType;

/**
 * 自定义mysql 方言，注册获取序列 当前值和下一值的函数
 * 
 * @author 孟郑宏
 */
public class WepayDialect
    extends MySQL5Dialect {

    public WepayDialect() {
        super();
        // 函数名必须是小写，试验大写出错
        // SQLFunctionTemplate函数第一个参数是函数的输出类型，varchar2对应new StringType() number对应new IntegerType()
        // ?1代表第一个参数,?2代表第二个参数 这是数据库nextval函数只需要一个参数，所以写成nextval(?1)
        this.registerFunction("nextval", new SQLFunctionTemplate(new StringType(), "nextval(?1)"));
        this.registerFunction("currval", new SQLFunctionTemplate(new StringType(), "currval(?1)"));
    }
}
