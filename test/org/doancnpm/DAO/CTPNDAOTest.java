package org.doancnpm.DAO;

import org.doancnpm.Models.ChiTietPhieuNhap;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.MatHang;
import org.doancnpm.Models.PhieuNhap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CTPNDAOTest extends ApplicationTest {

    private Connection connection;
    private CTPNDAO ctpndao;
    private PhieuNhapDAO phieuNhapDAO;
    private MatHangDAO matHangDAO;
    PhieuNhap lasPhieuNhap;
    private MatHang matHang;
    private List<ChiTietPhieuNhap> chiTietPhieuNhaps;

    @BeforeEach
    void setUp() throws SQLException {
        // Kết nối đến SQL Server thực tế
        connection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=QUANLYDAILY;user=sa;password=123456;");
        lasPhieuNhap = phieuNhapDAO.QueryMostRecent();
        // Xóa dữ liệu cũ trong bảng CTPN, sử dụng PreparedStatement để truyền MaPhieuNhap và MaMatHang
        int maPhieuNhap = lasPhieuNhap.getID();  // Ví dụ, bạn có thể thay đổi giá trị này theo nhu cầu
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM CTPN WHERE MaMatHang = ? AND MaPhieuNhap = ?")) {
            stmt.setInt(1, 1); // MaMatHang = 1
            stmt.setInt(2, maPhieuNhap); // MaPhieuNhap được truyền từ biến
            stmt.executeUpdate();
        }

        // Tiếp tục xóa dữ liệu theo MaMatHang và MaPhieuNhap khác
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM CTPN WHERE MaMatHang = ? AND MaPhieuNhap = ?")) {
            stmt.setInt(1, 2); // MaMatHang = 2
            stmt.setInt(2, maPhieuNhap); // MaPhieuNhap được truyền từ biến
            stmt.executeUpdate();
        }
// Tiếp tục xóa dữ liệu theo MaMatHang và MaPhieuNhap khác
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM PHIEUNHAP WHERE ID = ?")) {
            stmt.setInt(1, maPhieuNhap); // MaMatHang = 2
            stmt.executeUpdate();
        }
        // Tạo đối tượng PhieuNhap và thêm vào cơ sở dữ liệu
        PhieuNhap phieuNhap = new PhieuNhap("Nhà cung cấp a", 1, Date.valueOf(LocalDate.now()));
        phieuNhapDAO = PhieuNhapDAO.getInstance();
        phieuNhapDAO.Insert(phieuNhap);
        // Khởi tạo DAO
        ctpndao = CTPNDAO.getInstance();
        matHangDAO = MatHangDAO.getInstance();

        // Chèn dữ liệu mẫu vào cơ sở dữ liệu thực
        ChiTietPhieuNhap chiTietPhieuNhap = new ChiTietPhieuNhap(phieuNhap.getID(), 2, 2, matHangDAO.QueryID(2).getDonGiaNhap());
        chiTietPhieuNhaps.add(chiTietPhieuNhap);
        ctpndao.InsertBlock(chiTietPhieuNhaps);
        chiTietPhieuNhaps.clear();
    }
    @Test
    void UTCID01() throws SQLException {
        // Chèn dữ liệu mẫu vào cơ sở dữ liệu thực
        ChiTietPhieuNhap chiTietPhieuNhap = new ChiTietPhieuNhap(lasPhieuNhap.getID(), 1, 2, matHangDAO.QueryID(1).getDonGiaNhap());
        chiTietPhieuNhaps.add(chiTietPhieuNhap);

        int result =  ctpndao.InsertBlock(chiTietPhieuNhaps);;

        // Kiểm tra kết quả
        assertEquals(1, result, "Insert failed");
    }

    @Test
    void UTCID02() throws SQLException {
        ChiTietPhieuNhap chiTietPhieuNhap = new ChiTietPhieuNhap(lasPhieuNhap.getID(), 2, 2, matHangDAO.QueryID(1).getDonGiaNhap());
        chiTietPhieuNhaps.add(chiTietPhieuNhap);

        SQLException exception = assertThrows(SQLException.class, () -> {
            ctpndao.InsertBlock(chiTietPhieuNhaps);
        });

        assertTrue(exception.getMessage().contains("UNIQUE"), "Good existed");
    }
}