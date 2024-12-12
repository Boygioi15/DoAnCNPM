package org.doancnpm.DAO;

import org.doancnpm.Models.DaiLy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class Agent_Insert_DAO_Test {

    private Connection connection;
    private DaiLyDAO daiLyDAO;

    @BeforeEach
    void setUp() throws SQLException {
        // Kết nối đến SQL Server thực tế
        connection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=QUANLYDAILY;user=sa;password=123456;");
        // Xóa dữ liệu cũ trong bảng (đảm bảo không bị trùng email)
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM DAILY WHERE email = 'daTonTai@gmail.com';");
        stmt.execute("DELETE FROM DAILY WHERE email = 'chuaTonTai@gmail.com';");
        // Khởi tạo DAO
        daiLyDAO = DaiLyDAO.getInstance();

        // Chèn dữ liệu mẫu vào cơ sở dữ liệu thực
        DaiLy daiLy = new DaiLy(1, 1, "Đại lý Văn An", "0123456789", "daTonTai@gmail.com", "14 Tân Vạn", Date.valueOf(LocalDate.now()), 0L, "Ghi chú 1");
        daiLyDAO.Insert(daiLy);
    }

    @Test
    void UTCID01() throws SQLException {
        DaiLy newDaiLy = new DaiLy(2, 1, "Đại lý Tân Bình", "0356644894", "chuaTonTai@gmail.com", "14 Tân Vạn", Date.valueOf(LocalDate.now()), 0L, "Ghi chú 2");

        int result = daiLyDAO.Insert(newDaiLy);

        // Kiểm tra kết quả
        assertEquals(1, result, "Insert failed");

    }

    @Test
    void UTCID02() {
        DaiLy newDaiLy = new DaiLy(2, 1, "Đại lý Tân Bình", "0356644894", "daTonTai@gmail.com", "14 Tân Vạn", Date.valueOf(LocalDate.now()), 0L, "Ghi chú 3");

        SQLException exception = assertThrows(SQLException.class, () -> {
            daiLyDAO.Insert(newDaiLy);
        });

        assertTrue(exception.getMessage().contains("UNIQUE"), "Email existed");
    }

    @Test
    void UTCID03() {
        DaiLy newDaiLy = new DaiLy(2, 1, "Đại lý Tân Bình", "0123456789", "chuaTonTai@gmail.com", "14 Tân Vạn", Date.valueOf(LocalDate.now()), 0L, "Ghi chú 3");

        SQLException exception = assertThrows(SQLException.class, () -> {
            daiLyDAO.Insert(newDaiLy);
        });

        assertTrue(exception.getMessage().contains("UNIQUE"), "Phone existed");
    }
}
