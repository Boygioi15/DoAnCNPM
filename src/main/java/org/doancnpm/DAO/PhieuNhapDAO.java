package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Models.PhieuNhap;
import org.doancnpm.Models.PhieuThu;
import org.doancnpm.Models.PhieuXuat;

import java.sql.*;
import java.util.ArrayList;

public class PhieuNhapDAO implements Idao<PhieuNhap> {
    private static PhieuNhapDAO singleton = null;

    public static PhieuNhapDAO getInstance() {
        if (singleton == null) {
            singleton = new PhieuNhapDAO();
        }
        return singleton;
    }

    private final BooleanProperty phieuThuDtbChanged = new SimpleBooleanProperty(false);


    @Override
    public int Insert(PhieuNhap phieuNhap) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO PhieuNhapHang " +
                "(MaNhanVien, NgayLapPhieu, NhaCungCap, GhiChu) " +
                "VALUES " +
                "(?, ?, ?, ?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, phieuNhap.getMaNhanVien());
        pstmt.setDate(2, phieuNhap.getNgayLapPhieu());
        pstmt.setString(3, phieuNhap.getNhaCungCap());
        pstmt.setString(4, phieuNhap.getGhiChu());

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        notifyChange();
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Update(int ID, PhieuNhap phieuNhap) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE PHIEUNHAPHANG " +
                "SET GhiChu= ? " +
                "WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, phieuNhap.getGhiChu());
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
        String sql = "DELETE FROM PhieuNhapHang WHERE ID = ?";

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
    public PhieuNhap QueryID(int ID) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM PhieuNhapHang WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);

        ResultSet rs = pstmt.executeQuery();
        PhieuNhap phieuNhap = null;
        if (rs.next()) {
            int id = rs.getInt("ID");
            String maPhieuNhap = rs.getString("MaPhieuNhap");
            String nhaCungCap = rs.getString("nhaCungCap");
            int maNhanVien = rs.getInt("MaNhanVien");
            Date ngayLap  =rs.getDate("NgayLapPhieu");
            Long tongTien = rs.getLong("tongTien");
            String ghiChu = rs.getString("GhiChu");

            phieuNhap = new PhieuNhap();
            phieuNhap.setID(id);
            phieuNhap.setNhaCungCap(nhaCungCap);
            phieuNhap.setMaPhieuNhap(maPhieuNhap);
            phieuNhap.setMaNhanVien(maNhanVien);
            phieuNhap.setNgayLapPhieu(ngayLap);
            phieuNhap.setTongTien(tongTien);
            phieuNhap.setGhiChu(ghiChu);
        }
        rs.close();
        pstmt.close();
        return phieuNhap;
    }
    public PhieuNhap QueryMostRecent() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT TOP 1 * " +
                "FROM PhieuNhapHang " +
                "ORDER BY ID DESC;";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);

        ResultSet rs = pstmt.executeQuery();
        PhieuNhap phieuNhap = null;
        if (rs.next()) {
            int id = rs.getInt("ID");
            String maPhieuNhap = rs.getString("MaPhieuNhap");
            String nhaCungCap = rs.getString("nhaCungCap");
            int maNhanVien = rs.getInt("MaNhanVien");
            Date ngayLap  =rs.getDate("NgayLapPhieu");
            Long tongTien = rs.getLong("tongTien");
            String ghiChu = rs.getString("GhiChu");

            phieuNhap = new PhieuNhap();
            phieuNhap.setID(id);
            phieuNhap.setNhaCungCap(nhaCungCap);
            phieuNhap.setMaPhieuNhap(maPhieuNhap);
            phieuNhap.setMaNhanVien(maNhanVien);
            phieuNhap.setNgayLapPhieu(ngayLap);
            phieuNhap.setTongTien(tongTien);
            phieuNhap.setGhiChu(ghiChu);
        }
        rs.close();
        pstmt.close();
        return phieuNhap;
    }
    @Override
    public ArrayList<PhieuNhap> QueryAll() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM PhieuNhapHang";

        assert conn != null;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        ArrayList<PhieuNhap> listPhieuNhap = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("ID");
            String maPhieuNhap = rs.getString("MaPhieuNhap");
            String nhaCungCap = rs.getString("nhaCungCap");
            int maNhanVien = rs.getInt("MaNhanVien");
            Date ngayLap  =rs.getDate("NgayLapPhieu");
            Long tongTien = rs.getLong("tongTien");
            String ghiChu = rs.getString("GhiChu");

            PhieuNhap phieuNhap = new PhieuNhap();
            phieuNhap.setID(id);
            phieuNhap.setNhaCungCap(nhaCungCap);
            phieuNhap.setMaPhieuNhap(maPhieuNhap);
            phieuNhap.setMaNhanVien(maNhanVien);
            phieuNhap.setNgayLapPhieu(ngayLap);
            phieuNhap.setTongTien(tongTien);
            phieuNhap.setGhiChu(ghiChu);

            listPhieuNhap.add(phieuNhap);
        }
        rs.close();
        stmt.close();
        return listPhieuNhap;
    }

    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        phieuThuDtbChanged.addListener(listener);
    }

    private void notifyChange() {
        phieuThuDtbChanged.set(!phieuThuDtbChanged.get());
    }
}
