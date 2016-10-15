/*
 * ContentTemplate.java
 * 创建者：杨帆
 * 创建日期：2013-8-28
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.common.util;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * 内容模版工具.
 * 模版文件为Velocity模版.
 * 
 * @author 杨帆
 */
public final class TemplateUtil {
    
    private VelocityEngine templateEngine;
    
    /**
     * 对模版文件中的内容进行替换.
     * 
     * @param templateFilePath 模版文件所在路径
     * @param encode 模版文件编码格式
     * @param keyValueMap 替换内容的键值对
     * @return 替换后的内容
     */
    public String mergeTemplate(String templateFilePath, String encode, Map<String, Object> keyValueMap){
        if(templateEngine == null){
            return null;
        }
        
        StringWriter writer = new StringWriter();
        
        VelocityContext context = new VelocityContext();
        
        if(keyValueMap != null && keyValueMap.size() > 0){
            Iterator<String> keyIterator = keyValueMap.keySet().iterator();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                context.put(key, keyValueMap.get(key));
            }
        }
        
        templateEngine.mergeTemplate(templateFilePath, encode, context, writer);
        
        return writer.toString();
    }

    public VelocityEngine getTemplateEngine() {
        return templateEngine;
    }
    
    public void setTemplateEngine(VelocityEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
}
