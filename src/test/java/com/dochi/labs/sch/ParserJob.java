package com.dochi.labs.sch;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ParserJob extends ChainJob {
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        
        String nextStepName = jobDataMap.getString(NEXT_STEP_NAME);
        
        JobExecutionContext rootCtx = (JobExecutionContext) jobDataMap.get("ROOT");
        
        println(String.format("[%s][%s]", rootCtx.getTrigger().getKey().getName(), nextStepName));
    }
}
