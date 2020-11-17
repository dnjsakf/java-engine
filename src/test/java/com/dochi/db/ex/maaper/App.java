package com.dochi.db.ex.maaper;

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
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.dochi.quartz.crawl_db.db.DBManager;

class Mapper {
    
    private Element element = null;
    
    public Mapper(Element element) {
        this.element = element;
    }
    
    // XML 탐색기
    //   - 이름, 속성값으로 탐색
    //   - "select", "getDateTime"
    public String getSQL(String attrValue) {
        return getChild("sql", "id", attrValue).getText();
    }
    
    //   - 속성명, 속성값으로 탐색
    public Mapper getChild(String attrName, String attrValue) {
        return getChild(null, attrName, attrValue);
    }

    //   - 이름, 속성명, 속성값으로 탐색
    public Mapper getChild(String name, String attrName, String attrValue) {
        List<?> children = null;
        
        if( name != null ) {
            children = this.element.getChildren(name);
        } else {
            children = this.element.getChildren();
        }
        
        Iterator<?> iter = children.iterator();
        while(iter.hasNext()) {
            Element element = (Element) iter.next(); 
            String elementAttrValue = element.getAttributeValue(attrName);
            
            if( elementAttrValue != null && elementAttrValue.equalsIgnoreCase(attrValue) ) {
                return new Mapper(element);
            }
        }
        
        return null;
    }
    
    public String getText() {
        return this.element.getText();
    }
    public List<?> getChildren() {
        return this.element.getChildren();
    }
    public List<?> getChildren(String name) {
        return this.element.getChildren(name);
    }
    public String getAttributeValue(String attrName) {
        return this.element.getAttributeValue(attrName);
    }
    public String getAttributeValue(String attrName, String defaultValue) {
        return this.element.getAttributeValue(attrName, defaultValue);
    }
}

class MapperService extends DBManager {
    class SQLInfo {
        private String sql = null;
        private List<Object> data = null;
        
        public SQLInfo(String sql, List<Object> data) {
            this.sql = sql;
            this.data = data;
        }
        public String getSql() {
            return sql;
        }
        public void setSql(String sql) {
            this.sql = sql;
        }
        public List<Object> getData() {
            return data;
        }
        public void setData(List<Object> data) {
            this.data = data;
        }
    }
    
    // 상수설정
    //   - Mapper 파일명
    private final String XML_FILE_NAME = "src/test/java/com/dochi/db/ex/maaper/MyMapper.xml";

    // 변수설정
    //   - Mapper 변수
    private SAXBuilder builder = new SAXBuilder();
    private Document document = null;
    private Mapper root = null;
    
    public MapperService() {
        super("jdbc:sqlite::memory:");
        
        try {
            document = builder.build(XML_FILE_NAME);
            root = new Mapper(document.getRootElement());
            
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public SQLInfo maching(String sql, Map<String, Object> paramMap) {
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
    
    public Map<String, Object> executeSQL(SQLInfo info){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        Connection conn = ensureConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsMeta = null;
        
        try {
            pstmt = conn.prepareStatement(info.getSql());
            
            for(int i = 1; i <= info.getData().size(); i++) {
                Object data = info.getData().get(i-1);
                if( data instanceof Integer ) {
                    pstmt.setInt(i, (Integer)data);
                } else {
                    pstmt.setString(i, (String)data);
                }
            }
            
            rs = pstmt.executeQuery();
            rsMeta = rs.getMetaData();
            
            for(int i = 1; i <= rsMeta.getColumnCount(); i++ ) {
                String columnName = rsMeta.getColumnName(i);
                
                resultMap.put(columnName, rs.getObject(i));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        
        return resultMap;
    }
    
    public Map<String, Object> selectDateTime(Map<String, Object> paramMap) {
        final String METHOD_NAME = Thread.currentThread().getStackTrace()[1].getMethodName();
        final String sql = this.root.getSQL(METHOD_NAME); 

        return executeSQL(maching(sql, paramMap));
    }
    
    public Map<String, Object> selectDate(Map<String, Object> paramMap) {
        final String METHOD_NAME = Thread.currentThread().getStackTrace()[1].getMethodName();
        final String sql = this.root.getSQL(METHOD_NAME); 

        return executeSQL(maching(sql, paramMap));
    }
}


public class App {
    
    private MapperService service = new MapperService();
    
    App(){
    }
    
    public void run()  throws SQLException{
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("testValue", 10);
        
        System.out.println( service.selectDateTime(paramMap) );
        System.out.println( service.selectDate(paramMap) );
    }

    public static void main(String[] args) throws SQLException {
        App app = new App();
        
        app.run();
    }
}
