package com.dochi.labs.sch.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dochi.labs.sch.SampleManager;

public abstract class SampleJob {
    
    private final Logger LOGGER = LoggerFactory.getLogger(SampleJob.class);
    
    protected final static String PREFIX_JOB_KEY = "JOB";
    protected final static String PREFIX_TRIGGER_KEY = "TRIGGER";
    protected final static String PREFIX_GROUP = "GROUP";
    protected final static String DEFAULT_GROUP = "JOBS";
    
    protected String name = null;
    protected String group = null;
    protected String schedule = null;
    protected Date startDate = null;
    protected Date scheduleTime = null;
    
    protected Class<? extends Job> jobClass = null;
    protected Map<String, Object> params = new HashMap<String,  Object>();
    
    public SampleJob setParams(Map<String, Object> params) {
        this.params.putAll(params);
        
        return this;
    }
    public SampleJob setParam(String key, Object value) {
        this.params.put(key, value);
        
        return this;
    }
    
    /**
     * Job 생성
     */
    public JobDetail createJob() {
        String name = String.format("%s_%s", PREFIX_JOB_KEY, this.name);
        String group = String.format("%s_%s_%s", PREFIX_GROUP, PREFIX_JOB_KEY, this.group);
        
        JobKey key = new JobKey(name, group);
        JobDetail job = JobBuilder.newJob(this.jobClass)
                    .withIdentity(key)
                    .requestRecovery()
                    .build();
        
        JobDataMap jobDataMap = job.getJobDataMap();
        jobDataMap.put("group", group);
        jobDataMap.put("name", name);
        jobDataMap.put("created", new SimpleDateFormat("yyyyMMddhhmmss.SSSS").format(new Date()));
        
        if( params != null && params.size() > 0 ) {
            jobDataMap.putAll(params);
        }
        
        return job;
    }

    /**
     * Job 스케줄 실행
     */
    public abstract void start() throws SchedulerException, ParseException;

    /**
     * Job 스케줄 정지
     */
    public void stop() throws SchedulerException {
        try {
            SchedulerFactory factory = SampleManager.getSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();
            
            if( scheduler != null ) {
                TriggerKey triggerKey = new TriggerKey(
                        String.format("%s_%s", PREFIX_TRIGGER_KEY, this.name), 
                        String.format("%s_%s_%s", PREFIX_GROUP, PREFIX_TRIGGER_KEY, this.group)
                        );
                Trigger trigger = scheduler.getTrigger(triggerKey);
                
                if( trigger != null ) {
                    scheduler.pauseTrigger(triggerKey);
                    scheduler.unscheduleJob(triggerKey);
                    scheduler.deleteJob(trigger.getJobKey());
                }
            }
        } catch ( SchedulerException e ) {
            throw e;
        }
    }
}
