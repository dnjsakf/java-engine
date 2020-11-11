package com.dochi.quartz.step;

import java.util.Date;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;


public class MainJob implements InterruptableJob {
    
    private Thread currentThread = null;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().getName();
        
        System.out.println(String.format("[%s][%s][%s] START", JobLauncher.TIMESTAMP_FMT.format(new Date()), this.getClass().getName(), jobName));
        
        // 현재 Thread 저장
        this.currentThread = Thread.currentThread();
        
        try {
            // 강제로 종료를 지연시키기
            for(int i=1; i<=5; i++) {
                System.out.println(String.format("[%s][%s][%s] 작업중...", JobLauncher.TIMESTAMP_FMT.format(new Date()), this.getClass().getName(), jobName));
                Thread.sleep(1*1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("[%s][%s][%s] END", JobLauncher.TIMESTAMP_FMT.format(new Date()), this.getClass().getName(), jobName));
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        // interrupt 설정
        //   - 강제종료
        if( this.currentThread != null ) {
            this.currentThread.interrupt();
        }
    }
}
