package com.dochi.labs.sch.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dochi.labs.sch.SampleManager;

public class CronJob extends SampleJob {
    
    private final Logger LOGGER = LoggerFactory.getLogger(CronJob.class);

    public CronJob(String name, String schedule, Class<? extends Job> jobClass) {
        this(name, schedule, jobClass, DEFAULT_GROUP);
    }
    
    public CronJob(String name, String schedule, Class<? extends Job> jobClass, String group) {
        this.name = name;
        this.schedule = schedule;
        this.jobClass = jobClass;
        this.group = group;
    }

    public void start() throws SchedulerException, ParseException {
        SchedulerFactory factory = SampleManager.getSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        
        JobDetail job = createJob();
        
        String name = String.format("%s_%s", PREFIX_TRIGGER_KEY, this.name);
        String group = String.format("%s_%s", PREFIX_TRIGGER_KEY, this.group);
        
        CronExpression crontab = new CronExpression(this.schedule);
        CronScheduleBuilder schedule = CronScheduleBuilder.cronSchedule(crontab);
        
        TriggerKey key = new TriggerKey(name, group);
        Trigger trigger = TriggerBuilder.newTrigger()
                            .forJob(job)
                            .withIdentity(key)
                            .withSchedule(schedule)
                            .build();

        JobDataMap jobDataMap = job.getJobDataMap();
        jobDataMap.put("started", new SimpleDateFormat("yyyyMMddhhmmss.SSSS").format(new Date()));
        jobDataMap.put("crontab", crontab);
        
        this.scheduleTime = scheduler.scheduleJob(job, trigger);
        scheduler.start();
    }
}