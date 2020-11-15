package com.dochi.quartz.crawl_db.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dochi.quartz.crawl_db.db.DDLService.ResultType;

public class MariaApp {

    // 변수 생성
    //   - DDL 객체 변수 ( dochi.db 파일 생성 )
    private DDLService DDL = new DDLService("jdbc:mariadb://localhost:3306/DOCHI", "dochi", "dochi");

    //   - DML 객체 변수 ( dochi.db 파일 수정 )
    private DMLService DML = new DMLService("jdbc:mariadb://localhost:3306/DOCHI", "dochi", "dochi");

    //   - DQL 객체 변수 ( dochi.db 파일 조회 )
    private DQLService DQL = new DQLService("jdbc:mariadb://localhost:3306/DOCHI", "dochi", "dochi");

    // 테이블 생성 함수
    public void createTable() throws SQLException {
        final String SQL = "CREATE TABLE IF NOT EXISTS CW_BLOG_ATCL_LIST (   "+"\n"
                         + "  BLOG_ID     VARCHAR(100)   NOT NULL,           "+"\n"
                         + "  CATE_ID     VARCHAR(50)    NOT NULL,           "+"\n"
                         + "  ATCL_ID     VARCHAR(50)    NOT NULL,           "+"\n"
                         + "  URL         VARCHAR(500)   NOT NULL,           "+"\n"
                         + "  TITLE       VARCHAR(500),                      "+"\n"
                         + "  WORK_YN     INTEGER        DEFAULT 0,          "+"\n"
                         + "  REG_DTTM    VARCHAR(14),                       "+"\n"
                         + "  UPD_DTTM    VARCHAR(14),                       "+"\n"
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

        // DB 연결 종료
        DDL.closeConnection();
    }

    // 테이블 삭제 함수
    public void dropTable() throws SQLException {

        // 테이블 삭제
        ResultType result = DDL.dropTable("CW_BLOG_ATCL_LIST");

        // 테이블 삭제 결과 출력
        switch( result ) {
            case SUCCESS:
                System.out.println("테이블 삭제 완료.");
                break;
            case WARNING:
                System.out.println("테이블이 존재하지 않습니다.");
                break;
            case FAILURE:
                System.out.println("테이블 삭제 실패.");
                break;
        }

        // DB 연결 종료
        DDL.closeConnection();
    }

    // 데이터 입력 함수
    public void insert() throws SQLException {
        // 상수 설정
        //   - Data를 저장할 객체 생성
        //     * 입력/수정/삭제/조회 에서 공통으로 사용
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("BLOG_ID"   , "heodolf.tistory.com");
        dataMap.put("CATE_ID"   , "/");
        dataMap.put("ATCL_ID"   , "0");
        dataMap.put("URL"       , "https://heodolf.tistory.com/134");
        dataMap.put("TITLE"     , "[JAVA] Quartz 스케줄러 만들기 (1) - 실행");
        dataMap.put("WORK_YN"   , 0);

        // 데이터 입력
        int inserted = DML.insertBlogArticle(dataMap);
        if( inserted >= 0 ) {
            System.out.println(String.format("데이터 입력 성공: %d건", inserted));
        } else {
            System.out.println("데이터 입력 실패");
        }
    }

    // 다중 데이터 입력 함수
    public void insertList() throws SQLException {
        // 상수 설정
        //   - Data를 저장할 객체 생성
    	final List<Map<String, Object>> dataMapList = new ArrayList<Map<String, Object>>();

        final Map<String, Object> dataMap1 = new HashMap<String, Object>();
        dataMap1.put("BLOG_ID"   , "heodolf.tistory.com");
        dataMap1.put("CATE_ID"   , "/");
        dataMap1.put("ATCL_ID"   , "0");
        dataMap1.put("URL"       , "https://heodolf.tistory.com/134");
        dataMap1.put("TITLE"     , "[JAVA] Quartz 스케줄러 만들기 (1) - 실행");
        dataMap1.put("WORK_YN"   , 0);
        dataMapList.add(dataMap1);

        final Map<String, Object> dataMap2 = new HashMap<String, Object>();
        dataMap2.put("BLOG_ID"   , "heodolf.tistory.com");
        dataMap2.put("CATE_ID"   , "/");
        dataMap2.put("ATCL_ID"   , "1");
        dataMap2.put("URL"       , "https://heodolf.tistory.com/135");
        dataMap2.put("TITLE"     , "[JAVA] Quartz 스케줄러 만들기 (2) - Listener");
        dataMap2.put("WORK_YN"   , 0);
        dataMapList.add(dataMap2);

        // 데이터 입력
        int inserted = DML.insertBlogArticle(dataMapList);
        if( inserted >= 0 ) {
            System.out.println(String.format("데이터 입력 성공: %d건", inserted));
        } else {
            System.out.println("데이터 입력 실패");
        }
    }

    // 데이터 조회 함수
    public void select() {
        // 상수 설정
    	//   - 조회할 데이터
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("BLOG_ID"   , "heodolf.tistory.com");
        dataMap.put("CATE_ID"   , "/");

        // 데이터 조회
        //   - 수집한 블로그 목록 조
        List<Map<String, Object>> result = DQL.selectBlogArticleList(dataMap);

        // 조회 결과 출력
        DQL.printMapList(result);
    }

    // 데이터 수정 함수
    public void update() throws SQLException {
        // 상수 설정
    	//   - 조회할 데이터
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("BLOG_ID"   , "heodolf.tistory.com");
        dataMap.put("CATE_ID"   , "/");
        dataMap.put("ATCL_ID"   , "0");

    	//   - 수정할 데이터
        final Map<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put("WORK_YN"	, 1);
        updateMap.put("TITLE"	, "원하는 컬럼을 마음대로 수정!");

        // 수정 결과 출력
        int updated = DML.updateBlogArticle(dataMap, updateMap);
        if( updated >= 0 ) {
            System.out.println(String.format("데이터 수정 성공: %d건", updated));
        } else {
            System.out.println("데이터 수정 실패");
        }
    }

    // 데이터 삭제 함수
    public void delete() throws SQLException {
        // 상수 설정
    	//   - 조회할 데이터
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("BLOG_ID"   , "heodolf.tistory.com");
        dataMap.put("CATE_ID"   , "/");
        dataMap.put("ATCL_ID"   , "0");

        // 데이터 삭제
        int deleted = DML.deleteBlogArticle(dataMap);
        if( deleted >= 0 ) {
            System.out.println(String.format("데이터 삭제 성공: %d건", deleted));
        } else {
            System.out.println("데이터 삭제 실패");
        }
    }

    public static void main(String[] args) throws SQLException {
        MariaApp db = new MariaApp();

        db.dropTable();   	// 테이블 삭제
        db.createTable();   // 테이블 생성

        db.insert();		// 데이터 입력
        db.select();		// 데이터 조회

        db.update();		// 데이터 수정
        db.select();		// 데이터 조회

        db.delete();		// 데이터 삭제
        db.select();		// 데이터 조회

        db.insertList();	// 다중 데이터 입력
        db.select();		// 데이터 조회
    }
}
