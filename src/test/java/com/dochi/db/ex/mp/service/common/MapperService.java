package com.dochi.db.ex.mp.service.common;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.dochi.quartz.crawl_db.db.DBManager;

public class MapperService extends DBManager {
    // 상수설정
    //   - Mapper 파일명
    private static final String DEFAULT_MAPPER_PATH = "src/test/java/com/dochi/db/ex/mp/mapper";

    // 변수설정
    //   - Mapper 변수
    private SAXBuilder builder = new SAXBuilder();
    private Document document = null;
    private MapperElement root = null;
    
    public MapperService() {
        super("jdbc:sqlite::memory:");
        
        String filename = getFilename();
        try {
            document = builder.build(new File(DEFAULT_MAPPER_PATH, filename));
            root = new MapperElement(document.getRootElement());
            
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }
    
    // 파일명 가져오기
    //   - 현재 클래스에서 'Service'를 제외한 이름을 xml파일명으로 사용
    //     * DateMapperService -> dateMapper.xml
    private String getFilename() {
        String className = this.getClass().getSimpleName();
        String mapperName = className.split("Service")[0];
        
        char[] chars = mapperName.toCharArray();
        
        if( chars[0] >= 65 && chars[0] <= 90 ) {
            // A to Z
            chars[0] = (char)(chars[0]+32);
        }
        
        return String.valueOf(chars)+".xml";
    }

    // 특정 키워드를 찾아서 PreparedStatement로 변환해주는 함수
    //   - ${변수명} => Integer 
    //   - #{변수명} => String
    private SQLInfo parsing(String sql, Map<String, Object> paramMap) {
        List<Object> data = new ArrayList<Object>();
        
        Vector<Pattern> patterns = new Vector<Pattern>();
        patterns.add(Pattern.compile("((#|$)\\{([a-zA-Z_0-9]+)\\})", Pattern.DOTALL));
        
        Iterator<Pattern> iter = patterns.iterator();
        Matcher matcher = null;
        
        while( iter.hasNext() ) {
            Pattern pattern = iter.next();
            matcher = pattern.matcher(sql);

            while( matcher.find() ) {
                String type = matcher.group(2);
                String source = matcher.group(1);
                String targetKey = matcher.group(3);
                
                if( paramMap.containsKey(targetKey) ) {
                    sql = sql.replace(source, "?");
                    
                    data.add(paramMap.get(targetKey));
                }
            }
        }
        
        return new SQLInfo(sql, data);
    }
    
    // XML에서 id가 현재 함수명인 SQL 조회
    protected Map<String, Object> executeSQL(Map<String, Object> paramMap) {
        // 상수 설정
        //   - executeSQL을 호출한 함수명  변수
        final String METHOD_NAME = Thread.currentThread().getStackTrace()[2].getMethodName();
        
        //   - id가 현재 함수명인 SQL 변수
        final String rawSQL = this.root.getSQL(METHOD_NAME); 
        
        // 변수 설정
        //   - SQL을 PreparedStatement로 치환한 객체 변수
        SQLInfo info = parsing(rawSQL, paramMap);
        
        //   - 변환된 데이터 변수
        String SQL = info.getSql();
        List<Object> dataList = info.getData();
        
        //   - Database 연결 변수
        Connection conn = null;
        
        //   - 데이터 조회 변수
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsMeta = null;
        
        //   - 결과 변수
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        try {
            conn = ensureConnection();
            
            pstmt = conn.prepareStatement(SQL);
            
            // Data Mapping
            for(int i = 1; i <= dataList.size(); i++) {
                Object data = dataList.get(i-1);
                
                if( data instanceof Integer ) {
                    pstmt.setInt(i, (Integer)data);
                } else {
                    pstmt.setString(i, (String)data);
                }
            }
            
            rs = pstmt.executeQuery();
            rsMeta = rs.getMetaData();
            
            // 결과 값을 Map 객체로 치환
            //   - ResultSet -> Map<String, Object>
            for(int i = 1; i <= rsMeta.getColumnCount(); i++ ) {
                String columnName = rsMeta.getColumnName(i);
                
                resultMap.put(columnName, rs.getObject(i));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            
        } finally {
            // Close ResultSet
            try { if( rs != null ) { rs.close(); } 
            } catch (SQLException e) {
                e.printStackTrace();
                
            } finally { rs = null; }
            
            // Close PreparedStatement
            try { if( pstmt != null ) { pstmt.close(); } 
            } catch (SQLException e) {
                e.printStackTrace();
                
            } finally { pstmt = null; }
            
            // Close Connection
            closeConnection();
        }
        
        return resultMap;
    }
}