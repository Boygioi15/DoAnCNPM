package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Models.MatHang;

import java.sql.*;
import java.util.ArrayList;

public class MatHangDao implements Idao<MatHang> {
    private static MatHangDao singleton = null;
    public static MatHangDao getInstance() {
        if(singleton==null){
            singleton = new MatHangDao();
        }
        return singleton;
    }
    private final BooleanProperty matHangDtbChanged = new SimpleBooleanProperty(false);
    @Override
    public int Insert(MatHang matHang) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO MATHANG (MaDonViTinh, TenMatHang, GhiChu) VALUES (?, ?, ?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, matHang.getMaDonViTinh());
        pstmt.setString(2, matHang.getTenMaHang());
        pstmt.setString(3, matHang.getGhiChu());

        int rowsAffected = pstmt.executeUpdate();
        if(rowsAffected>0){
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Update(MatHang matHang) throws SQLException{
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE MATHANG SET ten_mh = ?, ghi_chu = ? WHERE ma_dvt = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, matHang.getTenMaHang());
        pstmt.setString(2, matHang.getGhiChu());
        pstmt.setInt(3, matHang.getMaDonViTinh());

        int rowsAffected = pstmt.executeUpdate();
        if(rowsAffected>0){
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Delete(MatHang matHang) throws SQLException{
        Connection conn = DatabaseDriver.getConnect();
        String sql = "DELETE FROM MATHANG WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, matHang.getID());

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
    public ArrayList<MatHang> QueryAll() {
        try {
            Connection conn = DatabaseDriver.getConnect();
            String sql = "SELECT * FROM MATHANG";

            assert conn != null;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ArrayList<MatHang> matHangList = new ArrayList<>();
            while (rs.next()) {
                String mamh = rs.getString("MaMatHang");
                int maDvt = rs.getInt("MaDonViTinh");
                String tenMh = rs.getString("TenMatHang");
                String ghiChu = rs.getString("GhiChu");
                MatHang matHang = new MatHang(mamh,maDvt, tenMh, ghiChu);
                matHangList.add(matHang);
            }

            rs.close();
            stmt.close();
            conn.close();

            return matHangList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
            String mamh = rs.getString("MaMatHang");
            int maDvt = rs.getInt("MaDonViTinh");
            String tenMh = rs.getString("TenMatHang");
            String ghiChu = rs.getString("GhiChu");
            matHang = new MatHang(mamh,maDvt, tenMh, ghiChu);
        }

        rs.close();
        pstmt.close();

        return matHang;
    }
}
