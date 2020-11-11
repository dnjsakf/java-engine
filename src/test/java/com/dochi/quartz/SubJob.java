package com.dochi.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SubJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        
        String mainStepJobName = jobDataMap.getString(QuartzLauncher.MAIN_STEP_JOB_NAME);
        String jobName = context.getJobDetail().getKey().getName();
        
        System.out.println(String.format("[%s][%s][%s] Running...", this.getClass().getName(), mainStepJobName, jobName));
        
        try {
            Thread.sleep(10*1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
