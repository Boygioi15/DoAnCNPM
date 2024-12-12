package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.PhieuNhap;
import org.doancnpm.Models.TaiKhoan;
import org.doancnpm.Models.DatabaseDriver;

import java.sql.*;
import java.util.ArrayList;

public class TaiKhoanDAO {
    private static TaiKhoanDAO singleton = null;

    public static TaiKhoanDAO getInstance() {
        if (singleton == null) {
            singleton = new TaiKhoanDAO();
        }
        return singleton;
    }

    private final BooleanProperty taiKhoanDtbChanged = new SimpleBooleanProperty(false);

    public int Insert(TaiKhoan taiKhoan) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO TAIKHOAN (UserName, Password, MaNhanVien) VALUES (?, ?, ?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, taiKhoan.getUserName());
        pstmt.setString(2, taiKhoan.getPassword());
        pstmt.setInt(3, taiKhoan.getMaNhanVien());

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    public int Update(String userName, TaiKhoan taiKhoan) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE TAIKHOAN SET UserName = ?, maNhanVien = ?  WHERE userName = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, userName);
        pstmt.setInt(2, taiKhoan.getMaNhanVien());
        pstmt.setString(3, taiKhoan.getUserName());


        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    public int Delete(String userName) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "DELETE FROM TAIKHOAN WHERE UserName = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, userName);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    public TaiKhoan QueryUserName(String userName) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM TAIKHOAN WHERE MaNhanVien = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, userName);

        ResultSet rs = pstmt.executeQuery();
        TaiKhoan taiKhoan = null;

        if (rs.next()) {
            taiKhoan = new TaiKhoan();

            String userName1 = rs.getString("UserName");
            String mk = rs.getString("Password");
            Integer maNV = rs.getInt("maNhanVien");
            taiKhoan.setUserName(userName1);
            taiKhoan.setPassword(mk);
            taiKhoan.setMaNhanVien(maNV);
        }
        rs.close();
        pstmt.close();

        return taiKhoan;
    }

    public ArrayList<TaiKhoan> QueryAll() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM TAIKHOAN";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<TaiKhoan> taiKhoanList = new ArrayList<>();

        while (rs.next()) {
            TaiKhoan taiKhoan = new TaiKhoan();

            String userName = rs.getString("UserName");
            String mk = rs.getString("Password");
            int maNhanVien = rs.getInt("MaNhanVien");

            taiKhoan.setUserName(userName);
            taiKhoan.setPassword(mk);
            taiKhoan.setMaNhanVien(maNhanVien);

            taiKhoanList.add(taiKhoan);
        }

        rs.close();
        pstmt.close();

        return taiKhoanList;
    }

    public void AddDatabaseListener(InvalidationListener listener) {
        taiKhoanDtbChanged.addListener(listener);
    }

    public void notifyChange() {
        taiKhoanDtbChanged.set(!taiKhoanDtbChanged.get());
    }
}