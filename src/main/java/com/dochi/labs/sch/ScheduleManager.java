package com.dochi.labs.sch;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.SchedulerException;

import com.dochi.labs.sch.executor.SampleJobExecutor;
import com.dochi.labs.sch.job.CronJob;
import com.dochi.labs.sch.job.OnceJob;
import com.dochi.labs.sch.job.SampleJob;

public class ScheduleManager extends Thread {
    
    public static void main(String[] args) throws SchedulerException, ParseException {
        
        SampleManager manager = new SampleManager();
        manager.start();

        
        SampleJob onceJob = new OnceJob("ScheduleLoader", new Date(), SampleJobExecutor.class);
        Map<String, Object> onceJobMap = new HashMap<String, Object>();
        onceJobMap.put("DATABASE", "maria");
        onceJobMap.put("USERNAME", "dochi");
        onceJobMap.put("PASSWORD", "dochi");
        onceJob.setParams(onceJobMap).start();
        
        
        SampleJob cronJob = new CronJob("DataCollector", "0/5 * * * * ?", SampleJobExecutor.class);
        Map<String, Object> cronJobMap = new HashMap<String, Object>();
        cronJobMap.put("DATABASE", "sqlite");
        cronJobMap.put("FILE", "src/main/resources/sqlite/chinook.db");
        cronJob.setParams(cronJobMap).start();
        
    }
}