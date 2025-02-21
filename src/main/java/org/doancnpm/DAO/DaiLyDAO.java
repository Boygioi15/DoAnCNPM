package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Mode;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Models.DonViTinh;
import org.doancnpm.Ultilities.CheckExist;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DaiLyDAO implements Idao<DaiLy> {
    private static DaiLyDAO singleton = null;
    public static DaiLyDAO getInstance() {
        if(singleton==null){
            singleton = new DaiLyDAO();
        }
        return singleton;
    }
    private DaiLyDAO(){};
    private final BooleanProperty dailyDtbChanged = new SimpleBooleanProperty(false);

    @Override
    public int Insert(DaiLy daiLy) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO DAILY " +
                "(MaQuan, MaLoaiDaiLy, TenDaiLy, DienThoai,Email,DiaChi,NgayTiepNhan,GhiChu) " +
                "VALUES " +
                "(?,?,?,?,?,?,?,?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, daiLy.getMaQuan());
        pstmt.setInt(2, daiLy.getMaLoaiDaiLy());
        pstmt.setString(3, daiLy.getTenDaiLy());
        pstmt.setString(4, daiLy.getDienThoai());
        pstmt.setString(5, daiLy.getEmail());
        pstmt.setString(6, daiLy.getDiaChi());
        pstmt.setDate(7, daiLy.getNgayTiepNhan());
        pstmt.setString(8, daiLy.getGhiChu());

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
                "SET MaQuan = ?, MaLoaiDaiLy =?, TenDaiLy = ?, DienThoai = ?, Email = ?, DiaChi = ?, NgayTiepNhan = ?, NoHienTai = ?, GhiChu = ? " +
                "WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, daiLy.getMaQuan());
        pstmt.setInt(2, daiLy.getMaLoaiDaiLy());
        pstmt.setString(3, daiLy.getTenDaiLy());
        pstmt.setString(4, daiLy.getDienThoai());
        pstmt.setString(5, daiLy.getEmail());
        pstmt.setString(6, daiLy.getDiaChi());
        pstmt.setDate(7, daiLy.getNgayTiepNhan());
        pstmt.setLong(8, daiLy.getNoHienTai());
        pstmt.setString(9, daiLy.getGhiChu());
        pstmt.setInt(10, id);
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
            PhieuThuDAO.getInstance().notifyChange();
            PhieuXuatDAO.getInstance().notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Delete(int id) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE DAILY " +
                "SET isDeleted = 1 " +
                "WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
            PhieuThuDAO.getInstance().notifyChange();
            PhieuXuatDAO.getInstance().notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    public int Restore(int id) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE DAILY " +
                "SET isDeleted = 0" +
                "WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
            PhieuThuDAO.getInstance().notifyChange();
            PhieuXuatDAO.getInstance().notifyChange();
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
            int maQuan = rs.getInt("MaQuan");
            int maLoaiDL = rs.getInt("MaLoaiDaiLy");
            String tenDaiLy = rs.getString("TenDaiLy");
            String SDT = rs.getString("DienThoai");
            String email = rs.getString("email");
            String diaChi = rs.getString("DiaChi");
            Date ngayTiepNhan = rs.getDate("ngayTiepNhan");
            Long noHienTai = rs.getLong("NoHienTai");
            String ghiChu = rs.getString("GhiChu");
            int isDeleted = rs.getInt("isDeleted");

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

            daiLy.setDeleted(isDeleted != 0);
        }

        rs.close();
        pstmt.close();

        return daiLy;
    }

    @Override
    public ArrayList<DaiLy> QueryAll() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM DAILY Where isDeleted = 0";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<DaiLy> daiLyList = new ArrayList<>();

        while (rs.next()) {
            DaiLy daiLy = new DaiLy();

            int ID = rs.getInt("ID");
            String maDL = rs.getString("MaDaiLy");
            int maQuan = rs.getInt("MaQuan");
            int maLoaiDL = rs.getInt("MaLoaiDaiLy");
            String tenDaiLy = rs.getString("TenDaiLy");
            String SDT = rs.getString("DienThoai");
            String email = rs.getString("email");
            String diaChi = rs.getString("DiaChi");
            Date ngayTiepNhan = rs.getDate("ngayTiepNhan");
            Long noHienTai = rs.getLong("NoHienTai");
            String ghiChu = rs.getString("GhiChu");

            int isDeleted = rs.getInt("isDeleted");

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

            if(isDeleted==0){
                daiLy.setDeleted(false);
            }else{
                daiLy.setDeleted(true);
            }
        }

        rs.close();
        pstmt.close();

        return daiLyList;
    }

    public ArrayList<DaiLy> QueryDeleted() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM DAILY Where isDeleted = 1";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<DaiLy> daiLyList = new ArrayList<>();

        while (rs.next()) {
            DaiLy daiLy = new DaiLy();

            int ID = rs.getInt("ID");
            String maDL = rs.getString("MaDaiLy");
            int maQuan = rs.getInt("MaQuan");
            int maLoaiDL = rs.getInt("MaLoaiDaiLy");
            String tenDaiLy = rs.getString("TenDaiLy");
            String SDT = rs.getString("DienThoai");
            String email = rs.getString("email");
            String diaChi = rs.getString("DiaChi");
            Date ngayTiepNhan = rs.getDate("ngayTiepNhan");
            Long noHienTai = rs.getLong("NoHienTai");
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


    public DaiLy QueryName(String name) throws SQLException {
        ArrayList<DaiLy> allDaiLy = DaiLyDAO.getInstance().QueryAll();
        String normalizedDaiLyName = CheckExist.normalize(name);

        // Duyệt qua danh sách tất cả các đại lý và kiểm tra xem đại lý đã tồn tại hay không
        for (DaiLy dl : allDaiLy) {
            // Chuẩn hóa tên đại lý từ danh sách (loại bỏ dấu và chuyển thành chữ thường)
            String normalizedDaiLy = CheckExist.normalize(dl.getTenDaiLy());

            if (normalizedDaiLy.equals(normalizedDaiLyName)) {
                return dl;
            }
        }

        return null;
    }
    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        dailyDtbChanged.addListener(listener);
    }

    public void notifyChange() {
        dailyDtbChanged.set(!dailyDtbChanged.get());
    }

    public int deleteByEmail(String email) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "DELETE FROM DAILY WHERE Email = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, email);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }
}

