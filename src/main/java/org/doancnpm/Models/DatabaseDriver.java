package org.doancnpm.Models;

import java.sql.Connection;

import static java.sql.DriverManager.getConnection;

public class DatabaseDriver {
    private static String DB_URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=QuanLyDaiLy;"
            + "encrypt=true; trustServerCertificate=true;"
            + "trustStore=storeName;" +
            "trustStorePassword=storePassword;";
    private static String USER_NAME = "sa";
    private static String PASSWORD = "123456789";
    private static Connection dataBaseConnection = null;
    public static Connection getConnect() {
        try {
            if(dataBaseConnection==null){
                dataBaseConnection = getConnection(DB_URL, USER_NAME, PASSWORD);
                return dataBaseConnection;
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
