package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Models.DonViTinh;
import org.doancnpm.Models.Quan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DonViTinhDAO implements Idao<DonViTinh> {
    private static DonViTinhDAO singleton = null;

    public static DonViTinhDAO getInstance() {
        if (singleton == null) {
            singleton = new DonViTinhDAO();
        }
        return singleton;
    }

    private final BooleanProperty dvtDTBChanged = new SimpleBooleanProperty(false);

    @Override
    public int Insert(DonViTinh donViTinh) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO DonViTinh (TenDonViTinh,GhiChu) VALUES (?, ?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, donViTinh.getTenDVT());
        pstmt.setString(1, donViTinh.getGhiChu());

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Update(int id, DonViTinh donViTinh) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE DonViTinh SET TenDonViTinh =? , GhiChu =? WHERE ID =? ";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, donViTinh.getTenDVT());
        pstmt.setString(2, donViTinh.getGhiChu());
        pstmt.setInt(3, id);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Delete(int id) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "DELETE FROM DonViTinh WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1,id);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public DonViTinh QueryID(int ID) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM DonViTinh WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);

        ResultSet rs = pstmt.executeQuery();

        DonViTinh donViTinh = null;
        if (rs.next()) {
            int id = rs.getInt("ID");
            String maDVT = rs.getString("MaDonViTinh");
            String tenDVT = rs.getString("TenDonViTinh");
            String ghiChu = rs.getString("GhiChu");

            donViTinh = new DonViTinh();
            donViTinh.setId(id);
            donViTinh.setMaDVT(maDVT);
            donViTinh.setTenDVT(tenDVT);
            donViTinh.setGhiChu(ghiChu);
        }
        rs.close();
        pstmt.close();
        return donViTinh;
    }

    @Override
    public ArrayList<DonViTinh> QueryAll() throws SQLException {
        ArrayList<DonViTinh> donViTinhs = new ArrayList<>();
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM DonViTinh";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("ID");
            String maDVT = rs.getString("MaDonViTinh");
            String tenDVT = rs.getString("TenDonViTinh");
            String ghiChu = rs.getString("GhiChu");

            DonViTinh donViTinh = new DonViTinh();
            donViTinh.setId(id);
            donViTinh.setMaDVT(maDVT);
            donViTinh.setTenDVT(tenDVT);
            donViTinh.setGhiChu(ghiChu);

            donViTinhs.add(donViTinh);
        }
        rs.close();
        pstmt.close();
        return donViTinhs;
    }

    public DonViTinh QueryMostRecent() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT TOP 1 * " +
                "FROM DonViTinh " +
                "ORDER BY ID DESC;";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);

        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int id = rs.getInt("ID");
            String tenDonViTinh = rs.getString("TenDonViTinh");
            String ghiChu = rs.getString("GhiChu");

            DonViTinh donViTinh = new DonViTinh();
            donViTinh.setId(id);
            donViTinh.setTenDVT(tenDonViTinh);
            donViTinh.setGhiChu(ghiChu);
            return donViTinh;
        }
        return null; // No record found with the given ID
    }
    
    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        dvtDTBChanged.addListener(listener);
    }

    private void notifyChange() {
        dvtDTBChanged.set(!dvtDTBChanged.get());
    }
}