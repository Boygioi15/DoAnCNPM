package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Ultilities.CurrentNVInfor;

import java.sql.*;
import java.util.ArrayList;

public class NhanVienDAO implements Idao<NhanVien> {
    private static NhanVienDAO singleton = null;

    public static NhanVienDAO getInstance() {
        if (singleton == null) {
            singleton = new NhanVienDAO();
        }
        return singleton;
    }

    private final BooleanProperty nhanVienDtbChanged = new SimpleBooleanProperty(false);

    @Override
    public int Insert(NhanVien nhanVien) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO NHANVIEN " +
                "(HoTen, GioiTinh, NgaySinh, SDT, Email, MaChucVu, Luong, GhiChu) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, nhanVien.getHoTen());
        pstmt.setString(2, nhanVien.getGioiTinh());
        pstmt.setDate(3, nhanVien.getNgaySinh());
        pstmt.setString(4, nhanVien.getSDT());
        pstmt.setString(5, nhanVien.getEmail());
        pstmt.setInt(6, nhanVien.getMaChucVu());
        pstmt.setDouble(7, nhanVien.getLuong());
        pstmt.setString(8, nhanVien.getGhiChu());

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Update(int id, NhanVien nhanVien) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE NHANVIEN " +
                "SET HoTen = ?, GioiTinh = ?, NgaySinh = ?, SDT = ?, Email = ?, MaChucVu = ?, Luong = ?, GhiChu = ? " +
                "WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, nhanVien.getHoTen());
        pstmt.setString(2, nhanVien.getGioiTinh());
        pstmt.setDate(3, nhanVien.getNgaySinh());
        pstmt.setString(4, nhanVien.getSDT());
        pstmt.setString(5, nhanVien.getEmail());
        pstmt.setInt(6, nhanVien.getMaChucVu());
        pstmt.setDouble(7, nhanVien.getLuong());
        pstmt.setString(8, nhanVien.getGhiChu());
        pstmt.setInt(9, id);

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
        String sql = "DELETE FROM NHANVIEN WHERE ID = ?";

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
    public NhanVien QueryID(int ID) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM NHANVIEN WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);

        ResultSet rs = pstmt.executeQuery();
        NhanVien nhanVien = null;

        if (rs.next()) {
            nhanVien = new NhanVien();
            int id = rs.getInt("ID");
            String maNhanVien = rs.getString("MaNhanVien");
            String hoTen = rs.getString("HoTen");
            String gioiTinh = rs.getString("GioiTinh");
            Date ngaySinh = rs.getDate("NgaySinh");
            String sdt = rs.getString("SDT");
            String email = rs.getString("Email");
            int maChucVu = rs.getInt("MaChucVu");
            Long luong = rs.getLong("Luong");
            String ghiChu = rs.getString("GhiChu");

            nhanVien.setID(id);
            nhanVien.setMaNhanVien(maNhanVien);
            nhanVien.setID(ID);
            nhanVien.setHoTen(hoTen);
            nhanVien.setGioiTinh(gioiTinh);
            nhanVien.setNgaySinh(ngaySinh);
            nhanVien.setSDT(sdt);
            nhanVien.setEmail(email);
            nhanVien.setMaChucVu(maChucVu);
            nhanVien.setLuong(luong);
            nhanVien.setGhiChu(ghiChu);
        }

        rs.close();
        pstmt.close();

        return nhanVien;
    }

    @Override
    public ArrayList<NhanVien> QueryAll() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM NHANVIEN";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<NhanVien> nhanVienList = new ArrayList<>();

        while (rs.next()) {
            NhanVien nhanVien = new NhanVien();
            String maNhanVien = rs.getString("MaNhanVien");
            int ID = rs.getInt("ID");
            String hoTen = rs.getString("HoTen");
            String gioiTinh = rs.getString("GioiTinh");
            Date ngaySinh = rs.getDate("NgaySinh");
            String sdt = rs.getString("SDT");
            String email = rs.getString("Email");
            int maChucVu = rs.getInt("MaChucVu");
            Long luong = rs.getLong("Luong");
            String ghiChu = rs.getString("GhiChu");

            nhanVien.setID(ID);
            nhanVien.setMaNhanVien(maNhanVien);
            nhanVien.setHoTen(hoTen);
            nhanVien.setGioiTinh(gioiTinh);
            nhanVien.setNgaySinh(ngaySinh);
            nhanVien.setSDT(sdt);
            nhanVien.setEmail(email);
            nhanVien.setMaChucVu(maChucVu);
            nhanVien.setLuong(luong);
            nhanVien.setGhiChu(ghiChu);

            nhanVienList.add(nhanVien);
        }

        rs.close();
        pstmt.close();

        return nhanVienList;
    }

    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        nhanVienDtbChanged.addListener(listener);
    }

    private void notifyChange() {
        nhanVienDtbChanged.set(!nhanVienDtbChanged.get());
    }

    public void updatePasswordByEmail(String email, String newPassword) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE TAIKHOAN " +
                "SET Password = ? " +
                "WHERE MaNhanVien IN (SELECT ID FROM NHANVIEN WHERE Email = ?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newPassword);
        pstmt.setString(2, email);

        int rowsUpdated = pstmt.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Mật khẩu đã được cập nhật thành công.");
        } else {
            System.out.println("Không tìm thấy tài khoản tương ứng với địa chỉ email: " + email);
        }
        // khi sua moi cap nhat
        if(CurrentNVInfor.getInstance().getLoggedInNhanVien()!=null){
            CurrentNVInfor.getInstance().getTaiKhoanOfNhanien().setPassword(newPassword);
        }
        pstmt.close();
    }
}