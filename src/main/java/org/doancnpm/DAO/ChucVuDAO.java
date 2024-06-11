package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.ChucVu;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Models.DonViTinh;
import org.doancnpm.Ultilities.CheckExist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChucVuDAO implements Idao<ChucVu> {
    private static ChucVuDAO singleton = null;

    public static ChucVuDAO getInstance() {
        if (singleton == null) {
            singleton = new ChucVuDAO();
        }
        return singleton;
    }

    private final BooleanProperty dvtDTBChanged = new SimpleBooleanProperty(false);

    @Override
    public int Insert(ChucVu chucVu) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO ChucVu (TenChucVu,GhiChu) VALUES (?, ?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, chucVu.getTenCV());
        pstmt.setString(2, chucVu.getGhiChu());

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Update(int id, ChucVu chucVu) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE DonViTinh SET TenDonViTinh =? , GhiChu =? WHERE ID =? ";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, chucVu.getTenCV());
        pstmt.setString(2, chucVu.getGhiChu());
        pstmt.setInt(3, id);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
            NhanVienDAO.getInstance().notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Delete(int id) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "DELETE FROM ChucVu WHERE ID = ?";

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
    public ChucVu QueryID(int ID) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM ChucVu WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);

        ResultSet rs = pstmt.executeQuery();
        ChucVu chucVu = null;
        if (rs.next()) {
            int id = rs.getInt("ID");
            String maCV = rs.getString("MaChucVu");
            String tenCV = rs.getString("TenChucVu");
            String ghiChu = rs.getString("GhiChu");

            chucVu = new ChucVu();
            chucVu.setId(id);
            chucVu.setMaCV(maCV);
            chucVu.setTenCV(tenCV);
            chucVu.setGhiChu(ghiChu);
        }
        rs.close();
        pstmt.close();
        return chucVu;
    }
    public ChucVu QueryMostRecent() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT TOP 1 * " +
                "FROM ChucVu " +
                "ORDER BY ID DESC;";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);

        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int id = rs.getInt("ID");
            String tenChucVu = rs.getString("TenChucVu");
            String ghiChu = rs.getString("GhiChu");

            ChucVu chucVu = new ChucVu();
            chucVu.setId(id);
            chucVu.setTenCV(tenChucVu);
            chucVu.setGhiChu(ghiChu);
            return chucVu;
        }
        return null; // No record found with the given ID
    }
    @Override
    public ArrayList<ChucVu> QueryAll() throws SQLException {
        ArrayList<ChucVu> chucVus = new ArrayList<>();
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM ChucVu";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("ID");
            String maCV = rs.getString("MaChucVu");
            String tenCV = rs.getString("TenChucVu");
            String ghiChu = rs.getString("GhiChu");

            ChucVu chucVu = new ChucVu();
            chucVu.setId(id);
            chucVu.setMaCV(maCV);
            chucVu.setTenCV(tenCV);
            chucVu.setGhiChu(ghiChu);

            chucVus.add(chucVu);
        }
        rs.close();
        pstmt.close();
        return chucVus;
    }
    public ChucVu QueryName(String name) throws SQLException {
        // Fetch all ChucVu objects
        ArrayList<ChucVu> allChucVu = QueryAll();
        String normalizedChucVuName = CheckExist.normalize(name);

        // Iterate through the list of all ChucVu objects and check if the name exists
        for (ChucVu chucVu : allChucVu) {
            // Normalize the ChucVu name from the list (remove accents and convert to lowercase)
            String normalizedChucVu = CheckExist.normalize(chucVu.getTenCV());

            if (normalizedChucVu.equals(normalizedChucVuName)) {
                return chucVu;
            }
        }

        return null;
    }
    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        dvtDTBChanged.addListener(listener);
    }

    private void notifyChange() {
        dvtDTBChanged.set(!dvtDTBChanged.get());
    }
}