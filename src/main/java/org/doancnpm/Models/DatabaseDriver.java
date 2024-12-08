package org.doancnpm.Models;


import java.sql.*;

public class DatabaseDriver {
    private static final String DB_URL = "jdbc:h2:./QuanLyDaiLy;AUTO_SERVER=TRUE";
    private static final String USER_NAME = "sa"; // Tên người dùng mặc định
    private static final String PASSWORD = ""; // Mật khẩu mặc định

    private static Connection dataBaseConnection = null;

    // Phương thức để lấy kết nối
    public static Connection getConnect() {
        try {
            if (dataBaseConnection == null) {
                // Tạo kết nối đến H2
                Class.forName("org.h2.Driver");

                dataBaseConnection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
                System.out.println("Kết nối H2 thành công!");


            }
            return dataBaseConnection;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    // Kiểm tra sự tồn tại của bảng
    private static boolean isTableExist(String tableName) {
        Connection conn = getConnect();
        if (conn == null) {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?")) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void initializeDatabase(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            // Câu lệnh tạo bảng

            if (!isTableExist( "THAMSO")) {
                String createThamSoSQL = """
                    CREATE TABLE THAMSO (
                        SoDaiLyToiDaMoiQuan INT NOT NULL DEFAULT 4,
                        TyLeDonGiaXuat FLOAT NOT NULL DEFAULT 1.02,
                        ChoPhepVuotNo INT NOT NULL DEFAULT 0
                    );
            """;
                statement.execute(createThamSoSQL);
                System.out.println("Tạo bảng THAMSO thành công!");
            }

            // Kiểm tra và tạo bảng QUAN nếu chưa có
            if (!isTableExist("QUAN")) {
                String createQuanSQL = """
                    CREATE TABLE QUAN (
                        ID INT AUTO_INCREMENT PRIMARY KEY,
                        MaQuan VARCHAR(10) GENERATED ALWAYS AS ('QN' || LPAD(ID, 3, '0')),
                        TenQuan NVARCHAR(100),
                        GhiChu NVARCHAR(1000)
                    );
            """;
                statement.execute(createQuanSQL);
                System.out.println("Tạo bảng QUAN thành công!");
            }

            // Kiểm tra và tạo bảng LOAIDAILY nếu chưa có
            if (!isTableExist( "LOAIDAILY")) {
                String createLoaiDaiLySQL = """
                    CREATE TABLE LOAIDAILY (
                        ID INT AUTO_INCREMENT PRIMARY KEY,
                        MaLoai VARCHAR(10) GENERATED ALWAYS AS ('LDL' || LPAD(ID, 3, '0')),
                        SoNoToiDa DECIMAL(19,4),
                        TenLoai NVARCHAR(100),
                        GhiChu NVARCHAR(1000)
                    );
            """;
                statement.execute(createLoaiDaiLySQL);
                System.out.println("Tạo bảng LOAIDAILY thành công!");
            }

            // Kiểm tra và tạo bảng CHUCVU nếu chưa có
            if (!isTableExist( "CHUCVU")) {
                String createChucVuSQL = """
                    CREATE TABLE CHUCVU (
                        ID INT AUTO_INCREMENT PRIMARY KEY,
                        MaChucVu VARCHAR(10) GENERATED ALWAYS AS ('CV' || LPAD(ID, 2, '0')),
                        TenChucVu NVARCHAR(255),
                        GhiChu NVARCHAR(1000)
                    );
            """;
                statement.execute(createChucVuSQL);
                System.out.println("Tạo bảng CHUCVU thành công!");
            }

            // Kiểm tra và tạo bảng DONVITINH nếu chưa có
            if (!isTableExist("DONVITINH")) {
                String createDonViTinhSQL = """
                    CREATE TABLE DONVITINH (
                        ID INT AUTO_INCREMENT PRIMARY KEY,
                        MaDonViTinh VARCHAR(10) GENERATED ALWAYS AS ('DVT' || LPAD(ID, 3, '0')),
                        TenDonViTinh NVARCHAR(100),
                        GhiChu NVARCHAR(1000)
                    );
            """;
                statement.execute(createDonViTinhSQL);
                System.out.println("Tạo bảng DONVITINH thành công!");
            }

            if (!isTableExist( "MATHANG")) {
                String createMatHangSQL = """
                    CREATE TABLE MATHANG (
                        ID INT AUTO_INCREMENT PRIMARY KEY,
                        MaMatHang VARCHAR(10) GENERATED ALWAYS AS ('MH' || LPAD(ID, 4, '0')),
                        MaDonViTinh INT,
                        TenMatHang NVARCHAR(100),
                        SoLuong INT,
                        DonGiaNhap INT,
                        DonGiaXuat INT,
                        isDeleted SMALLINT DEFAULT 0,
                        GhiChu NVARCHAR(1000),
                        FOREIGN KEY (MaDonViTinh) REFERENCES DONVITINH(ID)
                    );
            """;
                statement.execute(createMatHangSQL);
                System.out.println("Tạo bảng MATHANG thành công!");
            }

            if (!isTableExist("DAILY")) {
                String createDaiLySQL = """
                    CREATE TABLE DAILY (
                        ID INT AUTO_INCREMENT PRIMARY KEY,
                        MaDaiLy VARCHAR(10) GENERATED ALWAYS AS ('DL' || LPAD(ID, 4, '0')),
                        MaQuan INT,
                        MaLoaiDaiLy INT,
                        TenDaiLy NVARCHAR(100),
                        DienThoai CHAR(10),
                        Email NVARCHAR(100),
                        DiaChi NVARCHAR(100),
                        NgayTiepNhan DATE,
                        NoHienTai DECIMAL(19,4),
                        GhiChu NVARCHAR(1000),
                        isDeleted SMALLINT DEFAULT 0,
                        FOREIGN KEY (MaQuan) REFERENCES QUAN(ID),
                        FOREIGN KEY (MaLoaiDaiLy) REFERENCES LOAIDAILY(ID)
                    );
            """;
                statement.execute(createDaiLySQL);
                System.out.println("Tạo bảng DAILY thành công!");
            }

            if (!isTableExist( "NHANVIEN")) {
                String createNhanVienSQL = """
                    CREATE TABLE NHANVIEN (
                        ID INT AUTO_INCREMENT PRIMARY KEY,
                        MaNhanVien VARCHAR(10) GENERATED ALWAYS AS ('NV' || LPAD(ID, 4, '0')),
                        HoTen NVARCHAR(255),
                        GioiTinh NVARCHAR(50),
                        NgaySinh DATE,
                        SDT VARCHAR(11),
                        Email VARCHAR(255),
                        MaChucVu INT,
                        Luong DECIMAL(19,4),
                        isDeleted SMALLINT DEFAULT 0,
                        GhiChu NVARCHAR(1000),
                        FOREIGN KEY (MaChucVu) REFERENCES CHUCVU(ID)
                    );
            """;
                statement.execute(createNhanVienSQL);
                System.out.println("Tạo bảng NHANVIEN thành công!");
            }

            // Kiểm tra và tạo bảng TAIKHOAN nếu chưa có
            if (!isTableExist("TAIKHOAN")) {
                String createTaiKhoanSQL = """
                    CREATE TABLE TAIKHOAN (
                        UserName VARCHAR(100) UNIQUE,
                        Password VARCHAR(255),
                        MaNhanVien INT,
                        FOREIGN KEY (MaNhanVien) REFERENCES NHANVIEN(ID)
                    );
            """;
                statement.execute(createTaiKhoanSQL);
                System.out.println("Tạo bảng TAIKHOAN thành công!");
            }

            // Kiểm tra và tạo bảng PHIEUNHAPHANG nếu chưa có
            if (!isTableExist("PHIEUNHAPHANG")) {
                String createPhieuNhapSQL = """
                    CREATE TABLE PHIEUNHAPHANG (
                        ID INT AUTO_INCREMENT PRIMARY KEY,
                        MaPhieuNhap VARCHAR(10) GENERATED ALWAYS AS ('PN' || LPAD(ID, 6, '0')),
                        MaNhanVien INT,
                        NhaCungCap VARCHAR(250), 
                        NgayLapPhieu DATE,
                        TongTien DECIMAL(19,4) DEFAULT 0,
                        GhiCHu VARCHAR(250),
                        FOREIGN KEY (MaNhanVien) REFERENCES NHANVIEN(ID)
                    );
            """;
                statement.execute(createPhieuNhapSQL);
                System.out.println("Tạo bảng PHIEUNHAPHANG thành công!");
            }

            // Kiểm tra và tạo bảng PHIEUXUATHANG nếu chưa có
            if (!isTableExist("PHIEUXUATHANG")) {
                String createPhieuXuatSQL = """
                    CREATE TABLE PHIEUXUATHANG (
                        ID INT AUTO_INCREMENT PRIMARY KEY,
                        MaPhieuXuat VARCHAR(10) GENERATED ALWAYS AS ('PX' || LPAD(ID, 6, '0')),
                        MaNhanVien INT,
                        MaDaiLy INT,
                        NgayLapPhieu DATE,
                        TongTien DECIMAL(19,4) DEFAULT 0,
                         GhiCHu VARCHAR(250),
                        FOREIGN KEY (MaNhanVien) REFERENCES NHANVIEN(ID),
                        FOREIGN KEY (MaDaiLy) REFERENCES DAILY(ID)
                    );
            """;
                statement.execute(createPhieuXuatSQL);
                System.out.println("Tạo bảng PHIEUXUATHANG thành công!");
            }

            // Kiểm tra và tạo bảng PHIEUTHUTIEN nếu chưa có
            if (!isTableExist("PHIEUTHUTIEN")) {
                String createPhieuThuSQL = """
                    CREATE TABLE PHIEUTHUTIEN (
                        ID INT AUTO_INCREMENT PRIMARY KEY,
                        MaPhieuThu VARCHAR(10) GENERATED ALWAYS AS ('PT' || LPAD(ID, 6, '0')),
                        MaDaiLy INT,
                        MaNhanVien INT,
                        NgayLapPhieu DATE,
                        SoTienThu INT,
                        GhiChu VARCHAR(250),
                        FOREIGN KEY (MaDaiLy) REFERENCES DAILY(ID),
                        FOREIGN KEY (MaNhanVien) REFERENCES NHANVIEN(ID)
                    );
            """;
                statement.execute(createPhieuThuSQL);
                System.out.println("Tạo bảng PHIEUTHUTIEN thành công!");
            }
            // Kiểm tra và tạo bảng BAOCAODOANHSO nếu chưa có
            if (!isTableExist( "BAOCAODOANHSO")) {
                String createBaoCaoDoanhSoSQL = """
                    CREATE TABLE BAOCAODOANHSO (
                        MaDaiLy INT,
                        NgayLapPhieu DATE,
                        SoPhieuXuat INT,
                        TongTriGia DECIMAL(19,4),
                        TyLe FLOAT,
                        PRIMARY KEY (MaDaiLy, NgayLapPhieu),
                        FOREIGN KEY (MaDaiLy) REFERENCES DAILY(ID)
                    );
            """;
                statement.execute(createBaoCaoDoanhSoSQL);
                System.out.println("Tạo bảng BAOCAODOANHSO thành công!");
            }

            // Kiểm tra và tạo bảng BAOCAOCONGNO nếu chưa có
            if (!isTableExist("BAOCAOCONGNO")) {
                String createBaoCaoCongNoSQL = """
                    CREATE TABLE BAOCAOCONGNO (
                        MaDaiLy INT,
                        NgayLapPhieu DATE,
                        NoDau DECIMAL(19,4),
                        NoCuoi DECIMAL(19,4),
                        PhatSinh DECIMAL(19,4),
                        PRIMARY KEY (MaDaiLy, NgayLapPhieu),
                        FOREIGN KEY (MaDaiLy) REFERENCES DAILY(ID)
                    );
            """;
                statement.execute(createBaoCaoCongNoSQL);
                System.out.println("Tạo bảng BAOCAOCONGNO thành công!");
            }

            // Kiểm tra và tạo bảng CHITIETPHIEUNHAP nếu chưa có
            if (!isTableExist("CHITIETPHIEUNHAP")) {
                String createChiTietPhieuNhapSQL = """
                    CREATE TABLE CHITIETPHIEUNHAP (
                        MaPhieuNhap INT,
                        MaMatHang INT,
                        SoLuong INT,
                        DonGiaNhap DECIMAL(19,4),
                        ThanhTien DECIMAL(19,4),
                        GhiChu NVARCHAR(1000),
                        PRIMARY KEY (MaPhieuNhap, MaMatHang),
                        FOREIGN KEY (MaPhieuNhap) REFERENCES PHIEUNHAPHANG(ID),
                        FOREIGN KEY (MaMatHang) REFERENCES MATHANG(ID)
                    );
            """;
                statement.execute(createChiTietPhieuNhapSQL);
                System.out.println("Tạo bảng CHITIETPHIEUNHAP thành công!");
            }

            // Kiểm tra và tạo bảng CHITIETPHIEUXUAT nếu chưa có
            if (!isTableExist( "CHITIETPHIEUXUAT")) {
                String createChiTietPhieuXuatSQL = """
                    CREATE TABLE CHITIETPHIEUXUAT (
                        MaPhieuXuat INT,
                        MaMatHang INT,
                        SoLuong INT,
                        DonGiaXuat DECIMAL(19,4),
                        ThanhTien DECIMAL(19,4),
                        GhiChu NVARCHAR(1000),
                        PRIMARY KEY (MaPhieuXuat, MaMatHang),
                        FOREIGN KEY (MaPhieuXuat) REFERENCES PHIEUXUATHANG(ID),
                        FOREIGN KEY (MaMatHang) REFERENCES MATHANG(ID)
                    );
            """;
                statement.execute(createChiTietPhieuXuatSQL);
                System.out.println("Tạo bảng CHITIETPHIEUXUAT thành công!");
            }

            // Kiểm tra và tạo bảng CHITIETPHIEUTHU nếu chưa có
            if (!isTableExist( "CHITIETPHIEUTHU")) {
                String createChiTietPhieuThuSQL = """
                    CREATE TABLE CHITIETPHIEUTHU (
                        MaPhieuThu INT PRIMARY KEY,
                        SoTienThu DECIMAL(19,4),
                        GhiChu NVARCHAR(1000),
                        FOREIGN KEY (MaPhieuThu) REFERENCES PHIEUTHUTIEN(ID)
                    );
            """;
                statement.execute(createChiTietPhieuThuSQL);
                System.out.println("Tạo bảng CHITIETPHIEUTHU thành công!");
            }

            String insertSampleDataSQL = """

    INSERT INTO CHUCVU (TenChucVu, GhiChu)
    VALUES
        (N'Nhân viên', N'Nhân viên troll troll'),
        (N'Quản lý', N'Quản lý có quyền thêm/ kick nv');

    INSERT INTO LOAIDAILY (SoNoToiDa, TenLoai, GhiChu)
    VALUES
        (5000000, N'Loại 1', N'Loại 1 nợ tối đa 5tr'),
        (10000000, N'Loại 2', N'Loại 2 nợ tối đa 5tr');


    INSERT INTO DONVITINH (TenDonViTinh, GhiChu)
    VALUES
        (N'chiếc', N'bút, đồng hồ, ...'),
        (N'cái', N'xoong, chảo, ...'),
        (N'kí', N'kí táo, kí thịt, ...'),
        (N'hộp', N'hộp nước, hộp hộp, ....'),
        (N'bị', N'bị nhựa, ...'),
        (N'lít', N'lít nước, lít sữa, ...');


    INSERT INTO NHANVIEN (HoTen, GioiTinh, NgaySinh, SDT, Email, MaChucVu, Luong, GhiChu)
    VALUES
        (N'Nguyễn Văn 1', N'Nam', '2024-04-13', '0000000001', 'aaa@bbb.ccc', 1, 776164533, N''),
        (N'Nguyễn Văn 2', N'Nam', '2024-04-13', '0000000002', 'aaa@bbb.ccc', 2, 926876912, N''),
        (N'Nguyễn Văn admin', N'Nam', '2024-04-13', '0000000002', 'aaa@bbb.ccc', 2, 926876912, N''),
        (N'Nguyễn Văn 3', N'Nam', '2024-04-13', '0000000003', 'aaa@bbb.ccc', 1, 666998093, N'');

    INSERT INTO TAIKHOAN (UserName, Password, MaNhanVien)
    VALUES ('user', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1);

    INSERT INTO TAIKHOAN (UserName, Password, MaNhanVien)
    VALUES ('admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 2);

    INSERT INTO THAMSO (SoDaiLyToiDaMoiQuan, TyLeDonGiaXuat, ChoPhepVuotNo)
    VALUES (5, 1.02, 0);

      INSERT INTO QUAN (TenQuan, GhiChu)
                                VALUES ('Quan 1', 'Ghi chú cho quận 1'),
                                       ('Quan 2', 'Ghi chú cho quận 2'),
                                       ('Quan 3', 'Ghi chú cho quận 3');
""";



            statement.execute(insertSampleDataSQL);



            System.out.println("Thêm dữ liệu mẫu thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
