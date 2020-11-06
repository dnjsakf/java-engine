package com.dochi.labs.sch;

import java.text.ParseException;
import java.util.Date;

import org.quartz.SchedulerException;

import com.dochi.labs.sch.jobs.SampleJob;
import com.dochi.labs.sch.launcher.CronJobLauncher;
import com.dochi.labs.sch.launcher.OnceJobLauncher;

public class ScheduleManager extends Thread {
    
    public static void main(String[] args) throws SchedulerException, ParseException {
        
        SampleManager manager = new SampleManager();
        manager.start();

        new OnceJobLauncher("test1", new Date(), SampleJob.class)
            .setParam("DATABASE", "maria")
            .setParam("USERNAME", "dochi")
            .setParam("PASSWORD", "dochi")
            .start();
        new CronJobLauncher("test2", "0/1 * * * * ?", SampleJob.class)
            .setParam("DATABASE", "sqlite")
            .setParam("FILE", "src/main/resources/sqlite/chinook.db")
            .start();
        
//        manager.addJob(new CronJobLauncher("test2", "0/5 * * * * ?", SampleJob.class));       
    }
}
