package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.ChiTietPhieuNhap;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Models.PhieuThu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CTPNDAO {
    private static CTPNDAO singleton = null;

    public static CTPNDAO getInstance() {
        if (singleton == null) {
            singleton = new CTPNDAO();
        }
        return singleton;
    }

    private final BooleanProperty ctpnDtbChanged = new SimpleBooleanProperty(false);

    public int InsertBlock(List<ChiTietPhieuNhap> chiTietPhieuNhaps) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO ChiTietPhieuNhap(MaPhieuNhap, MaMatHang, SoLuong, DonGiaNhap) VALUES\n";

        for (int i = 0; i < chiTietPhieuNhaps.size(); i++) {
            sql = sql.concat(("(?,?,?,?),\n"));
        }
        sql = sql.substring(0, sql.length() - 2);
        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 0; i < chiTietPhieuNhaps.size(); i++) {
            pstmt.setInt(i * 4 + 1, chiTietPhieuNhaps.get(i).getMaPhieuNhap());
            pstmt.setInt(i * 4 + 2, chiTietPhieuNhaps.get(i).getMaMatHang());
            pstmt.setInt(i * 4 + 3, chiTietPhieuNhaps.get(i).getSoLuong());
            pstmt.setLong(i * 4 + 4, chiTietPhieuNhaps.get(i).getDonGiaNhap());
        }

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
            MatHangDAO.getInstance().notifyChange();
            PhieuNhapDAO.getInstance().notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    public List<ChiTietPhieuNhap> QueryByPhieuNhapID(Integer phieuNhapID) throws SQLException {

        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ?";

        List<ChiTietPhieuNhap> chiTietPhieuNhaps = new ArrayList<>();

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, phieuNhapID);

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            int maPhieuNhap = rs.getInt("MaPhieuNhap");
            int maMatHang = rs.getInt("MaMatHang");
            int soLuong = rs.getInt("SoLuong");
            Long thanhTien = rs.getLong("ThanhTien");
            Long donGiaNhap = rs.getLong("DonGiaNhap");

            ChiTietPhieuNhap temp = new ChiTietPhieuNhap();
            temp.setMaMatHang(maMatHang);
            temp.setMaPhieuNhap(maPhieuNhap);
            temp.setSoLuong(soLuong);
            temp.setThanhTien(thanhTien);
            temp.setDonGiaNhap(donGiaNhap);
            chiTietPhieuNhaps.add(temp);
        }
        rs.close();
        pstmt.close();
        return chiTietPhieuNhaps;
    }

    public void AddDatabaseListener(InvalidationListener listener) {
        ctpnDtbChanged.addListener(listener);
    }

    private void notifyChange() {
        ctpnDtbChanged.set(!ctpnDtbChanged.get());
    }
}
