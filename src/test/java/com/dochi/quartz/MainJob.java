package com.dochi.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MainJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().getName();
        
        System.out.println(String.format("[%s][%s] Running...", this.getClass().getName(), jobName));
    }
}
