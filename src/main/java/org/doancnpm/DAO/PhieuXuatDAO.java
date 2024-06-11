package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.*;
import org.doancnpm.Models.PhieuXuat;

import java.sql.*;
import java.util.ArrayList;

public class PhieuXuatDAO implements Idao<PhieuXuat> {
    private static PhieuXuatDAO singleton = null;

    public static PhieuXuatDAO getInstance() {
        if (singleton == null) {
            singleton = new PhieuXuatDAO();
        }
        return singleton;
    }

    private final BooleanProperty phieuThuDtbChanged = new SimpleBooleanProperty(false);


    @Override
    public int Insert(PhieuXuat PhieuXuat) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO PhieuXuatHang " +
                "(MaNhanVien, MaDaiLy, NgayLapPhieu, GhiChu) " +
                "VALUES " +
                "(?, ?, ?, ?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, PhieuXuat.getMaNhanVien());
        pstmt.setInt(2, PhieuXuat.getMaDaiLy());
        pstmt.setDate(3, PhieuXuat.getNgayLapPhieu());
        pstmt.setString(4, PhieuXuat.getGhiChu());

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Update(int ID, PhieuXuat PhieuXuat) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE PhieuXuatHANG " +
                "SET GhiChu= ? " +
                "WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, PhieuXuat.getGhiChu());
        pstmt.setInt(2,ID);
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Delete(int ID) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "DELETE FROM PhieuXuatHang WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public PhieuXuat QueryID(int ID) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM PhieuXuatHang WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);

        ResultSet rs = pstmt.executeQuery();
        PhieuXuat PhieuXuat = null;
        if (rs.next()) {
            int id = rs.getInt("ID");
            String maPhieuXuat = rs.getString("MaPhieuXuat");
            int maDaiLy = rs.getInt("MaDaiLy");
            int maNhanVien = rs.getInt("MaNhanVien");
            Date ngayLap  =rs.getDate("NgayLapPhieu");
            Long tongTien = rs.getLong("tongTien");
            String ghiChu = rs.getString("GhiChu");

            PhieuXuat = new PhieuXuat();
            PhieuXuat.setID(id);
            PhieuXuat.setMaDaiLy(maDaiLy);
            PhieuXuat.setMaPhieuXuat(maPhieuXuat);
            PhieuXuat.setMaNhanVien(maNhanVien);
            PhieuXuat.setNgayLapPhieu(ngayLap);
            PhieuXuat.setTongTien(tongTien);
            PhieuXuat.setGhiChu(ghiChu);
        }
        rs.close();
        pstmt.close();
        return PhieuXuat;
    }

    @Override
    public ArrayList<PhieuXuat> QueryAll() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM PhieuXuatHang";

        assert conn != null;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        ArrayList<PhieuXuat> listPhieuXuat = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("ID");
            String maPhieuXuat = rs.getString("MaPhieuXuat");
            int maDaiLy = rs.getInt("MaDaiLy");
            int maNhanVien = rs.getInt("MaNhanVien");
            Date ngayLap  =rs.getDate("NgayLapPhieu");
            Long tongTien = rs.getLong("tongTien");
            String ghiChu = rs.getString("GhiChu");

            PhieuXuat phieuXuat = new PhieuXuat();
            phieuXuat.setID(id);
            phieuXuat.setMaDaiLy(maDaiLy);
            phieuXuat.setMaPhieuXuat(maPhieuXuat);
            phieuXuat.setMaNhanVien(maNhanVien);
            phieuXuat.setNgayLapPhieu(ngayLap);
            phieuXuat.setTongTien(tongTien);
            phieuXuat.setGhiChu(ghiChu);

            listPhieuXuat.add(phieuXuat);
        }
        rs.close();
        stmt.close();
        return listPhieuXuat;
    }
    public PhieuXuat QueryMostRecent() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT TOP 1 * " +
                "FROM PhieuXuatHang " +
                "ORDER BY ID DESC;";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);

        ResultSet rs = pstmt.executeQuery();
        PhieuXuat phieuXuat = null;
        if (rs.next()) {
            int id = rs.getInt("ID");
            String maPhieuXuat = rs.getString("MaPhieuXuat");
            int maDaiLy = rs.getInt("MaDaiLy");
            int maNhanVien = rs.getInt("MaNhanVien");
            Date ngayLap  =rs.getDate("NgayLapPhieu");
            Long tongTien = rs.getLong("tongTien");
            String ghiChu = rs.getString("GhiChu");

            phieuXuat = new PhieuXuat();
            phieuXuat.setID(id);
            phieuXuat.setMaDaiLy(maDaiLy);
            phieuXuat.setMaPhieuXuat(maPhieuXuat);
            phieuXuat.setMaNhanVien(maNhanVien);
            phieuXuat.setNgayLapPhieu(ngayLap);
            phieuXuat.setTongTien(tongTien);
            phieuXuat.setGhiChu(ghiChu);
        }
        rs.close();
        pstmt.close();
        return phieuXuat;
    }
    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        phieuThuDtbChanged.addListener(listener);
    }

    public void notifyChange() {
        phieuThuDtbChanged.set(!phieuThuDtbChanged.get());
    }
}
