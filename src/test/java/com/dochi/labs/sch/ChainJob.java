package com.dochi.labs.sch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * Job을 상속받은 추상화 클래스
 * Refs.
 *   - https://flylib.com/books/en/2.65.1/job_chaining_in_quartz.html
 */
public abstract class ChainJob implements Job {

    public static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static final String NEXT_STEP = "NEXT_STEP";
    public static final String NEXT_STEP_NAME = "NEXT_STEP_NAME";
    public static final String PREFIX_JOB_NAME = "chain_job";
    public static final String PREFIX_TRIGGER_NAME = "chain_trigger_";
    
    /**
     * 다음 스탭이 있는 경우, 새로운 스케줄을 등록하여 실행
     */
    public void runNextStep(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        // 다음 스탭이 없는 경우 종료
        if( !jobDataMap.containsKey(NEXT_STEP) ) {
            return;
        }

        /*
         * Job/Trigger 이름 설정
         */
        String chainName = jobDataMap.getString("NEXT_STEP_NAME");
        if( chainName == null ) {
            chainName = UUID.randomUUID().toString();   // 중복방지용
        }
        String jobName = context.getJobDetail().getKey().getName();
        String triggerName = context.getTrigger().getKey().getName();
        
        try {
            // 다음 스탭의 클래스 탐색
            Class<?> chainJobClass =  Class.forName(jobDataMap.getString(NEXT_STEP));
            
            // 현재 Job 정보 저장
            jobDataMap.put("ROOT", context);

            // 다음 스탭의 Job 생성
            @SuppressWarnings("unchecked")
            JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) chainJobClass)
                                    .withIdentity(PREFIX_JOB_NAME+chainName, jobName)
                                    .setJobData(jobDataMap)
                                    .build();

            /*
             * 다음 스탭의 Trigger 생성
             *   - 즉시 실행
             *   - JobDataMap에 실행시간, 크론탭 등을 저장해서 활용할 수 있음.
             *   - 수행이 완료된 후 Trigger 객체는 소멸됨
             */
            Trigger trigger = TriggerBuilder.newTrigger()
                                .withIdentity(PREFIX_TRIGGER_NAME+chainName, triggerName)
                                //.startNow()
                                .build();
        
            // 다음 스탭 스케줄 등록
            context.getScheduler().scheduleJob(jobDetail, trigger);
            
        } catch (ClassNotFoundException | SchedulerException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Logging
     */
    public void println(String message) {
        System.out.println(String.format("[%s][%s]%s"
            , TIMESTAMP_FMT.format(new Date()) 
            , this.getClass().getName()
            , message
        ));
    }
}
