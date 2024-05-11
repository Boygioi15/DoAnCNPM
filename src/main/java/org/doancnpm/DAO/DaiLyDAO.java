package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.DatabaseDriver;

import java.sql.*;
import java.util.ArrayList;

public class DaiLyDAO implements Idao<DaiLy> {
    private static DaiLyDAO singleton = null;
    public static DaiLyDAO getInstance() {
        if(singleton==null){
            singleton = new DaiLyDAO();
        }
        return singleton;
    }

    private final BooleanProperty dailyDtbChanged = new SimpleBooleanProperty(false);

    @Override
    public int Insert(DaiLy daiLy) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO DAILY (TenDaiLy, DienThoai,Email,DiaChi,NgayTiepNhan,GhiChu) VALUES (?, ?, ?,?,?,?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, daiLy.getTenDaiLy());
        pstmt.setString(2, daiLy.getDienThoai());
        pstmt.setString(3, daiLy.getEmail());
        pstmt.setString(4, daiLy.getDiaChi());
        pstmt.setDate(5, daiLy.getNgayTiepNhan());
        pstmt.setString(6, daiLy.getGhiChu());

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Update(int id, DaiLy daiLy) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE DAILY " +
                "SET TenDaiLy = ?, DienThoai = ?, Email = ?, DiaChi = ?, NgayTiepNhan = ?, NoHienTai = ?, GhiChu = ? " +
                "WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, daiLy.getTenDaiLy());
        pstmt.setString(2, daiLy.getDienThoai());
        pstmt.setString(3, daiLy.getEmail());
        pstmt.setString(4, daiLy.getDiaChi());
        pstmt.setDate(5, daiLy.getNgayTiepNhan());
        pstmt.setInt(6, daiLy.getNoHienTai());
        pstmt.setString(7, daiLy.getGhiChu());
        pstmt.setInt(8, id);
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
        String sql = "DELETE FROM DAILY WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public DaiLy QueryID(int ID) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM DAILY WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);

        ResultSet rs = pstmt.executeQuery();
        DaiLy daiLy = null;

        if (rs.next()) {
            daiLy = new DaiLy();

            String maDL = rs.getString("MaDaiLy");
            String maQuan = rs.getString("MaQuan");
            String maLoaiDL = rs.getString("MaLoaiDaiLy");
            String tenDaiLy = rs.getString("TenDaiLy");
            String SDT = rs.getString("DienThoai");
            String email = rs.getString("email");
            String diaChi = rs.getString("DiaChi");
            Date ngayTiepNhan = rs.getDate("ngayTiepNhan");
            int noHienTai = rs.getInt("NoHienTai");
            String ghiChu = rs.getString("GhiChu");

            daiLy.setMaDaiLy(maDL);
            daiLy.setMaQuan(maQuan);
            daiLy.setMaLoaiDaiLy(maLoaiDL);

            daiLy.setTenDaiLy(tenDaiLy);
            daiLy.setDiaChi(diaChi);
            daiLy.setEmail(email);
            daiLy.setDienThoai(SDT);
            daiLy.setNgayTiepNhan(ngayTiepNhan);
            daiLy.setNoHienTai(noHienTai);
            daiLy.setGhiChu(ghiChu);
        }

        rs.close();
        pstmt.close();

        return daiLy;
    }

    @Override
    public ArrayList<DaiLy> QueryAll() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM DAILY";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<DaiLy> daiLyList = new ArrayList<DaiLy>();

        while (rs.next()) {
            DaiLy daiLy = new DaiLy();

            int ID = rs.getInt("ID");
            String maDL = rs.getString("MaDaiLy");
            String maQuan = rs.getString("MaQuan");
            String maLoaiDL = rs.getString("MaLoaiDaiLy");
            String tenDaiLy = rs.getString("TenDaiLy");
            String SDT = rs.getString("DienThoai");
            String email = rs.getString("email");
            String diaChi = rs.getString("DiaChi");
            Date ngayTiepNhan = rs.getDate("ngayTiepNhan");
            Integer noHienTai = rs.getInt("NoHienTai");
            String ghiChu = rs.getString("GhiChu");

            daiLy.setID(ID);
            daiLy.setMaDaiLy(maDL);
            daiLy.setMaQuan(maQuan);
            daiLy.setMaLoaiDaiLy(maLoaiDL);

            daiLy.setTenDaiLy(tenDaiLy);
            daiLy.setDiaChi(diaChi);
            daiLy.setEmail(email);
            daiLy.setDienThoai(SDT);
            daiLy.setNgayTiepNhan(ngayTiepNhan);
            daiLy.setNoHienTai(noHienTai);
            daiLy.setGhiChu(ghiChu);

            daiLyList.add(daiLy);
        }

        rs.close();
        pstmt.close();

        return daiLyList;
    }

    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        dailyDtbChanged.addListener(listener);
    }

    private void notifyChange() {
        dailyDtbChanged.set(!dailyDtbChanged.get());
    }
}

