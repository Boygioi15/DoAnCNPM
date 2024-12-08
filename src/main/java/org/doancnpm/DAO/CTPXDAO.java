package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.ChiTietPhieuXuat;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Ultilities.PopDialog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CTPXDAO {
    private static CTPXDAO singleton = null;

    public static CTPXDAO getInstance() {
        if (singleton == null) {
            singleton = new CTPXDAO();
        }
        return singleton;
    }

    private final BooleanProperty ctpxDtbChanged = new SimpleBooleanProperty(false);

    public int InsertBlock(int dailyid,int id ,List<ChiTietPhieuXuat> chiTietPhieuXuats) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO ChiTietPhieuXuat(MaPhieuXuat, MaMatHang, SoLuong,DonGiaXuat,ThanhTien) VALUES\n";

        for (int i = 0; i < chiTietPhieuXuats.size(); i++) {
            sql = sql.concat(("(?,?,?,?,?),\n"));
        }
        sql = sql.substring(0, sql.length() - 2);
        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);

        for (int i = 0; i < chiTietPhieuXuats.size(); i++) {
            pstmt.setInt(i * 5 + 1, chiTietPhieuXuats.get(i).getMaPhieuXuat());
            pstmt.setInt(i * 5 + 2, chiTietPhieuXuats.get(i).getMaMatHang());
            pstmt.setInt(i * 5 + 3, chiTietPhieuXuats.get(i).getSoLuong());
            pstmt.setLong(i * 5 + 4, chiTietPhieuXuats.get(i).getDonGiaXuat());
            pstmt.setLong(i * 5 + 5, chiTietPhieuXuats.get(i).getDonGiaXuat()* chiTietPhieuXuats.get(i).getSoLuong());
            PhieuXuatDAO.getInstance().UpdatePrice(id,  (chiTietPhieuXuats.get(i).getSoLuong() * chiTietPhieuXuats.get(i).getDonGiaXuat()));
            DaiLyDAO.getInstance().updateNoHienTai(dailyid,chiTietPhieuXuats.get(i).getSoLuong()* chiTietPhieuXuats.get(i).getDonGiaXuat());
            MatHangDAO.getInstance().updateSoluongMatHang(chiTietPhieuXuats.get(i).getMaMatHang(),-chiTietPhieuXuats.get(i).getSoLuong());
        }
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
            MatHangDAO.getInstance().notifyChange();
            DaiLyDAO.getInstance().notifyChange();
            PhieuXuatDAO.getInstance().notifyChange();
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
            Long donGiaXuat = rs.getLong("donGiaXuat");
            ChiTietPhieuXuat temp = new ChiTietPhieuXuat();
            temp.setMaMatHang(maMatHang);
            temp.setMaPhieuXuat(maPhieuXuat);
            temp.setSoLuong(soLuong);
            temp.setThanhTien(thanhTien);
            temp.setDonGiaXuat(donGiaXuat);
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
