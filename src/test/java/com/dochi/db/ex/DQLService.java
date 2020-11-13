package com.dochi.db.ex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DQLService extends SQLiteManager {
    
    // 생성자
    public DQLService() {
        
    }
    public DQLService(String url) {
        super(url);
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
}
