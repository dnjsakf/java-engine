package com.dochi.quartz.interrupt;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class JobLogListener implements JobListener {
    
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        System.out.println(String.format("[%s][%s][triggerComplete]", JobLauncher.TIMESTAMP_FMT.format(new Date()), getName()));
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        System.out.println(String.format("[%s][%s][jobExecutionVetoed]", JobLauncher.TIMESTAMP_FMT.format(new Date()), getName()));
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        System.out.println(String.format("[%s][%s][jobWasExecuted]", JobLauncher.TIMESTAMP_FMT.format(new Date()), getName()));
    }

}
