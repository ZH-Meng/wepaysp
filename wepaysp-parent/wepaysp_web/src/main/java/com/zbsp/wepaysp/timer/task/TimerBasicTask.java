package com.zbsp.wepaysp.timer.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

/**
 * 抽象任务类
 * 
 * @author mengzh
 */
public abstract class TimerBasicTask {
    
    /**日志对象*/
    protected Logger logger = LogManager.getLogger(getClass());
    @Value("${intervalTime}")
    protected Long intervalTime = 600L; // 定时查询间隔时间 单位 秒
    
    protected Boolean IF_JOB_RUN = Boolean.FALSE;
    
    public abstract void doJob();

    public void process() {
        synchronized (IF_JOB_RUN) {
            if (Boolean.TRUE.equals(IF_JOB_RUN)) {
                return;
            } else {
                IF_JOB_RUN = Boolean.TRUE;
            }
        }

        this.doJob();

        synchronized (IF_JOB_RUN) {
            IF_JOB_RUN = Boolean.FALSE;
        }

        return;
    }
}
