package com.dochi.labs.ex.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class SQLiteManager {
    
    // 상수 설정
    //   - DateFormat 설정
    public static final SimpleDateFormat DATETIME_FMT = new SimpleDateFormat("yyyyMMddHHmmss");

    //   - Database 변수( for SQLite )
    private static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String SQLITE_URL = "jdbc:sqlite:quartz.db";
    
    //   - Database 변수( for MariaDB )
    private static final String MARIADB_JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String MARIADB_URL = "jdbc:mariadb://localhost:3306/dochi";
    
    //  - Database 옵션 변수
    private static final boolean OPT_AUTO_COMMIT = false;
    private static final int OPT_VALID_TIMEOUT = 500;

    // 변수 설정
    //   - Database 접속 정보 변수
    private String driver = null;
    private Connection conn = null;
    private String url = null;
    private String username = null;
    private String password = null;

    // 생성자
    public SQLiteManager(){
        this(SQLITE_URL, null, null);
    }
    public SQLiteManager(String url) {
        this(url, null, null);
    }
    public SQLiteManager(String url, String username, String password) {
        // JDBC Driver 설정
        if( "mariadb".equals( url.split(":")[1] ) ) {
            this.driver = MARIADB_JDBC_DRIVER;
        } else {
            this.driver = SQLITE_JDBC_DRIVER;
        }
        
        this.url = url;
        this.username = username;
        this.password = password;
    }

    // DB 연결 함수
    public Connection createConnection() {
        try {
            // JDBC Driver Class 로드
            Class.forName(this.driver);
            
            // DB 연결 객체 생성
            this.conn = DriverManager.getConnection(this.url, this.username, this.password);

            // 옵션 설정
            //   - 자동 커밋
            this.conn.setAutoCommit(OPT_AUTO_COMMIT);
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        
        return this.conn;
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
            if( this.conn == null || this.conn.isValid(OPT_VALID_TIMEOUT) ) {
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
