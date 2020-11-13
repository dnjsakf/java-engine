package com.dochi.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SQLiteManager {

    // 상수 설정
    //   - DateFormat 설정
    public static final SimpleDateFormat DATETIME_FMT = new SimpleDateFormat("yyyyMMddHHmmss");

    //   - DATABASE 옵션
    private static String DEFAULT_URL = "jdbc:sqlite:quartz.db";
    private final boolean AUTO_COMMIT = false;
    private final int VALID_TIMEOUT = 500;

    // 변수 설정
    private Connection conn = null;
    private String url = null;

    // 생성자
    public SQLiteManager(){
        this(DEFAULT_URL);
    }
    public SQLiteManager(String url) {
        this.url = url;
    }

    // DB 연결 함수
    public void createConnection() {
        try {
            // Load JDBC Class
            //   - 나중에 다른 Database를 적용하려면, 해당 JDBC를 Load
            Class.forName("org.sqlite.JDBC");
            
            // DB 연결 객체 생성
            this.conn = DriverManager.getConnection(this.url);

            // 자동 커밋 옵션 설정
            this.conn.setAutoCommit(AUTO_COMMIT);
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // DB 연결 종료 함수
    public void closeConnection() {
        try {
            if( this.conn != null ) {
                this.conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.conn = null;
        }
    }

    // DB 재연결 함수
    public Connection ensureConnection() {
        try {
            if( this.conn == null || this.conn.isValid(VALID_TIMEOUT)) {
                closeConnection();      // 연결 종료
                createConnection();     // 연결
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return this.conn;
    }

    // DB 연결 객체 가져오기
    public Connection getConnection() {
        return this.conn;
    }
    
    // DB 기본 테이블 생성
    public int createTable() {
        // 상수설정
        //   - SQL
        final String sql = "CREATE TABLE IF NOT EXISTS CW_BLOG_ATCL_LIST ("+"\n"
                         + "  BLOG_ID     TEXT        UNIQUE,             "+"\n"
                         + "  CATE_ID     TEXT        UNIQUE,             "+"\n"
                         + "  ATCL_ID     TEXT        UNIQUE,             "+"\n"
                         + "  URL         TEXT        NOT NULL,           "+"\n"
                         + "  TITLE       TEXT        ,                   "+"\n"
                         + "  WORK_YN     INTEGER     DEFAULT 0,          "+"\n"
                         + "  REG_DTTM    TEXT,                           "+"\n"
                         + "  UPD_DTTM    TEXT                            "+"\n"
                         + ")";

        
        // 변수설정
        //   - Database 변수
        Connection _conn = ensureConnection();
        Statement stmt = null;

        //   - 생성 결과 변수
        int success = -1;

        try {
            // Statement 객체  생성
            stmt = _conn.createStatement();
            
            // 테이블 생성
            success = stmt.execute(sql) ? 1 : 0;
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            success = -1;

        } finally {
            // Statement 종료
            if( stmt != null ) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // 결과 반환
        //   - 테이블 생성 결과
        //     * false: 테이블이 이미 생성되어있거나 실패
        //     * true: 생성 완료
        return success;
    }
    
    // 데이터 조회 함수
    public List<Map<String, Object>> select(Map<String, Object> dataMap){
        // 상수설정
        //   - SQL
        final String SQL = "SELECT T1.BLOG_ID           "+"\n"
                         + "     , T1.CATE_ID           "+"\n"
                         + "     , T1.ATCL_ID           "+"\n"
                         + "     , T1.TITLE             "+"\n"
                         + "     , T1.URL               "+"\n"
                         + "     , T1.WORK_YN           "+"\n"
                         + "     , T1.REG_DTTM          "+"\n"
                         + "     , T1.UPD_DTTM          "+"\n"
                         + "  FROM CW_BLOG_ATCL_LIST T1 "+"\n"
                         + " WHERE 1=1                  "+"\n"
                         + "   AND T1.BLOG_ID = ?       "+"\n"
                         + "   AND T1.CATE_ID = ?       "+"\n"
                         + "   AND T1.ATCL_ID = ?       "+"\n"
                         ;
        
        //   - 조회 결과 변수
        final Set<String> columnNames = new HashSet<String>();        
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();

        // 변수설정
        //   - Database 변수
        Connection _conn = ensureConnection();
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        
        try {
            // PreparedStatement 객체 생성
            pstmt = _conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, dataMap.get("BLOG_ID"));
            pstmt.setObject(2, dataMap.get("CATE_ID"));
            pstmt.setObject(3, dataMap.get("ATCL_ID"));
            
            // 데이터 조회
            ResultSet rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
                resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
            // PreparedStatement 종료
            if( pstmt != null ) {
                try {
                    pstmt.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }
        
        // 조회 결과 출력
        printMapList(selected);

        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
    }
    
    // 데이터 삽입 함수
    public int insert(Map<String, Object> dataMap) {
        final String sql = "INSERT INTO CW_BLOG_ATCL_LIST ("+"\n"
                   + "    BLOG_ID,                         "+"\n"
                   + "    CATE_ID,                         "+"\n"
                   + "    ATCL_ID,                         "+"\n"
                   + "    URL,                             "+"\n"
                   + "    TITLE,                           "+"\n"
                   + "    WORK_YN,                         "+"\n"
                   + "    REG_DTTM                         "+"\n"
                   + ") VALUES (                           "+"\n"
                   + "    ?,                               "+"\n"
                   + "    ?,                               "+"\n"
                   + "    ?,                               "+"\n"
                   + "    ?,                               "+"\n"
                   + "    ?,                               "+"\n"
                   + "    ?,                               "+"\n"
                   + "    ?                                "+"\n"
                   + ")";
        
        // 변수설정
        //   - Database 변수
        Connection _conn = ensureConnection();
        PreparedStatement pstmt = null;
        
        //   - 입력 결과 변수
        int inserted = 0;
        
        try {
            // PreparedStatement 생성
            pstmt = _conn.prepareStatement(sql);
            
            // 입력 데이터 매핑
            pstmt.setObject(1, dataMap.get("BLOG_ID"));
            pstmt.setObject(2, dataMap.get("CATE_ID"));
            pstmt.setObject(3, dataMap.get("ATCL_ID"));
            pstmt.setObject(4, dataMap.get("URL"));
            pstmt.setObject(5, dataMap.get("TITLE"));
            pstmt.setObject(6, dataMap.get("WORK_YN"));
            pstmt.setObject(7, DATETIME_FMT.format(new Date()));
            
            // 쿼리 실행
            pstmt.executeUpdate();
            
            // COMMIT
            this.conn.commit();

            // 입력건수  조회
            inserted = pstmt.getUpdateCount();
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            inserted = -1;

        } finally {
            // PreparedStatement 종료
            if( pstmt != null ) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        // 결과 반환
        //   - 입력된 데이터 건수
        return inserted;
    }
    
    // 데이터 수정 함수
    public int update(Map<String, Object> dataMap, Map<String, Object> updateMap) {
        // 상수 설정
        //   - 수정 컬럼 적용을 위한 동적 SQL 생성
        final StringBuilder sqlBuilder = new StringBuilder();
        final List<String> updateSeq = new ArrayList<String>();
        
        sqlBuilder.append("UPDATE CW_BLOG_ATCL_LIST \n");
        sqlBuilder.append("SET ");
        sqlBuilder.append(
            updateMap.keySet().stream().reduce("", (prev, current)->{
                updateSeq.add(current);
                
                current = current + " = ? ";         
                if( !"".equals(prev) ) {
                    prev = prev+", ";
                }
                
                return prev+current+"\n";
            })      
        );
        sqlBuilder.append("WHERE 1=1 \n");
        sqlBuilder.append("  AND BLOG_ID = ? \n");
        sqlBuilder.append("  AND CATE_ID = ? \n");
        sqlBuilder.append("  AND ATCL_ID = ? \n");
        
        //   - 최종 SQL
        final String SQL = sqlBuilder.toString();

        // 변수설정
        //   - Database 변수
        Connection _conn = ensureConnection();
        PreparedStatement pstmt = null;

        //   - 수정 결과 변수
        int updated = 0;
        
        try {
            // PreparedStatement 객체 생성
            pstmt = _conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            //   - 수정
            int updateSize = updateSeq.size();
            for(int i=1; i<=updateSize; i++ ) {
                pstmt.setObject(i, updateMap.get(updateSeq.get(i-1)));
            }
            
            //   - 조건
            pstmt.setObject(updateSize+1, dataMap.get("BLOG_ID"));
            pstmt.setObject(updateSize+2, dataMap.get("CATE_ID"));
            pstmt.setObject(updateSize+3, dataMap.get("ATCL_ID"));
            
            // 데이터 조회
            updated = pstmt.executeUpdate();
            
            // COMMIT
            _conn.commit();
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            updated = -1;
            
        } finally  {
            // PreparedStatement 종료
            if( pstmt != null ) {
                try {
                    pstmt.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }

        // 결과 반환
        //   - 수정된 데이터 건수
        return updated;
    }
    
    // 데이터 삭제
    public int delete(Map<String, Object> dataMap) {
        final String sql = "DELETE FROM CW_BLOG_ATCL_LIST  "+"\n"
                         + " WHERE 1=1                     "+"\n"
                         + "   AND BLOG_ID = ?             "+"\n"
                         + "   AND CATE_ID = ?             "+"\n"
                         + "   AND ATCL_ID = ?             "+"\n"
                         ;

        // 변수설정
        //   - Database 변수
        Connection _conn = ensureConnection();
        PreparedStatement pstmt = null;

        //   - 삭제 결과 변수
        int deleted = 0;
        
        try {
            // PreparedStatement 객체 생성
            pstmt = _conn.prepareStatement(sql);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, dataMap.get("BLOG_ID"));
            pstmt.setObject(2, dataMap.get("CATE_ID"));
            pstmt.setObject(3, dataMap.get("ATCL_ID"));
            
            // 데이터 조회
            deleted = pstmt.executeUpdate();
            
            // COMMIT
            _conn.commit();
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            deleted = -1;
            
        } finally  {
            // PreparedStatement 종료
            if( pstmt != null ) {
                try {
                    pstmt.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }

        // 결과 반환
        //   - 삭제된 데이터 건수
        return deleted;
    }

    // 조회 결과 출력 함수
    public void printMapList(List<Map<String, Object>> mapList) {
        mapList.forEach((map)->{
            final StringBuilder sb = new StringBuilder();
            
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            
            sb.append("{");
            entries.forEach(( entry )->{
                sb.append('"')
                    .append(entry.getKey())
                    .append("\": \"")
                    .append(entry.getValue())
                    .append("\", ");
            });
            sb.append("}");
            
            System.out.println(sb.toString());
        });
        
    }
    
    public static void main(String[] args) throws SQLException {
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
        
        // 변수 설정
        //   - Database Manager 객체 생성
        SQLiteManager manager = new SQLiteManager();
        
        
        // 테이블 생성
        int created = manager.createTable();
        if( created == 1 ) {
            System.out.println("테이블 생성 완료.");
        } else if ( created == 0 ) {
            System.out.println("테이블이 이미 존재합니다.");
        } else {
            System.out.println("테이블 생성 실패.");
        }
        

        // 데이터 입력
        //   - 입력전 중복 데이터 조회
        if( manager.select(dataMap).size() == 0 ) {
            // 데이터 입력
            int inserted = manager.insert(dataMap);
            if( inserted >= 0 ) {
                System.out.println(String.format("데이터 입력 성공: %d건", inserted));
            } else {
                System.out.println("데이터 입력 실패");
            }
        } else {
            System.out.println("중복된 데이터가 존재합니다.");
        }
        
        
        // 데이터 조회
        List<Map<String, Object>> selected = manager.select(dataMap);
        if( selected != null ) {
            System.out.println(String.format("데이터 조회 성공: %d건", selected.size()));
        } else {
            System.out.println("데이터 조회 실패");
        }
        
        
        // 데이터 수정
        final Map<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put("WORK_YN", 1);
        updateMap.put("TITLE", "원하는 컬럼을 마음대로 수정!");
        
        int updated = manager.update(dataMap, updateMap);
        if( updated >= 0 ) {
            manager.select(dataMap);
            System.out.println(String.format("데이터 수정 성공: %d건", updated));
        } else {
            System.out.println("데이터 수정 실패");
        }
        
        
        // 데이터 삭제
        int deleted = manager.delete(dataMap);
        if( deleted >= 0 ) {
            System.out.println(String.format("데이터 삭제 성공: %d건", deleted));
        } else {
            System.out.println("데이터 삭제 실패");
        }
    }
}
