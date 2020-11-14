package com.dochi.labs.ex.db;

import java.sql.SQLException;

import com.dochi.labs.ex.db.DDLService.ResultType;

public class App {

    // 변수 생성
    //   - DDL 객체 변수
    private DDLService DDL = null;

    public App() {
        // DDL 객체 for MariaDB
        this.DDL = new DDLService(
                "jdbc:mariadb://localhost:3306/dochi_dev",  // url
                "dochi",                                    // username
                "dochi"                                     // password
            );

        // DDL 객체 for SQLite
        this.DDL = new DDLService("jdbc:sqlite:dochi.db");
    }

    // 테이블 생성 함수
    public void createTable() throws SQLException {
        final String SQL = "CREATE TABLE IF NOT EXISTS CW_BLOG_ATCL_LIST (   "+"\n"
                         + "  BLOG_ID     VARCHAR        UNIQUE,             "+"\n"
                         + "  CATE_ID     VARCHAR        UNIQUE,             "+"\n"
                         + "  ATCL_ID     VARCHAR        UNIQUE,             "+"\n"
                         + "  URL         VARCHAR        NOT NULL,           "+"\n"
                         + "  TITLE       VARCHAR,                           "+"\n"
                         + "  WORK_YN     INTEGER        DEFAULT 0,          "+"\n"
                         + "  REG_DTTM    VARCHAR,                           "+"\n"
                         + "  UPD_DTTM    VARCHAR                            "+"\n"
                         + ")";

        ResultType result = DDL.createTable("CW_BLOG_ATCL_LIST", SQL);

        // 테이블 생성
        switch(result) {
            case SUCCESS:
                System.out.println("테이블 생성 완료.");
                break;
            case WARNING:
                System.out.println("테이블이 이미 존재합니다.");
                break;
            case FAILURE:
                System.out.println("테이블 생성 실패.");
                break;
        }

        // DB 연결 종료
        DDL.closeConnection();
    }

    // 테이블 삭제 함수
    public void dropTable() throws SQLException {

        ResultType result = DDL.dropTable("CW_BLOG_ATCL_LIST");

        // 테이블 삭제
        switch( result ) {
            case SUCCESS:
                System.out.println("테이블 삭제 완료.");
                break;
            case WARNING:
                System.out.println("테이블이 존재하지 않습니다.");
                break;
            case FAILURE:
                System.out.println("테이블 삭제 실패.");
                break;
        }

        // DB 연결 종료
        DDL.closeConnection();
    }

    public static void main(String[] args) throws SQLException {
        App sqlite = new App();

        sqlite.dropTable();
        sqlite.createTable();
        sqlite.createTable();
        sqlite.dropTable();
    }
}
