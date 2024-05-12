package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.TaiKhoan;
import org.doancnpm.Models.DatabaseDriver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TaiKhoanDAO implements Idao<TaiKhoan> {
    private static TaiKhoanDAO singleton = null;

    public static TaiKhoanDAO getInstance() {
        if (singleton == null) {
            singleton = new TaiKhoanDAO();
        }
        return singleton;
    }

    private final BooleanProperty taiKhoanDtbChanged = new SimpleBooleanProperty(false);

    @Override
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

    @Override
    public int Update(int maNhanVien, TaiKhoan taiKhoan) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE TAIKHOAN SET UserName = ?, Password = ? WHERE MaNhanVien = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, taiKhoan.getUserName());
        pstmt.setString(2, taiKhoan.getPassword());
        pstmt.setInt(3, maNhanVien);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Delete(int maNhanVien) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "DELETE FROM TAIKHOAN WHERE MaNhanVien = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, maNhanVien);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public TaiKhoan QueryID(int maNhanVien) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM TAIKHOAN WHERE MaNhanVien = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, maNhanVien);

        ResultSet rs = pstmt.executeQuery();
        TaiKhoan taiKhoan = null;

        if (rs.next()) {
            taiKhoan = new TaiKhoan();

            String userName = rs.getString("UserName");
            String password = rs.getString("Password");

            taiKhoan.setUserName(userName);
            taiKhoan.setPassword(password);
            taiKhoan.setMaNhanVien(maNhanVien);
        }

        rs.close();
        pstmt.close();

        return taiKhoan;
    }

    @Override
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
            String password = rs.getString("Password");
            int maNhanVien = rs.getInt("MaNhanVien");

            taiKhoan.setUserName(userName);
            taiKhoan.setPassword(password);
            taiKhoan.setMaNhanVien(maNhanVien);

            taiKhoanList.add(taiKhoan);
        }

        rs.close();
        pstmt.close();

        return taiKhoanList;
    }

    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        taiKhoanDtbChanged.addListener(listener);
    }

    private void notifyChange() {
        taiKhoanDtbChanged.set(!taiKhoanDtbChanged.get());
    }
}