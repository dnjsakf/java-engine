package com.dochi.labs.sch.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleJob implements Job {
    
    private final Logger LOGGER = LoggerFactory.getLogger(SampleJob.class);
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        
        LOGGER.info("==========================");
        jobDataMap.forEach((key, value)->{
            LOGGER.info(String.format("%-10s: %s", key, (String)value ));
        });
        LOGGER.info("==========================");
    }
}
