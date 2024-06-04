package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.ChiTietPhieuXuat;
import org.doancnpm.Models.DatabaseDriver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CTPXDAO{
    private static CTPXDAO singleton = null;

    public static CTPXDAO getInstance() {
        if (singleton == null) {
            singleton = new CTPXDAO();
        }
        return singleton;
    }
    private final BooleanProperty ctpxDtbChanged = new SimpleBooleanProperty(false);

    public int InsertBlock(List<ChiTietPhieuXuat> chiTietPhieuXuats) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO ChiTietPhieuXuat(MaPhieuXuat, MaMatHang, SoLuong) VALUES\n";

        for(int i = 0;i<chiTietPhieuXuats.size();i++){
            sql = sql.concat(("(?,?,?),\n"));
        }
        sql = sql.substring(0,sql.length()-2);
        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for(int i = 0;i<chiTietPhieuXuats.size();i++){
            pstmt.setInt(i*3+1,chiTietPhieuXuats.get(i).getMaPhieuXuat());
            pstmt.setInt(i*3+2,chiTietPhieuXuats.get(i).getMaMatHang());
            pstmt.setInt(i*3+3,chiTietPhieuXuats.get(i).getSoLuong());
        }
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }
    public List<ChiTietPhieuXuat> QueryByPhieuXuatID(Integer phieuXuatID) throws SQLException {

        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM ChiTietPhieuXuat WHERE MaPhieuXuat = ?";

        List<ChiTietPhieuXuat> chiTietPhieuXuats = new ArrayList<>();

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, phieuXuatID);

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            int maPhieuXuat = rs.getInt("MaPhieuXuat");
            int maMatHang = rs.getInt("MaMatHang");
            int soLuong = rs.getInt("SoLuong");
            Long thanhTien = rs.getLong("ThanhTien");

            ChiTietPhieuXuat temp = new ChiTietPhieuXuat();
            temp.setMaMatHang(maMatHang);
            temp.setMaPhieuXuat(maPhieuXuat);
            temp.setSoLuong(soLuong);
            temp.setThanhTien(thanhTien);

            chiTietPhieuXuats.add(temp);
        }
        rs.close();
        pstmt.close();
        return chiTietPhieuXuats;
    }
    public void AddDatabaseListener(InvalidationListener listener) {
        ctpxDtbChanged.addListener(listener);
    }
    private void notifyChange() {
        ctpxDtbChanged.set(!ctpxDtbChanged.get());
    }
}
