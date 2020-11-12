package com.dochi.db.ex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

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
            // DB 연결 객체 생성
            this.conn = DriverManager.getConnection(this.url);

            // 자동 커밋 옵션 설정
            this.conn.setAutoCommit(AUTO_COMMIT);
            
        } catch (SQLException e) {
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
}
