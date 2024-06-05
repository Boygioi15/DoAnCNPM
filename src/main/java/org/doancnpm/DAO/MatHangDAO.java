package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Models.MatHang;

import java.sql.*;
import java.util.ArrayList;

public class MatHangDAO implements Idao<MatHang> {
    private static MatHangDAO singleton = null;
    public static MatHangDAO getInstance() {
        if(singleton==null){
            singleton = new MatHangDAO();
        }
        return singleton;
    }
    private final BooleanProperty matHangDtbChanged = new SimpleBooleanProperty(false);
    @Override
    public int Insert(MatHang matHang) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO MATHANG(MaDonViTinh,TenMatHang,DonGiaNhap,GhiChu) VALUES (?, ?, ?, ?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, matHang.getMaDVT());
        pstmt.setString(2, matHang.getTenMatHang());
        pstmt.setDouble(3, matHang.getDonGiaNhap());
        pstmt.setString(4, matHang.getGhiChu());
        int rowsAffected = pstmt.executeUpdate();
        if(rowsAffected>0){
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Update(int ID, MatHang matHang) throws SQLException{
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE MATHANG " +
                "SET TenMatHang = ?, MaDonViTinh = ?, DonGiaNhap = ?, GhiChu = ? " +
                "WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, matHang.getTenMatHang());
        pstmt.setInt(2, matHang.getMaDVT());
        pstmt.setDouble(3, matHang.getDonGiaNhap());
        pstmt.setString(4, matHang.getGhiChu());
        pstmt.setInt(5, matHang.getID());

        int rowsAffected = pstmt.executeUpdate();
        if(rowsAffected>0){
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Delete(int id) throws SQLException{
        Connection conn = DatabaseDriver.getConnect();
        String sql = "DELETE FROM MATHANG WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);

        int rowsAffected = pstmt.executeUpdate();
        if(rowsAffected>0){
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        matHangDtbChanged.addListener(listener);
    }
    private void notifyChange(){
        matHangDtbChanged.set(!matHangDtbChanged.get());
    }
    @Override
    public ArrayList<MatHang> QueryAll() throws SQLException{
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM MATHANG";

        assert conn != null;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        ArrayList<MatHang> matHangList = new ArrayList<>();
        while (rs.next()) {
            int ID = rs.getInt("ID");
            String maMH = rs.getString("MaMatHang");
            int maDVT = rs.getInt("MaDonViTinh");
            String tenMH = rs.getString("TenMatHang");
            Long donGiaNhap = rs.getLong("DonGiaNhap");
            Long donGiaXuat = rs.getLong("DonGiaXuat");
            String ghiChu = rs.getString("GhiChu");
            int sl = rs.getInt("SoLuong");

            MatHang matHang = new MatHang();
            matHang.setID(ID);
            matHang.setMaMatHang(maMH);
            matHang.setMaDVT(maDVT);
            matHang.setTenMatHang(tenMH);
            matHang.setDonGiaNhap(donGiaNhap);
            matHang.setDonGiaXuat(donGiaXuat);
            matHang.setGhiChu(ghiChu);
            matHang.setSoLuong(sl);

            matHangList.add(matHang);
        }

        rs.close();
        stmt.close();

        return matHangList;
    }

    @Override
    public MatHang QueryID(int ID) throws SQLException{
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM MATHANG WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);

        ResultSet rs = pstmt.executeQuery();
        MatHang matHang = null;
        if (rs.next()) {
            String maMH = rs.getString("MaMatHang");
            int maDVT = rs.getInt("MaDonViTinh");
            String tenMH = rs.getString("TenMatHang");
            Long donGiaNhap = rs.getLong("DonGiaNhap");
            Long donGiaXuat = rs.getLong("DonGiaXuat");
            String ghiChu = rs.getString("GhiChu");
            int sl = rs.getInt("SoLuong");

            matHang = new MatHang();
            matHang.setID(ID);
            matHang.setMaMatHang(maMH);
            matHang.setMaDVT(maDVT);
            matHang.setTenMatHang(tenMH);
            matHang.setDonGiaNhap(donGiaNhap);
            matHang.setDonGiaXuat(donGiaXuat);
            matHang.setGhiChu(ghiChu);
            matHang.setSoLuong(sl);
        }

        rs.close();
        pstmt.close();

        return matHang;
    }
}
