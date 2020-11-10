package com.dochi.labs.sch;

import java.util.UUID;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CrawlerJob extends ChainJob {
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        String nextStep = jobDataMap.getString("NEXT_STEP");
        String nextStepName = UUID.randomUUID().toString();
        
        println(String.format("[%s][%s]", context.getTrigger().getKey().getName(), nextStepName));
        
        if( nextStep != null ) {
            jobDataMap.put(NEXT_STEP_NAME, nextStepName);
            
            try {
                Thread.sleep(1234);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            runNextStep(context);
        }
    }
}
