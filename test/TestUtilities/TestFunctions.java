package TestUtilities;

import org.doancnpm.Mode;
import org.doancnpm.Models.DatabaseDriver;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestFunctions {
    public static void InsertDailyWithNoHienTai(int nohienTai) {
        if(!Mode.TestMode){
            return;
        }
        try {
            Connection conn = DatabaseDriver.getConnect();
            String sql = "INSERT INTO DAILY " +
                    "(MaQuan, MaLoaiDaiLy, TenDaiLy, DienThoai, Email, DiaChi, NgayTiepNhan, NoHienTai, GhiChu) " +
                    "VALUES " +
                    "(?,?,?,?,?,?,?,?,?)";

            assert conn != null;
            PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, 1);
            pstmt.setInt(2, 1);
            pstmt.setString(3, "Test phieu thu");
            pstmt.setString(4, "0848282828");
            pstmt.setString(5, "hoan@gmail.com");
            pstmt.setString(6, "binh duong");

            // Format the date as yyyy-MM-dd
            String dateString = "14/02/2024";
            String[] dateParts = dateString.split("/");
            String formattedDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
            pstmt.setDate(7, Date.valueOf(formattedDate));

            pstmt.setInt(8, nohienTai);
            pstmt.setString(9, "Nothing");

            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void DeleteAll(String DB_NAME){
        if(Mode.TestMode){
            Connection conn = DatabaseDriver.getConnect();
            String sql = "DELETE FROM " + DB_NAME;
            PreparedStatement pstmt = null;
            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();
                pstmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void SetUpTestPhieuXuat() throws SQLException {
        if(!Mode.TestMode){
            return;
        }
        Connection conn = DatabaseDriver.getConnect();
        InsertDailyWithNoHienTai(0);
        InsertTestMatHang2();
        String sql = "Update MatHang Set SoLuong=30";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.executeUpdate();
        pstmt.close();
    }
    public static void InsertTestMatHang() throws SQLException {
        if(!Mode.TestMode){
            return;
        }
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO MATHANG(MaDonViTinh,TenMatHang,DonGiaNhap) VALUES (1,N'Mặt hàng test',30000)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.executeUpdate();
        pstmt.close();
    }
    public static void InsertTestMatHang2() throws SQLException {
        if(!Mode.TestMode){
            return;
        }
        Connection conn = DatabaseDriver.getConnect();
        String sql1 = "INSERT INTO MATHANG(MaDonViTinh,TenMatHang,DonGiaNhap) VALUES (1,N'Mặt hàng test1',30000)";
        String sql2 = "INSERT INTO MATHANG(MaDonViTinh,TenMatHang,DonGiaNhap) VALUES (2,N'Mặt hàng test2',40000)";
        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql1);
        pstmt.executeUpdate();
        pstmt = conn.prepareStatement(sql2);
        pstmt.executeUpdate();
        pstmt.close();
    }
}
