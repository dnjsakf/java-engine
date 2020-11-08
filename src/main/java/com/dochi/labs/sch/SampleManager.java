package com.dochi.labs.sch;

import java.text.ParseException;
import java.util.Vector;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.dochi.labs.sch.job.SampleJob;
import com.dochi.labs.sch.listener.MyScheduleListener;
import com.dochi.labs.sch.listener.MyTriggerListener;

public class SampleManager {
    
    public static SchedulerFactory schdedulerFactory = null;
    public final String PROPS_PATH = "quartz/quartz.properties";
    
    private Vector<SampleJob> jobs = new Vector<SampleJob>();
    
    public SampleManager() throws SchedulerException {
        SampleManager.setSchedulerFactory(new StdSchedulerFactory(PROPS_PATH));
    }
    
    public void addJob(SampleJob job) {
        jobs.add(job);
    }
    public void startJobs() {
        jobs.forEach(( job )->{
            try {
                job.start();
            } catch (SchedulerException | ParseException e) {
                e.printStackTrace();
            }
        });
    }
    
    public boolean start() throws SchedulerException {
        Scheduler scheduler = SampleManager.getSchedulerFactory().getScheduler();
        
        scheduler.getListenerManager().addJobListener(new MyScheduleListener());
        scheduler.getListenerManager().addTriggerListener(new MyTriggerListener());
        scheduler.start();
        
        startJobs();
        
        return true;
    }
    
    public void stop() throws SchedulerException {
        SampleManager.getSchedulerFactory().getScheduler().shutdown();
    }
    
    public static SchedulerFactory getSchedulerFactory() {
        return schdedulerFactory;
    }
    public static void setSchedulerFactory(SchedulerFactory schdedulerFactory) {
        SampleManager.schdedulerFactory = schdedulerFactory;
    }
}
