package com.dochi.quartz.crawl_db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import com.dochi.quartz.crawl_db.db.DMLService;
import com.dochi.quartz.crawl_db.db.DQLService;
import com.dochi.quartz.step.JobLauncher;


public class CrawlerJob implements InterruptableJob {

	// 상수 설정
	//   - 크롤링 대상 정보 변수
    private final String URL = "https://heodolf.tistory.com/";
    private final String BLOG_ID = "heodolf.tistory.com";
    private final String CATE_ID = "/";

    //   - Data Manage Service 객체
    private final DMLService DML = new DMLService("jdbc:sqlite:quartz.db");
    private final DQLService DQL = new DQLService("jdbc:sqlite:quartz.db");

    // 변수 설정
    //   - 현재 실행중인 Thread 변수
    private Thread currentThread = null;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 현재 Thread 저장
        this.currentThread = Thread.currentThread();

        // 페이지 정보 수집
        int startPage = 1;
        int endPage = getEndPage();

        // 페이지별로 글목록 수집
        List<Map<String, Object>> postList = new ArrayList<Map<String, Object>>();
        for(int page=endPage; page>=startPage; page--) {
            postList.addAll(getAritcleList(page));

            // 테스트용이므로 한번만 적재
            break;
        }

        // 수집된 글목록 저장
        saveAritcleList(postList);

        System.out.println(String.format("[%s][%s] - 조회 완료 ( %d건 )"
                , JobLauncher.TIMESTAMP_FMT.format(new Date())
                , this.getClass().getName()
                , postList.size()));
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        // interrupt 설정
        //   - 강제종료
        if( this.currentThread != null ) {
            this.currentThread.interrupt();
        }
    }

    // 마지막 페이지를 가지고 오는 함수
    private int getEndPage() {
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("BLOG_ID"   , "heodolf.tistory.com");
        dataMap.put("CATE_ID"   , "/");

        // 데이터 조회
        //   - 수집한 블로그 목록 조
        Map<String, Object> result = DQL.getLastArticle(dataMap);
        if( result != null ) {
            Integer maxAtclId = (Integer) result.get("MAX_ATCL_ID");
            Integer maxPage = (Integer) result.get("MAX_PAGE");

            System.out.println(maxAtclId);
            System.out.println(maxPage);
        }

        int lastPage = 1;

        try {
            // 크롤링
            Document document = Jsoup.connect(URL).get();

            // 마지막 페이지를 가진 Element 탐색
            Element element = document.select(".pagination > a[href]:not([class])").last();

            // 마지막 페이지 파싱
            lastPage = Integer.valueOf(element.attr("href").split("=")[1]);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lastPage;
    }

    // 글목록을 가지고 오는 함수
    private List<Map<String, Object>> getAritcleList(int page) {
        // 상수설정
        //   - PAGE URL
        final String PAGE_URL = URL+"?page="+page;
    	//   - 결과변수
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        try {
            // 크롤링
            Document document = Jsoup.connect(PAGE_URL).get();

            // 마지막 페이지를 가진 Element 탐색
            Elements elements = document.select("div.post-item");

            // 글목록 파싱
            elements.forEach((element)->{
            	// 글목록 정보 Map 객체
                Map<String, Object> postMap = new HashMap<String, Object>();

                // Element 탐색
                Element child = element.selectFirst("a[href]");

                // 데이터 추출
                String postId = child.attr("href");
                String title = child.selectFirst("span.title").text();

                if( postId.startsWith("/") ) {
                    postId = postId.substring(1, postId.length());
                }

                // 데이터 저장
                postMap.put("BLOG_ID"   , BLOG_ID);
                postMap.put("CATE_ID"   , CATE_ID);
                postMap.put("ATCL_ID"   , postId);
                postMap.put("URL"       , URL+postId);
                postMap.put("TITLE"     , title);
                postMap.put("PAGE"      , page);
                postMap.put("WORK_YN"   , 0);

                // 리스트에 추가
                list.add(postMap);

                System.out.println(postMap);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 결과 반환
        return list;
    }

    // 글목록을 저장하는 함수
    public int saveAritcleList(List<Map<String, Object>> mapList) {

        //   - 저장 결과 변수
        int inserted = 0;

        try {
        	// 저장 실행
			inserted = DML.insertBlogArticle(mapList);

			// 저장 결과 출력
	        if( inserted >= 0 ) {
	            System.out.println(String.format("데이터 입력 성공: %d건", inserted));
	        } else {
	            System.out.println("데이터 입력 실패");
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}

        // 결과 반환
		return inserted;
    }
}
