package com.dochi.labs.sch;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.ListenerManager;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Quartz Job
 *   for StatefulJob
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SampleJobExecutor implements Job {
    
    private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");
    public static final String EXECUTION_COUNT = "EXECUTION_COUNT";
    
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobDataMap map = ctx.getJobDetail().getJobDataMap();
        String currentDate = TIMESTAMP_FMT.format(new Date());
        String message = map.getString("message");

        int executeCount = 0;
        if (map.containsKey(EXECUTION_COUNT)) {
            executeCount = map.getInt(EXECUTION_COUNT);
        }
        executeCount += 1;
        map.put(EXECUTION_COUNT, executeCount);
        
        System.out.println(String.format("[%-18s][%d][%s] %s", "execute", executeCount, currentDate, message ));
    }
}