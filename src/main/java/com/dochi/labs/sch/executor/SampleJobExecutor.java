package com.dochi.labs.sch.executor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.CronExpression;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SampleJobExecutor implements Job {
    
    private final Logger LOGGER = LoggerFactory.getLogger(SampleJobExecutor.class);
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	JobDetail jobDetail = context.getJobDetail();
        JobKey jobKey = jobDetail.getKey();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        
        Trigger trigger = context.getTrigger();
        TriggerKey triggerKey = trigger.getKey();
        
        Scheduler scheduler = context.getScheduler();
        
        TriggerState triggerState = null;
        try {
        	triggerState = scheduler.getTriggerState(triggerKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
        
        LOGGER.info("==========================");
        LOGGER.info(String.format("%-10s: %s", "state", triggerState.toString() ));
        LOGGER.info(String.format("%-10s: %s", "jobKey", jobKey.toString() ));
        LOGGER.info(String.format("%-10s: %s", "trigger", triggerKey.toString() ));
        jobDataMap.forEach((key, value)->{
        	if( value instanceof String ) {
                LOGGER.info(String.format("%-10s: %s", key, (String)value ));
        	} else if ( value instanceof CronExpression ) {
            	Date nextDate = ((CronExpression) value).getNextValidTimeAfter(new Date());
                LOGGER.info(String.format("%-10s: %s", "nextTime", new SimpleDateFormat("yyyyMMddhhmmss.SSSS").format(nextDate)));
        	}
        });
        LOGGER.info("==========================");
        
        try {
			Thread.sleep(12000);
			
			System.out.println("Finish!!!!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
    }
}
