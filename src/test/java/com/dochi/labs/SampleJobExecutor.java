package com.dochi.labs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.ListenerManager;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Quartz Job
 *   for StatefulJob
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SampleJobExecutor implements Job {
    
    private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");
    public static final String EXECUTION_COUNT = "EXECUTION_COUNT";
    
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobDataMap map = ctx.getJobDetail().getJobDataMap();
        String currentDate = TIMESTAMP_FMT.format(new Date());
        String message = map.getString("message");

        int executeCount = 0;
        if (map.containsKey(EXECUTION_COUNT)) {
            executeCount = map.getInt(EXECUTION_COUNT);
        }
        executeCount += 1;
        map.put(EXECUTION_COUNT, executeCount);
        
        System.out.println(String.format("[%-18s][%d][%s] %s", "execute", executeCount, currentDate, message ));
    }
}

/**
 * Quartz Scheduler 실행
 */
class JobLuacher {
    public static void main(String[] args) {
        try {
            // Scheduler 생성
            SchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();
            
            // Scheduler / Trigger Listener 설정
            ListenerManager listenrManager = scheduler.getListenerManager(); 
            listenrManager.addJobListener(new MyJobListener());
            listenrManager.addTriggerListener(new MyTriggerListener());
            
            // Scheduler 실행
            scheduler.start();

            // JOB Executor Class
            Class<? extends Job> jobClass = SampleJobExecutor.class;
            
            // JOB Data 객체 생성
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("message", "Hello, Quartz!!!");
            jobDataMap.put(SampleJobExecutor.EXECUTION_COUNT, 0);

            // JOB 생성
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                                    .withIdentity("job_name", "job_group")
                                    .setJobData(jobDataMap)
                                    .build();
            
            // SimpleTrigger 생성
            // 3초마다 반복하며, 최대 5회 실행
            SimpleScheduleBuilder simpleSch = SimpleScheduleBuilder.simpleSchedule()
            									.withRepeatCount(5)
            									.withIntervalInSeconds(3);
            SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                                            .withIdentity("simple_trigger", "simple_trigger_group")
                                            .withSchedule(simpleSch)
                                            .forJob(jobDetail)
                                            .build();

            // Schedule 등록
            scheduler.scheduleJob(jobDetail, simpleTrigger);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}