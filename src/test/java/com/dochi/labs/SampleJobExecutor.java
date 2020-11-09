package com.dochi.labs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Quartz Job
 * http://www.quartz-scheduler.org/documentation/quartz-2.1.7/examples/Example5.html
 */

// Stateful Job
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SampleJobExecutor implements Job {
    
    private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");
    public static final String NUM_EXECUTIONS = "NumExecutions";
    
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobDataMap jobDataMap = ctx.getJobDetail().getJobDataMap();
        
        String currentDate = TIMESTAMP_FMT.format(new Date());
        String triggerKey = ctx.getTrigger().getKey().toString();
        String message = jobDataMap.getString("message");
        
        JobDataMap map = ctx.getJobDetail().getJobDataMap();

        int executeCount = 0;
        if (map.containsKey(NUM_EXECUTIONS)) {
            executeCount = map.getInt(NUM_EXECUTIONS);
        }
        executeCount += 1;
        map.put(NUM_EXECUTIONS, executeCount);
        
        System.out.println(String.format("[4][%s][%s][%d] %s", currentDate, triggerKey, executeCount, message ));
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
            
            scheduler.getListenerManager().addJobListener(new MyScheduleListener());
            scheduler.getListenerManager().addTriggerListener(new MyTriggerListener());
            
            // Scheduler 실행
            scheduler.start();
            
            // JOB Data 객체 생성
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("message", "Hello, Quartz!!!");
            
            // JOB Executor Class
            Class<? extends Job> jobClass = SampleJobExecutor.class;

            // JOB 생성
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                                    .withIdentity("job_name", "job_group")
                                    .setJobData(jobDataMap)
                                    .build();
            
            // SimpleTrigger 생성
            // 4초마다 반복하며, 최대 5회 실행
            SimpleScheduleBuilder simpleSch = SimpleScheduleBuilder.repeatSecondlyForTotalCount(10, 1).withMisfireHandlingInstructionNowWithExistingCount();
            SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                                            .withIdentity("simple_trigger", "simple_trigger_group")
                                            .withSchedule(simpleSch)
                                            .forJob(jobDetail)
                                            .build();
            
            // CronTrigger 생성
            // 15초주기로 반복( 0, 15, 30, 45 )
            CronScheduleBuilder cronSch = CronScheduleBuilder.cronSchedule(new CronExpression("0/15 * * * * ?"));
            CronTrigger cronTrigger = (CronTrigger) TriggerBuilder.newTrigger()
                                        .withIdentity("cron_trigger", "cron_trigger_group")
                                        .withSchedule(cronSch)
                                        .forJob(jobDetail)
                                        .build();
            
            // JobDtail : Trigger = 1 : N 설정
            Set<Trigger> triggerSet = new HashSet<Trigger>();
            triggerSet.add(simpleTrigger);
//            triggerSet.add(cronTrigger);

            // Schedule 등록
            scheduler.scheduleJob(jobDetail, triggerSet, false);
            
        } catch (ParseException | SchedulerException e) {
            e.printStackTrace();
        }
    }
}