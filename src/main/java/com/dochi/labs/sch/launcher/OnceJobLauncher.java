package com.dochi.labs.sch.launcher;

import java.text.ParseException;
import java.util.Date;

import org.quartz.Job;
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

public class OnceJobLauncher extends SampleJobLauncher {
    
    private final Logger LOGGER = LoggerFactory.getLogger(OnceJobLauncher.class);

    public OnceJobLauncher(String name, Date startDate, Class<? extends Job> jobClass) {
        this(name, startDate, jobClass, DEFAULT_GROUP);
    }
    
    public OnceJobLauncher(String name, Date startDate, Class<? extends Job> jobClass, String group) {
        this.name = name;
        this.startDate = startDate;
        this.jobClass = jobClass;
        this.group = group;
    }

    public void start() throws SchedulerException, ParseException {
        SchedulerFactory factory = SampleManager.getSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        
        JobDetail job = createJob();
        String name = String.format("%s_%s", PREFIX_TRIGGER_KEY, this.name);
        String group = String.format("%s_%s", PREFIX_TRIGGER_KEY, this.group);
        
        TriggerKey key = new TriggerKey(name, group);
        Trigger trigger = TriggerBuilder.newTrigger()
                            .forJob(job)
                            .withIdentity(key)
                            .startAt(this.startDate)
                            .build();
        
        this.scheduleTime = scheduler.scheduleJob(job, trigger);
        scheduler.start();
    }
}