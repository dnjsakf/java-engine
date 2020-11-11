package com.dochi.quartz;

import java.io.IOException;
import java.util.Set;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

public class QuartzLauncher {
    
    // 상수 설정
    //   - JobDataMap에서 사용할 Key 정의
    public static final String MAIN_STEP_JOB_NAME = "mainStepJobName";
    public static final String NEXT_STEP_CLASS_NAME = "nextStepClassName";
    public static final String NEXT_STEP_JOB_NAME = "nextStepJobName";

    //   - Prefix 설정
    public static final String PREFIX_STEP_JOB_NAME = "job_";
    public static final String PREFIX_STEP_TRIGGER_NAME = "trigger_";
    public static final String PREFIX_NEXT_STEP_JOB_NAME = "step_job_";
    public static final String PREFIX_NEXT_STEP_TRIGGER_NAME = "step_trigger_";

    // Scheduler 객체 생성
    private static SchedulerFactory factory = null;
    private static Scheduler scheduler = null;
    
    // Main 함수
    public static void main(String[] args) throws SchedulerException {
        // Scheduler 실행
        start();
        
        // Schedule 등록
        addSchedule("MainJob");
        addSchedule("MainJob2");

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
        
        // JobListener 적용
        scheduler.getListenerManager().addJobListener(new ChainJobListener());
        
        // Scheduler 실행
        scheduler.start();
    }
    
    // Scheduler 종료 함수
    public static void stop() throws SchedulerException {
        try {
            // Scheduler 종료
            //   - true : 모든 Job이  완료될 때까지 대기 후 종료
            //   - false: 즉시 종료
            scheduler.shutdown(true);
            
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    
    // Schedule 등록 함수
    public static void addSchedule(String name) throws SchedulerException {
        // JobDataMap 설정
        //   - Step으로 실행시킬 Job Class 이름 설정
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(NEXT_STEP_CLASS_NAME, "com.dochi.quartz.SubJob");
        
        // JobDetail 설정
        JobDetail jobDetail = JobBuilder.newJob(MainJob.class)
                                .withIdentity(PREFIX_STEP_JOB_NAME+name)
                                .setJobData(jobDataMap)
                                .build();
        
        // Simple Schedule 생성
        //   - 3초마다 실행, 최대 5회
        SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule()
                                            .withIntervalInSeconds(3)
                                            .withRepeatCount(5);
        
        // Trigger 설정
        Trigger trigger = TriggerBuilder.newTrigger()
                              .withIdentity(PREFIX_STEP_TRIGGER_NAME+name)
                              .withSchedule(schedule)
                              .forJob(jobDetail)
                              .build();
        
        // Schedule 등록
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
