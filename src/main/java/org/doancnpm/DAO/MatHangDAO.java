package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.DatabaseDriver;
import org.doancnpm.Models.MatHang;
import org.doancnpm.Models.PhieuNhap;
import org.doancnpm.Ultilities.CheckExist;

import java.sql.*;
import java.util.ArrayList;

public class MatHangDAO implements Idao<MatHang> {
    private static MatHangDAO singleton = null;

    public static MatHangDAO getInstance() {
        if (singleton == null) {
            singleton = new MatHangDAO();
        }
        return singleton;
    }

    private final BooleanProperty matHangDtbChanged = new SimpleBooleanProperty(false);

    @Override
    public int Insert(MatHang matHang) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "INSERT INTO MATHANG(MaDonViTinh,TenMatHang,DonGiaNhap,DonGiaXuat,SoLuong,GhiChu) VALUES (?, ?, ?, ?,?,?)";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, matHang.getMaDVT());
        pstmt.setString(2, matHang.getTenMatHang());
        pstmt.setDouble(3, matHang.getDonGiaNhap());
        pstmt.setString(6, matHang.getGhiChu());
        pstmt.setInt(5, 0);
        pstmt.setDouble(4,matHang.getDonGiaNhap() * ThamSoDAO.getInstance().GetTyLeDonGiaXuat());

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
        }


        pstmt.close();

        return rowsAffected;
    }

    @Override
    public int Update(int ID, MatHang matHang) throws SQLException {
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
        if (rowsAffected > 0) {
            notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }
    public int updateDonGiaXuat(double tyleDonGiaXuat) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE MATHANG SET DonGiaXuat = DonGiaNhap * ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setDouble(1, tyleDonGiaXuat);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
            PhieuNhapDAO.getInstance().notifyChange();
            PhieuXuatDAO.getInstance().notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    public int updateSoluongMatHang(int id, int soluong) throws SQLException {
        System.out.println("So luong: " + soluong);
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE MATHANG SET SoLuong = SoLuong + ? WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setDouble(1, soluong);
        pstmt.setDouble(2, id);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
            PhieuNhapDAO.getInstance().notifyChange();
            PhieuXuatDAO.getInstance().notifyChange();
            this.QueryID(id);
        }

        pstmt.close();
        return rowsAffected;
    }

    @Override
    public int Delete(int id) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE MatHang " +
                "SET isDeleted = 1 " +
                "WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
            PhieuNhapDAO.getInstance().notifyChange();
            PhieuXuatDAO.getInstance().notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    public int Restore(int id) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "UPDATE MatHang " +
                "SET isDeleted = 0" +
                "WHERE ID = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            notifyChange();
            PhieuNhapDAO.getInstance().notifyChange();
            PhieuXuatDAO.getInstance().notifyChange();
        }
        pstmt.close();
        return rowsAffected;
    }

    @Override
    public void AddDatabaseListener(InvalidationListener listener) {
        matHangDtbChanged.addListener(listener);
    }

    public void notifyChange() {
        matHangDtbChanged.set(!matHangDtbChanged.get());
    }

    @Override
    public ArrayList<MatHang> QueryAll() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM MATHANG  Where isDeleted = 0";

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
            int isDeleted = rs.getInt("isDeleted");

            MatHang matHang = new MatHang();
            matHang.setID(ID);
            matHang.setMaMatHang(maMH);
            matHang.setMaDVT(maDVT);
            matHang.setTenMatHang(tenMH);
            matHang.setDonGiaNhap(donGiaNhap);
            matHang.setDonGiaXuat(donGiaXuat);
            matHang.setGhiChu(ghiChu);
            matHang.setSoLuong(sl);
            matHang.setDeleted(isDeleted != 0);
            matHangList.add(matHang);
        }

        rs.close();
        stmt.close();

        return matHangList;
    }

    @Override
    public MatHang QueryID(int ID) throws SQLException {
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
            int isDeleted = rs.getInt("isDeleted");

            matHang = new MatHang();
            matHang.setID(ID);
            matHang.setMaMatHang(maMH);
            matHang.setMaDVT(maDVT);
            matHang.setTenMatHang(tenMH);
            matHang.setDonGiaNhap(donGiaNhap);
            matHang.setDonGiaXuat(donGiaXuat);
            matHang.setGhiChu(ghiChu);
            matHang.setSoLuong(sl);
            matHang.setDeleted(isDeleted != 0);

            System.out.println("ID: " + matHang.getID());
            System.out.println("Mã mặt hàng: " + matHang.getMaMatHang());
            System.out.println("Mã đơn vị tính: " + matHang.getMaDVT());
            System.out.println("Tên mặt hàng: " + matHang.getTenMatHang());
            System.out.println("Đơn giá nhập: " + matHang.getDonGiaNhap());
            System.out.println("Đơn giá xuất: " + matHang.getDonGiaXuat());
            System.out.println("Ghi chú: " + matHang.getGhiChu());
            System.out.println("Số lượng: " + matHang.getSoLuong());

        }

        rs.close();
        pstmt.close();

        return matHang;
    }

    public ArrayList<MatHang> QueryDeleted() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "SELECT * FROM MatHang Where isDeleted = 1";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
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
            int isDeleted = rs.getInt("isDeleted");

            MatHang matHang = new MatHang();
            matHang.setID(ID);
            matHang.setMaMatHang(maMH);
            matHang.setMaDVT(maDVT);
            matHang.setTenMatHang(tenMH);
            matHang.setDonGiaNhap(donGiaNhap);
            matHang.setDonGiaXuat(donGiaXuat);
            matHang.setGhiChu(ghiChu);
            matHang.setSoLuong(sl);
            matHang.setDeleted(isDeleted != 0);
            matHangList.add(matHang);
        }

        rs.close();
        pstmt.close();

        return matHangList;
    }


    public MatHang QueryName(String name) throws SQLException {
        ArrayList<MatHang> allMatHang = QueryAll();
        String normalizedMatHang = CheckExist.normalize(name);

        // Iterate through the list of all MatHang objects and check if the name exists
        for (MatHang matHang : allMatHang) {
            // Normalize the MatHang name from the list
            String normalizedMatHangName = CheckExist.normalize(matHang.getTenMatHang());

            if (normalizedMatHangName.equals(normalizedMatHang)) {
                return matHang;
            }
        }
        return null;
    }

}
