package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Models.PhieuThu;

import java.sql.*;
import java.util.ArrayList;

public class PhieuThuDAO implements Idao<PhieuThu> {
    private static PhieuThuDAO singleton = null;

    public static PhieuThuDAO getInstance() {
        if (singleton == null) {
            singleton = new PhieuThuDAO();
        }
        return singleton;
    }

    private final BooleanProperty phieuThuDtbChanged = new SimpleBooleanProperty(false);


    @Override
    public int Insert(PhieuThu phieuThu) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO PHIEUTHUTIEN " +
                "(MaDaiLy,MaNhanVien,NgayLapPhieu,SoTienThu,GhiChu) " +
                "VALUES " +
                "(?,?,?,?,?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, phieuThu.getMaDaiLy());
        pstmt.setInt(2, phieuThu.getMaNhanVien());
        pstmt.setDate(3, phieuThu.getNgayLap());
        pstmt.setLong(4, phieuThu.getSoTienThu());
        pstmt.setString(5, phieuThu.getGhiChu());
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            DaiLyDAO.getInstance().updateNoHienTai(phieuThu.getMaDaiLy(),-phieuThu.getSoTienThu());
            notifyChange();
            DaiLyDAO.getInstance().notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Update(int ID, PhieuThu phieuThu) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE PHIEUTHUTIEN " +
                "SET GhiChu=? " +
                "WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, phieuThu.getGhiChu());
        pstmt.setInt(2,ID);
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
            DaiLyDAO.getInstance().notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Delete(int ID) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "DELETE FROM PHIEUTHUTIEN WHERE ID = ?";

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
    public PhieuThu QueryID(int ID) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM PHIEUTHUTIEN WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);

        ResultSet rs = pstmt.executeQuery();
        PhieuThu phieuThu = null;
        if (rs.next()) {
            int id = rs.getInt("ID");
            int maDaili = rs.getInt("MaDaiLy");
            int maNhanVien = rs.getInt("MaNhanVien");
            Date ngayLap  =rs.getDate("NgayLapPhieu");
            String maPhieuThu = rs.getString("MaPhieuThu");
            Long soTienThu = rs.getLong("SoTienThu");
            String ghiChu = rs.getString("GhiChu");
            phieuThu = new PhieuThu(id,maPhieuThu, maDaili,maNhanVien,ngayLap,soTienThu,ghiChu);
        }
        rs.close();
        pstmt.close();
        return phieuThu;
    }

    @Override
    public ArrayList<PhieuThu> QueryAll() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM PHIEUTHUTIEN";

        assert conn != null;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        ArrayList<PhieuThu> listPhieuThu = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("ID");
            int maDaili = rs.getInt("MaDaiLy");
            int maNhanVien = rs.getInt("MaNhanVien");
            Date ngayLap  =rs.getDate("NgayLapPhieu");
            String maPhieuThu = rs.getString("MaPhieuThu");
            Long soTienThu = rs.getLong("SoTienThu");
            String ghiChu = rs.getString("GhiChu");
            PhieuThu phieuThu = new PhieuThu(id,maPhieuThu, maDaili,maNhanVien,ngayLap,soTienThu,ghiChu);
            listPhieuThu.add(phieuThu);
        }
        rs.close();
        stmt.close();
        return listPhieuThu;
    }

    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        phieuThuDtbChanged.addListener(listener);
    }

    public void notifyChange() {
        phieuThuDtbChanged.set(!phieuThuDtbChanged.get());
    }
}
