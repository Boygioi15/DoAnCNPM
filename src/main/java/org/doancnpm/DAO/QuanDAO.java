package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Models.Quan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuanDAO implements Idao<Quan> {
    private static QuanDAO singleton = null;

    public static QuanDAO getInstance() {
        if (singleton == null) {
            singleton = new QuanDAO();
        }
        return singleton;
    }

    private final BooleanProperty quanDtbChanged = new SimpleBooleanProperty(false);

    @Override
    public int Insert(Quan quan) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO QUAN (TenQuan,GhiChu) VALUES (?, ?, ?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, quan.getTenQuan());
        pstmt.setString(2, quan.getGhiChu());

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Update(int id, Quan quan) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE Quan SET TenQuan=?,GhiChu=? WHERE ID =? ";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, quan.getTenQuan());
        pstmt.setString(2, quan.getGhiChu());
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
        String sql = "DELETE FROM QUAN WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public Quan QueryID(int ID) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM QUAN WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int id = rs.getInt("ID");
            String maQuan = rs.getString("MaQuan");
            String tenQuan = rs.getString("TenQuan");
            String ghiChu = rs.getString("GhiChu");

            return new Quan(id, maQuan, tenQuan, ghiChu);

        }

        return null; // No record found with the given ID
    }

    @Override
    public ArrayList<Quan> QueryAll() throws SQLException {
        ArrayList<Quan> quans = new ArrayList<>();
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM QUAN";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("ID");
            String maQuan = rs.getString("MaQuan");
            String tenQuan = rs.getString("TenQuan");
            String ghiChu = rs.getString("GhiChu");

            quans.add(new Quan(id, maQuan, tenQuan, ghiChu));
        }

        return quans;
    }

    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        quanDtbChanged.addListener(listener);
    }

    private void notifyChange() {
        quanDtbChanged.set(!quanDtbChanged.get());
    }

}