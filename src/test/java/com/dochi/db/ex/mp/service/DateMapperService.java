package com.dochi.db.ex.mp.service;

import java.util.Map;

import com.dochi.db.ex.mp.service.common.MapperService;

public class DateMapperService extends MapperService {
    
    public Map<String, Object> selectDateTime(Map<String, Object> paramMap) {
        return executeSQL(paramMap);
    }
    
    public Map<String, Object> selectDate(Map<String, Object> paramMap) {
        return executeSQL(paramMap);
    }

}
