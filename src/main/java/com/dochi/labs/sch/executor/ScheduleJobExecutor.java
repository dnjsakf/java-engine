package com.dochi.labs.sch.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dochi.labs.db.MariaDBManager;

class JOB_TYPES {
	public static int ONCE = 0;			// 일회성
	public static int MULTIPLE = 1;		// 주기성
}

class SCH_TYPES {
	public static int FIXED = 0;		// 지정일
	public static int CRONTAB = 1;		// 크론탭
}

public class ScheduleJobExecutor implements Job {
	
	final String SELECT_JOB_LIST = "SELECT T1.JOB_ID"
								 + "     , T1.JOB_TYPE"
								 + "     , T1.JOB_NAME"
								 + "     , T1.EXEC_SCH_TYPE"
								 + "     , T1.EXEC_SCH"
								 + "     , T1.EXEC_SRC"
								 + "     , T1.REG_USER"
								 + "     , T1.REG_DTTM"
								 + "  FROM DOCHI.JOB_MST T1"
								 + " WHERE 1=1"
								 + "   AND T1.EXEC_YN = 'Y'"
								 ;
	
	final String UPDATE_JOB_MST = "UPDATE DOCHI.JOB_MST"
								+ "   SET EXEC_YN = ?"
								+ " WHERE 1=1"
								+ "   AND JOB_ID = ?"
								;
	
	final String GET_JOB_SCH_INFO = "SELECT T1.JOB_ID"
						 		  + "     , T2.SCH_ID"
						 		  + "     , T3.SCH_TYPE"
						 		  + "     , T3.NEXT_SCH_DATE"
						 		  + "     , T3.NEXT_SCH_TIME"
						 		  + "     , T3.RUN_FLAG"
						 		  + "  FROM DOCHI.JOB T1"
						 		  + " INNER JOIN DOCHI.JOB_SCH_MAPP T2"
						 		  + "    ON T2.JOB_ID = T1.JOB_ID"
						 		  + " INNER JOIN DOCHI.JOB_SCHEDULE T3"
						 		  + "    ON T3.SCH_ID = T2.SCH_ID"
						 		  + " WHERE 1=1"
						 		  + "   AND T1.JOB_ID = ?"
						 		  + "   AND T1.EXEC_YN = 'Y'"
						 		  ;
	
	final String INSERT_SCHEDULE = "INSERT INTO DOCHI.JOB_SCHEDULE ("
							     + ""
							     + ") VALUES ( "
							     + ""
							     + ")"
							     ;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		MariaDBManager db = new MariaDBManager();

		db.ensureConnection();
        
        Connection conn = db.getConnection();
		
		try {
			PreparedStatement psmt = conn.prepareStatement(SELECT_JOB_LIST);
			ResultSet rs = psmt.executeQuery();
			
			while(rs.next()) {
				ResultSetMetaData rsMap = rs.getMetaData();
				
				int colCnt = rsMap.getColumnCount();
				
				for(int i = 1; i <= colCnt; i++) {
					int colType = rsMap.getColumnType(0);
					Object colData = null;
					
					switch(colType) {
						case Types.INTEGER:
							
							colData = (Integer) rs.getInt(i);
							
							System.out.println(String.format("INTEGER: %d", colData));
							break;

						case Types.CHAR:
						case Types.VARCHAR:
							colData = (String) rs.getString(i);
							
							System.out.println(String.format("STRING: %s", colData));
							break;
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getSchedule() {
		MariaDBManager db = new MariaDBManager();

		db.ensureConnection();
        
        Connection conn = db.getConnection();
		
		try {
			PreparedStatement psmt = conn.prepareStatement(SELECT_JOB_LIST);
			ResultSet rs = psmt.executeQuery();
			
			while(rs.next()) {
				String jobId 	= (String) rs.getObject("JOB_ID");
				Integer jobType = (Integer) rs.getObject("JOB_TYPE");
				String jobName 	= (String) rs.getObject("JOB_NAME");
				
				Integer execSchType = (Integer) rs.getObject("EXEC_SCH_TYPE");
				String execSch = (String) rs.getObject("EXEC_SCH");
				
				System.out.println(jobId);
				System.out.println(jobType);
				System.out.println(jobName);
				
				// 스케줄 반복 여부 설정
				if( JOB_TYPES.ONCE == jobType ) {
					
				} else if( JOB_TYPES.MULTIPLE == jobType ) {
				}
				
				// 스케줄 실행 일자/시간 생성 
				if( SCH_TYPES.FIXED == execSchType ) {
					
 				} else if ( SCH_TYPES.CRONTAB == execSchType ) {
					try {
						CronExpression crontab = new CronExpression(execSch);
						
						Date nextDateTime = crontab.getNextValidTimeAfter(new Date());
						
						System.out.println( nextDateTime );
						
					} catch (ParseException e) {
						e.printStackTrace();
					}
 				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ScheduleJobExecutor job = new ScheduleJobExecutor();
		job.getSchedule();
	}
	
}
