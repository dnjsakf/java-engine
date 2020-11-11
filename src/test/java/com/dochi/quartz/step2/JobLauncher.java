package com.dochi.quartz.step2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Set;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

public class JobLauncher {
    
    // 상수 설정
    //   - Prefix 설정
    public static final String PREFIX_STEP_JOB_NAME = "job_";
    public static final String PREFIX_STEP_TRIGGER_NAME = "trigger_";
    public static final String PREFIX_NEXT_STEP_JOB_NAME = "step_job_";
    public static final String PREFIX_NEXT_STEP_TRIGGER_NAME = "step_trigger_";
    
    //   - DateFormat 설정
    public static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static final SimpleDateFormat DATETIME_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //   - JobDataMap에서 사용할 Key 정의
    public static final String MAIN_STEP_JOB_NAME = "mainStepJobName";
    public static final String NEXT_STEP_CLASS_NAME = "nextStepClassName";
    public static final String NEXT_STEP_JOB_NAME = "nextStepJobName";

    // Scheduler 객체 생성
    private static SchedulerFactory factory = null;
    private static Scheduler scheduler = null;
    
    // Main 함수
    public static void main(String[] args) throws SchedulerException {
        // Scheduler 실행
        start();
        
        // Schedule 등록
        addSchedule("MainJob");

        try {
            System.out.println("아무키나 입력하면 종료됩니다...");
            System.in.read();

            // Scheduler 롱료
            stop();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Scheduler 실행 함수
    public static void start() throws SchedulerException {
        // Scheduler 객체 정의
        factory = new StdSchedulerFactory();
        scheduler = factory.getScheduler();
        
        // Listener 설정
        scheduler.getListenerManager().addJobListener(new JobStepListener());
        
        // Scheduler 실행
        scheduler.start();
    }
    
    // Scheduler 종료 함수
    public static void stop() throws SchedulerException {
        try {
            System.out.println("스케줄러가 종료됩니다...");
            
            // Job Key 목록
            Set<JobKey> allJobKeys = scheduler.getJobKeys(GroupMatcher.anyGroup());
            
            // Job 강제 중단
            allJobKeys.forEach((jobKey)->{
                try {
                    scheduler.interrupt(jobKey);
                } catch (UnableToInterruptJobException e) {
                    e.printStackTrace();
                }
            });
            
            // Scheduler 중단
            //   - true : 모든 Job이  완료될 때까지 대기 후 종료
            //   - false: 즉시 종료
            scheduler.shutdown(true);

            System.out.println("스케줄러가 종료되었습니다.");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    
    // Schedule 등록 함수
    public static void addSchedule(String name) throws SchedulerException {
        // JobDataMap 설정
        //   - Step으로 실행시킬 Job Class 이름 설정
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(NEXT_STEP_CLASS_NAME, "com.dochi.quartz.step.SubJob");
        
        // JobDetail 설정
        JobDetail jobDetail = JobBuilder.newJob(MainJob.class)
                                .withIdentity(PREFIX_STEP_JOB_NAME+name)
                                .setJobData(jobDataMap)
                                .build();
        
        // Trigger 설정
        Trigger trigger = TriggerBuilder.newTrigger()
                              .withIdentity(PREFIX_STEP_TRIGGER_NAME+name)
                              .startNow()
                              .forJob(jobDetail)
                              .build();
        
        // Schedule 등록
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
