package com.dochi.quartz.crawl_db;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Set;

import org.quartz.JobBuilder;
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

import com.dochi.db.ex.DDLService;
import com.dochi.db.ex.DDLService.ResultType;

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
    
    //   - Database Service 객체 변수
    private final DDLService DDL = new DDLService("jdbc:sqlite:quartz.db");

    // Scheduler 객체 생성
    private static SchedulerFactory factory = null;
    private static Scheduler scheduler = null;

    // Main 함수
    public static void main(String[] args) throws SchedulerException, SQLException {
    	JobLauncher launcher = new JobLauncher();
    	
    	// 데이터베이스 설정
    	launcher.initDatabase();
    	
        // Scheduler 실행
    	launcher.start();

        // Schedule 등록
    	launcher.addSchedule("CrawlerJob");

        try {
            System.out.println("아무키나 입력하면 종료됩니다...");
            System.in.read();

            // Scheduler 롱료
            launcher.stop();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Scheduler 실행 함수
    public void start() throws SchedulerException {
        // Scheduler 객체 정의
        factory = new StdSchedulerFactory();
        scheduler = factory.getScheduler();
        
        // Listener 설정
        scheduler.getListenerManager().addJobListener(new JobLogListener());

        // Scheduler 실행
        scheduler.start();
    }

    // Scheduler 종료 함수
    public void stop() throws SchedulerException {
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
    public void addSchedule(String name) throws SchedulerException {
        // JobDetail 설정
        JobDetail jobDetail = JobBuilder.newJob(CrawlerJob.class)
                                .withIdentity(PREFIX_STEP_JOB_NAME+name)
                                .build();

        // Schedule 생성
        //   - 5분마다 반복, 최대 3회
        SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule()
                                            .withRepeatCount(3)
                                            .withIntervalInMinutes(5);

        // Trigger 설정
        Trigger trigger = TriggerBuilder.newTrigger()
                              .withIdentity(PREFIX_STEP_TRIGGER_NAME+name)
                              .withSchedule(schedule)
                              .forJob(jobDetail)
                              .build();

        // Schedule 등록
        scheduler.scheduleJob(jobDetail, trigger);
    }
    
    // 데이터베이스 설정
    private void initDatabase() throws SQLException {
        //DDL.dropTable("CW_BLOG_ATCL_LIST");
    	
    	// 상수 설정
    	//   - 테이블 생성 SQL 변수
        final String SQL = "CREATE TABLE IF NOT EXISTS CW_BLOG_ATCL_LIST (   "+"\n"
                         + "  BLOG_ID     TEXT           NOT NULL,           "+"\n"
                         + "  CATE_ID     TEXT           NOT NULL,           "+"\n"
                         + "  ATCL_ID     TEXT           NOT NULL,           "+"\n"
                         + "  URL         TEXT           NOT NULL,           "+"\n"
                         + "  TITLE       TEXT,                              "+"\n"
                         + "  WORK_YN     INTEGER        DEFAULT 0,          "+"\n"
                         + "  REG_DTTM    TEXT,                              "+"\n"
                         + "  UPD_DTTM    TEXT,                              "+"\n"
                         + "  PRIMARY KEY (BLOG_ID, CATE_ID, ATCL_ID)       )";

        // 테이블 생성
        ResultType result = DDL.createTable("CW_BLOG_ATCL_LIST", SQL);

        // 테이블 생성 결과 출력
        switch( result ) {
            case SUCCESS:
                System.out.println("테이블 생성 완료.");
                break;
            case WARNING:
                System.out.println("테이블이 이미 존재합니다.");
                break;
            case FAILURE:
                System.out.println("테이블 생성 실패.");
                break;
        }
    }
}
