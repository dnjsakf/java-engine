package com.dochi.quartz.crawl_db.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DMLService extends DBManager {

    // 상수 설정
    //   - DateFormat 설정
	private static final SimpleDateFormat DATETIME_FMT = new SimpleDateFormat("yyyyMMddHHmmss");
	//   - Batch건수 설정
	//     * 100건마다 적재 시도
    private static final int OPT_BATCH_SIZE = 100;

    // 생성자
    public DMLService() {

    }
    public DMLService(String url) {
        this(url, null, null);
    }
    public DMLService(String url, String username, String password) {
        super(url, username, password);
    }

    // 다중 데이터 삽입 함수
    public int insertBlogArticle(List<Map<String, Object>> dataMapList) throws SQLException {
        // 상수 설정
    	//   - 데이터 입력 SQL 변수
        final String SQL = "INSERT INTO CW_BLOG_ATCL_LIST ("+"\n"
                         + "    BLOG_ID,                         "+"\n"
                         + "    CATE_ID,                         "+"\n"
                         + "    ATCL_ID,                         "+"\n"
                         + "    URL,                             "+"\n"
                         + "    TITLE,                           "+"\n"
                         + "    PAGE,                            "+"\n"
                         + "    WORK_YN,                         "+"\n"
                         + "    REG_DTTM                         "+"\n"
                         + ") VALUES (                           "+"\n"
                         + "    ?,                               "+"\n"
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
        Connection conn = ensureConnection();
        PreparedStatement pstmt = null;

        //   - 입력 결과 변수
        int inserted = 0;

        try {
            // PreparedStatement 생성
            pstmt = conn.prepareStatement(SQL);

            // 입력받은 데이터를 Batch 처리
            for(int i = 0; i < dataMapList.size(); i++ ) {
            	// 입력 데이터 객체
            	Map<String, Object> dataMap = dataMapList.get(i);

                // 입력 데이터 매핑
                pstmt.setObject(1, dataMap.get("BLOG_ID"));
                pstmt.setObject(2, dataMap.get("CATE_ID"));
                pstmt.setObject(3, dataMap.get("ATCL_ID"));
                pstmt.setObject(4, dataMap.get("URL"));
                pstmt.setObject(5, dataMap.get("TITLE"));
                pstmt.setObject(6, dataMap.get("PAGE"));
                pstmt.setObject(7, dataMap.get("WORK_YN"));
                pstmt.setObject(8, DATETIME_FMT.format(new Date()));

                // Batch에 추가
                pstmt.addBatch();

                // Batch 실행
                if( i % OPT_BATCH_SIZE == 0 ) {
                    inserted += pstmt.executeBatch().length;
                }
            }

            // Batch 실행
            inserted += pstmt.executeBatch().length;

            // 트랜잭션 COMMIT
            conn.commit();

        } catch (SQLException e) {
            // 오류출력
            System.out.println(e.getMessage());

            // 오류
            inserted = -1;

            // 트랜잭션 ROLLBACK
            if( conn != null ) {
            	conn.rollback();
            }

        } finally {
            try {
                // PreparedStatement 종료
                if( pstmt != null ) {
                	pstmt.close();
                }

                // Connection 종료
                closeConnection();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // 결과 반환
        //   - 입력된 데이터 건수
        return inserted;
    }

    // 데이터 삽입 함수
    public int insertBlogArticle(Map<String, Object> dataMap) throws SQLException {
        // 상수 설정
    	//   - 데이터 입력 SQL 변수
        final String SQL = "INSERT INTO CW_BLOG_ATCL_LIST ("+"\n"
                   + "    BLOG_ID,                         "+"\n"
                   + "    CATE_ID,                         "+"\n"
                   + "    ATCL_ID,                         "+"\n"
                   + "    URL,                             "+"\n"
                   + "    TITLE,                           "+"\n"
                   + "    PAGE,                            "+"\n"
                   + "    WORK_YN,                         "+"\n"
                   + "    REG_DTTM                         "+"\n"
                   + ") VALUES (                           "+"\n"
                   + "    ?,                               "+"\n"
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
        Connection conn = ensureConnection();
        PreparedStatement pstmt = null;

        //   - 입력 결과 변수
        int inserted = 0;

        try {
            // PreparedStatement 생성
            pstmt = conn.prepareStatement(SQL);

            // 입력 데이터 매핑
            pstmt.setObject(1, dataMap.get("BLOG_ID"));
            pstmt.setObject(2, dataMap.get("CATE_ID"));
            pstmt.setObject(3, dataMap.get("ATCL_ID"));
            pstmt.setObject(4, dataMap.get("URL"));
            pstmt.setObject(5, dataMap.get("TITLE"));
            pstmt.setObject(6, dataMap.get("PAGE"));
            pstmt.setObject(7, dataMap.get("WORK_YN"));
            pstmt.setObject(8, DATETIME_FMT.format(new Date()));

            // SQL 실행
            pstmt.executeUpdate();

            // 입력 건수 조회
            inserted = pstmt.getUpdateCount();

            // 트랜잭션 COMMIT
            conn.commit();

        } catch (SQLException e) {
            // 오류출력
            System.out.println(e.getMessage());

            // 오류
            inserted = -1;

            // 트랜잭션 ROLLBACK
            if( conn != null ) {
            	conn.rollback();
            }

        } finally {
            try {
                // PreparedStatement 종료
                if( pstmt != null ) {
                	pstmt.close();
                }

                // Connection 종료
                closeConnection();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // 결과 반환
        //   - 입력된 데이터 건수
        return inserted;
    }

    // 데이터 수정 함수
    public int updateBlogArticle(Map<String, Object> dataMap, Map<String, Object> updateMap) throws SQLException {
        // 상수 설정
        //   - 수정할 컬럼을 동적으로 작성하기 위한 SQL 변수
        final StringBuilder sqlBuilder = new StringBuilder();
        //   - PreparedStatement 객체에 데이터를 매핑할 순서를 저장할 변수
        final List<String> updateSeq = new ArrayList<String>();

        // 수정일자 추가
        updateMap.put("UPD_DTTM", DATETIME_FMT.format(new Date()));

        // 동적 SQL 작성
        sqlBuilder.append("UPDATE CW_BLOG_ATCL_LIST \n");
        sqlBuilder.append("SET ");
        sqlBuilder.append(
        	// 수정할 Data를 조회하여 동적 SQL 작성
            updateMap.keySet().stream().reduce("", (prev, current)->{
            	// 데이터를 매핑할 순서를 저장
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
        Connection conn = ensureConnection();
        PreparedStatement pstmt = null;

        //   - 수정 결과 변수
        int updated = 0;

        try {
            // PreparedStatement 객체 생성
            pstmt = conn.prepareStatement(SQL);

            // 수정할 데이터 매핑
            int updateSize = updateSeq.size();
            for(int i=1; i<=updateSize; i++ ) {
                pstmt.setObject(i, updateMap.get(updateSeq.get(i-1)));
            }

            // 수정할 데이터 조건 매핑
            pstmt.setObject(updateSize+1, dataMap.get("BLOG_ID"));
            pstmt.setObject(updateSize+2, dataMap.get("CATE_ID"));
            pstmt.setObject(updateSize+3, dataMap.get("ATCL_ID"));

            // SQL 실행
            pstmt.executeUpdate();

            // 수정 건수 조회
    		updated = pstmt.getUpdateCount();

            // 트랜잭션 COMMIT
            conn.commit();

        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());

            // 오류
            updated = -1;

            // 트랜잭션 ROLLBACK
            conn.rollback();

        } finally  {
            try {
                // PreparedStatement 종료
                if( pstmt != null ) {
                	pstmt.close();
                }

                // Connection 종료
                closeConnection();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // 결과 반환
        //   - 수정된 데이터 건수
        return updated;
    }

    // 데이터 삭제 함수
    public int deleteBlogArticle(Map<String, Object> dataMap) throws SQLException {
        final String sql = "DELETE FROM CW_BLOG_ATCL_LIST  "+"\n"
                         + " WHERE 1=1                     "+"\n"
                         + "   AND BLOG_ID = ?             "+"\n"
                         + "   AND CATE_ID = ?             "+"\n"
                         + "   AND ATCL_ID = ?             "+"\n"
                         ;

        // 변수설정
        //   - Database 변수
        Connection conn = ensureConnection();
        PreparedStatement pstmt = null;

        //   - 삭제 결과 변수
        int deleted = 0;

        try {
            // PreparedStatement 객체 생성
            pstmt = conn.prepareStatement(sql);

            // 조회 데이터 조건 매핑
            pstmt.setObject(1, dataMap.get("BLOG_ID"));
            pstmt.setObject(2, dataMap.get("CATE_ID"));
            pstmt.setObject(3, dataMap.get("ATCL_ID"));

            // SQL 실행
            pstmt.executeUpdate();

            // 데이터 삭제 건수
            deleted = pstmt.getUpdateCount();

            // 트랜잭션 COMMIT
            conn.commit();

        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());

            // 오류
            deleted = -1;

            // 트랜잭션 ROLLBACK
            conn.commit();

        } finally  {
            try {
                // PreparedStatement 종료
                if( pstmt != null ) {
                	pstmt.close();
                }

                // Connection 종료
                closeConnection();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // 결과 반환
        //   - 삭제된 데이터 건수
        return deleted;
    }
}
