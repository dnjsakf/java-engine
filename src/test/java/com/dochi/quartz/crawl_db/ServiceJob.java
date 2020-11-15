package com.dochi.quartz.crawl_db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import com.dochi.db.ex.DMLService;

public class ServiceJob implements InterruptableJob {
	
	private Thread currentThread = null;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		this.currentThread = Thread.currentThread();
		
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		// 강제 종료
		if( this.currentThread != null ) {
			this.currentThread.interrupt();
		}
	}

	// 수집한 글목록을 저장하는 함수
	public int savePost(List<Map<String, Object>> mapList) {
		// 변수 설정
		//   - Data Manage Service 객체
        DMLService DML = new DMLService("jdbc:sqlite:quartz.db");
        
        //   - 저장 결과 변수
        int inserted = 0;
        
        try {
        	// 저장 실행
			inserted = DML.insertBlogArticle(mapList);
	        if( inserted >= 0 ) {
	            System.out.println(String.format("데이터 입력 성공: %d건", inserted));
	        } else {
	            System.out.println("데이터 입력 실패");
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return inserted;
	}
}
