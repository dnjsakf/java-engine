package com.dochi.quartz.crawl;

import java.io.IOException;
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

import com.dochi.quartz.step.JobLauncher;


public class CrawlerJob implements InterruptableJob {

    private Thread currentThread = null;
    private final String URL = "https://heodolf.tistory.com/";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 현재 Thread 저장
        this.currentThread = Thread.currentThread();

        System.out.println(String.format("[%s][%s][%s] START"
                , JobLauncher.TIMESTAMP_FMT.format(new Date())
                , this.getClass().getName()
                , context.getJobDetail().getKey().getName()));

        // 페이지 정보 수집
        int startPage = 1;
        int endPage = getEndPage();

        // 페이지별로 글목록 수집
        List<Map<String, String>> postList = new ArrayList<Map<String, String>>();
        for(int page=endPage; page>=startPage; page--) {
            postList.addAll(getAritcleList(URL+"?page="+page));
        }

        System.out.println(String.format("[%s][%s] - 조회 완료 ( %d건 )"
                , JobLauncher.TIMESTAMP_FMT.format(new Date())
                , this.getClass().getName()
                , postList.size()));
        System.out.println(String.format("[%s][%s][%s] END"
                , JobLauncher.TIMESTAMP_FMT.format(new Date())
                , this.getClass().getName()
                , context.getJobDetail().getKey().getName()));
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
    private List<Map<String, String>> getAritcleList(String url) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        try {
            // 크롤링
            Document document = Jsoup.connect(url).get();

            // 마지막 페이지를 가진 Element 탐색
            Elements elements = document.select("div.post-item");

            // 글목록 파싱
            elements.forEach((element)->{
                Map<String, String> postMap = new HashMap<String, String>();

                // Element 탐색
                Element child = element.selectFirst("a[href]");

                // 데이터 추출
                String postId = child.attr("href");
                String title = child.selectFirst("span.title").text();
                String meta = child.selectFirst("span.meta").text();

                if( postId.startsWith("/") ) {
                    postId = postId.substring(1, postId.length());
                }

                // 데이터 저장
                postMap.put("url", URL+postId);
                postMap.put("title", title);
                postMap.put("meta", meta);

                // 리스트에 추가
                list.add(postMap);

                System.out.println(postMap.toString());
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
