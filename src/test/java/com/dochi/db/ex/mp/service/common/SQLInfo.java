package com.dochi.db.ex.mp.service.common;

import java.util.List;

//변환된 SQL과 DATA를 저장할 클래스
public class SQLInfo {
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

