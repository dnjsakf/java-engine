package com.dochi.labs.sch;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

public class JobLauncher {
    
    private static final Random rand = new Random();
    
    public static void main(String[] args) {
        
        System.out.println("Enter키를 누르면 종료됩니다.");
        
        try {
            // Scheduler 생성
            SchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();
            
            // Scheduler 실행
            scheduler.start();
            
            // JOB Data 객체 생성
            String nextStepClassName = "com.dochi.labs.sch.ParserJob";
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(ChainJob.NEXT_STEP, nextStepClassName);

            // JOB 생성
            String jobName = "job_name";
            JobDetail jobDetail = JobBuilder.newJob(CrawlerJob.class)
                                    .withIdentity(jobName)
                                    .setJobData(jobDataMap)
                                    .build();

            // Multiple Trigger 설정
            Set<Trigger> triggerSet = new HashSet<Trigger>();
            
            for(int i=1; i<=5; i++) {
                int interval = rand.nextInt(10)+1;
                String triggerName = "simple_trigger_"+i;
                
                System.out.println(String.format("[%s] %d초마다 반복", triggerName, interval));
                
                // SimpleSchedule 생성
                SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule()
                                                    .withIntervalInSeconds(interval)
                                                    .repeatForever();
                
                // SimpleTrigger 생성
                Trigger trigger = TriggerBuilder.newTrigger()
                                    .withIdentity(triggerName)
                                    .withSchedule(schedule)
                                    .forJob(jobDetail)
                                    .build();
                
                triggerSet.add(trigger);
            }
            
            // Schedule 등록
            scheduler.scheduleJob(jobDetail, triggerSet, false);
            
            // Set ShutdownHook
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        scheduler.shutdown();
                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    }
                }
            }));
            
            try {
                System.in.read();
                Set<JobKey> allJobs = scheduler.getJobKeys(GroupMatcher.anyGroup());
                scheduler.deleteJobs(allJobs.stream().collect(Collectors.toList()));
                scheduler.shutdown(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }       
            
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}