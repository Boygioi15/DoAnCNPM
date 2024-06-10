package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Models.LoaiDaiLy;
import org.doancnpm.Ultilities.CheckExist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoaiDaiLyDAO implements Idao<LoaiDaiLy> {
    private static LoaiDaiLyDAO singleton = null;

    public static LoaiDaiLyDAO getInstance() {
        if (singleton == null) {
            singleton = new LoaiDaiLyDAO();
        }
        return singleton;
    }

    private final BooleanProperty loaiDaiLyDtbChanged = new SimpleBooleanProperty(false);

    @Override
    public int Insert(LoaiDaiLy loaiDaiLy) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO LoaiDaiLy (SoNoToiDa,TenLoai,GhiChu) VALUES (?, ?, ?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, loaiDaiLy.getSoNoToiDa());
        pstmt.setString(2, loaiDaiLy.getTenLoai());
        pstmt.setString(3, loaiDaiLy.getGhiChu());

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Update(int id, LoaiDaiLy loaiDaiLy) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE LoaiDaiLy SET SoNoToiDa = ?, TenLoai =? , GhiChu =? WHERE ID =? ";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, loaiDaiLy.getSoNoToiDa());
        pstmt.setString(2, loaiDaiLy.getTenLoai());
        pstmt.setString(3, loaiDaiLy.getGhiChu());
        pstmt.setInt(4, id);

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
        String sql = "DELETE FROM LOAIDAILY WHERE ID = ?";

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
    public LoaiDaiLy QueryID(int ID) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM LOAIDAILY WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int id = rs.getInt("ID");
            String maLoai = rs.getString("MaLoai");
            int soNoToiDa = rs.getInt("SoNoToiDa");
            String tenLoai = rs.getString("TenLoai");
            String ghiChu = rs.getString("GhiChu");

            return new LoaiDaiLy(id, maLoai, soNoToiDa, tenLoai, ghiChu);
        }

        return null;
    }
    public LoaiDaiLy QueryName(String name) throws SQLException {
        ArrayList<LoaiDaiLy> allLoaiDaiLy = QueryAll();
        String normalizedLoaiDaiLy = CheckExist.normalize(name);

        // Duyệt qua danh sách tất cả các Loại Đại Lý và kiểm tra xem Loại Đại Lý đã tồn tại hay không
        for (LoaiDaiLy loaiDaiLy : allLoaiDaiLy) {
            // Chuẩn hóa tên Loại Đại Lý từ danh sách (loại bỏ dấu và chuyển thành chữ thường)
            String normalizedLoaiDaiLyName = CheckExist.normalize(loaiDaiLy.getTenLoai());

            if (normalizedLoaiDaiLyName.equals(normalizedLoaiDaiLy)) {
                return loaiDaiLy;
            }
        }

        return null;
    }

    @Override
    public ArrayList<LoaiDaiLy> QueryAll() throws SQLException {
        ArrayList<LoaiDaiLy> loaiDaiLys = new ArrayList<>();
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM LOAIDAILY";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("ID");
            String maLoai = rs.getString("MaLoai");
            int soNoToiDa = rs.getInt("SoNoToiDa");
            String tenLoai = rs.getString("TenLoai");
            String ghiChu = rs.getString("GhiChu");

            loaiDaiLys.add(new LoaiDaiLy(id,maLoai, soNoToiDa, tenLoai, ghiChu));
        }

        return loaiDaiLys;
    }

    public LoaiDaiLy QueryMostRecent() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT TOP 1 * " +
                "FROM LoaiDaiLy " +
                "ORDER BY ID DESC;";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            LoaiDaiLy ldl = new LoaiDaiLy();
            int id = rs.getInt("ID");
            String maLoai = rs.getString("MaLoai");
            int soNoToiDa = rs.getInt("SoNoToiDa");
            String tenLoai = rs.getString("TenLoai");
            String ghiChu = rs.getString("GhiChu");

            ldl.setId(id);
            ldl.setTenLoai(tenLoai);
            ldl.setSoNoToiDa(soNoToiDa);
            ldl.setGhiChu(ghiChu);

            return ldl;
        }

        return null;
    }

    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        loaiDaiLyDtbChanged.addListener(listener);
    }

    private void notifyChange() {
        loaiDaiLyDtbChanged.set(!loaiDaiLyDtbChanged.get());
    }
}