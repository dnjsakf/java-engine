package com.dochi.quartz.crawl_db.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    // 상수 설정
    //   - JDBC Driver 변수
    private static final String DEFAULT_JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String MARIADB_JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    
    //   - Database URL 변수
    private static final String DEFAULT_DB_URL = "jdbc:sqlite::memory";

    //   - Database 옵션 변수
    private static final boolean OPT_AUTO_COMMIT = false;
    private static final int OPT_VALID_TIMEOUT = 500;

    // 변수 설정
    //   - Database 접속 정보 변수
    private Connection conn = null;
    private String driver = null;
    private String url = null;
    private String username = null;
    private String password = null;

    // 생성자
    public DBManager(){
        this(DEFAULT_DB_URL);
    }
    public DBManager(String url) {
        this(url, null, null);
    }
    public DBManager(String url, String username, String password) {
        // JDBC Driver 설정
    	//   - Database 이름 가져오기
    	final String DB_NAME = url.split(":")[1];
    	
    	//   - Database에 따른 Driver 매핑
    	if( "mariadb".equalsIgnoreCase(DB_NAME) ) {
    		this.driver = MARIADB_JDBC_DRIVER;
    	} else if ( "sqlite".equalsIgnoreCase(DB_NAME) ) {
    		this.driver = SQLITE_JDBC_DRIVER;
    	} else {
    		this.driver = DEFAULT_JDBC_DRIVER;
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
