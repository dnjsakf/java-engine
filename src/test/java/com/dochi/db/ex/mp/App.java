package com.dochi.db.ex.mp;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.dochi.db.ex.mp.service.DateMapperService;

public class App {
    
    private DateMapperService service = new DateMapperService();
    
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
