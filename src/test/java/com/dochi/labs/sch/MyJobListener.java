package com.dochi.labs.sch;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class MyJobListener implements JobListener{
    
    Class<? extends Job> nextJobClass = null;
    
    public MyJobListener() {
        super();
    }
    public MyJobListener(Class<? extends Job> nextJobClass) {
        this.nextJobClass = nextJobClass;
    }

    @Override
    public String getName() {
        return MyJobListener.class.getName();
    }

    /**
     * Job이 수행되기 전 상태
     *   - TriggerListener.vetoJobExecution == false
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        System.out.println(String.format("[%-18s][%s] 작업시작", "jobToBeExecuted", context.getJobDetail().getKey().toString()));
    }

    /**
     * Job이 중단된 상태
     *   - TriggerListener.vetoJobExecution == true
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        System.out.println(String.format("[%-18s][%s] 작업중단", "jobExecutionVetoed", context.getJobDetail().getKey().toString()));
    }

    /**
     * Job 수행이 완료된 상태
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        /*
        Scheduler scheduler = context.getScheduler();
        
        JobDetail prevJobDetail = context.getJobDetail();
        String prevJobName = prevJobDetail.getKey().getName();
        String prevJobGroup = prevJobDetail.getKey().getGroup();
        
        Trigger prevTrigger = context.getTrigger();
        String prevTriggerName = prevTrigger.getKey().getName();
        String prevTriggerGroup = prevTrigger.getKey().getGroup();
        
        
        JobDetail jobDetail = JobBuilder.newJob(this.nextJobClass)
                                    .withIdentity("chain_"+prevJobName, prevJobGroup)
                                    .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                            .withIdentity("chain_"+prevTriggerName, prevTriggerGroup)
                            .startNow()
                            .build();
        
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        */
        System.out.println(String.format("[%-18s][%s] 작업완료", "jobWasExecuted", context.getJobDetail().getKey().toString()));
    }
}
