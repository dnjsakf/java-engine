package com.dochi.quartz;

import java.util.UUID;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class ChainJobListener implements JobListener {

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        System.out.println(String.format("[%s][jobToBeExecuted]", getName()));
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        System.out.println(String.format("[%s][jobExecutionVetoed]", getName()));
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        try {
            // JobDataMapp에 Next Step이 등록된 경우 스케줄 생성
            if( jobDataMap.containsKey(QuartzLauncher.NEXT_STEP_CLASS_NAME) ) {
                System.out.println("Add next step schedule...");
                addNextStepSchedule(context);
            }
        } catch (ClassNotFoundException | SchedulerException e) {
            e.printStackTrace();
        } finally {
            System.out.println(String.format("[%s][jobWasExecuted]", getName()));
        }
    }
    
    // Next Step Schedule 등록 함수
    private void addNextStepSchedule(JobExecutionContext context) throws SchedulerException, ClassNotFoundException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        
        String mainStepJobName = context.getJobDetail().getKey().getName();
        String nextStepClassName = jobDataMap.getString(QuartzLauncher.NEXT_STEP_CLASS_NAME);
        String nextStepJobName = jobDataMap.getString(QuartzLauncher.NEXT_STEP_JOB_NAME);
        
        if( nextStepJobName == null ) {
            // 이름이 없는 경우
            //   - 중복 방지를 위해 UUID 생성
            nextStepJobName = UUID.randomUUID().toString();
        }
        
        // Next Step Class
        //   - 문자열로 Class 탐색
        Class<?> jobClass = Class.forName(nextStepClassName);
        
        // JobDetail 생성
        //   - NextStep에 MainStepName을 전달
        JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>)jobClass)
                                .withIdentity(QuartzLauncher.PREFIX_NEXT_STEP_JOB_NAME+nextStepJobName)
                                .usingJobData(QuartzLauncher.MAIN_STEP_JOB_NAME, mainStepJobName)
                                .build();
        
        // Trigger 생성
        //   - 바로 실행
        Trigger trigger = TriggerBuilder.newTrigger()
                              .withIdentity(QuartzLauncher.PREFIX_NEXT_STEP_TRIGGER_NAME+nextStepJobName)
                              .startNow()
                              .forJob(jobDetail)
                              .build();
        
        // 스케줄 등록
        context.getScheduler().scheduleJob(jobDetail, trigger);
    }
}
