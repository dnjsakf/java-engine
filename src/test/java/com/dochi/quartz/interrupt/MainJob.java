package com.dochi.quartz.interrupt;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;


public class MainJob implements InterruptableJob {
    
    private Thread currentThread = null;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 현재 Thread 저장
        this.currentThread = Thread.currentThread();
        
        String jobName = context.getJobDetail().getKey().getName();
        
        System.out.println(String.format("[%s][%s] Running...", this.getClass().getName(), jobName));
        try {
            // 강제로 종료를 지연시키기
            Thread.sleep(5*1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("[%s][%s] Finish!!!", this.getClass().getName(), jobName));
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
