package com.dochi.labs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class MyScheduleListener implements JobListener{

    @Override
    public String getName() {
        return MyScheduleListener.class.getName();
    }

    // TriggerListener.vetoJobExecution == false
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        System.out.println(String.format("[3][%s] 작업시작", context.getJobDetail().getKey().toString()));
    }

    // TriggerListener.vetoJobExecution == true
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        System.out.println(String.format("[3][%s] 작업종료", context.getJobDetail().getKey().toString()));
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        System.out.println(String.format("[4][%s] 작업완료: %s", context.getJobDetail().getKey().toString(), jobException != null ? jobException.getMessage() : ""));
    }
}
