package org.doancnpm.Models;

import org.doancnpm.Mode;

import java.sql.Connection;

import static java.sql.DriverManager.getConnection;

public class DatabaseDriver {
    private static String DB_URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=QuanLyDaiLy;"
            + "encrypt=true; trustServerCertificate=true;"
            + "trustStore=storeName;" +
            "trustStorePassword=storePassword;";
    private static String DB_URL_TEST = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=QuanLyDaiLy_Test;"
            + "encrypt=true; trustServerCertificate=true;"
            + "trustStore=storeName;" +
            "trustStorePassword=storePassword;";
    private static String USER_NAME = "sa";
    private static String PASSWORD = "123456789";
    private static Connection dataBaseConnection = null;
    public static Connection getConnect() {
        try {
            if(dataBaseConnection==null){
                if(Mode.TestMode){
                    dataBaseConnection = getConnection(DB_URL_TEST, USER_NAME, PASSWORD);
                    return dataBaseConnection;
                }else{
                    dataBaseConnection = getConnection(DB_URL, USER_NAME, PASSWORD);
                    return dataBaseConnection;
                }

            }
            else{
                return dataBaseConnection;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
