package org.doancnpm.SQLUltilities;

import org.doancnpm.Models.DatabaseDriver;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CalculateSQL {
    private static CalculateSQL singleton = null;

    public static CalculateSQL getInstance() {
        if (singleton == null) {
            singleton = new CalculateSQL();
        }
        return singleton;
    }

    public double calculateTongNoDaiLy() {
        Connection conn = DatabaseDriver.getConnect();
        double tongNo = 0;
        if (conn != null) {
            try {
                String sql = "SELECT SUM(NoHienTai) AS TongNo FROM DAILY";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                if (rs.next()) {
                    tongNo = rs.getDouble("TongNo");
                }

                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
        return tongNo;
    }

    public double calculateTotalReceiptsForMonth(int month, int year) {
        Connection conn = DatabaseDriver.getConnect();
        double totalReceipts = 0;
        if (conn != null) {
            try {
                String sql = "SELECT SUM(SoTienThu) AS TotalReceipts FROM PHIEUTHUTIEN WHERE MONTH(NgayLapPhieu) = ? AND YEAR(NgayLapPhieu) = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, month);
                pstmt.setInt(2, year);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    totalReceipts = rs.getDouble("TotalReceipts");
                }

                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
        return totalReceipts;
    }

    public double calculateTotalValueExportsForMonth(int month, int year) {
        Connection conn = DatabaseDriver.getConnect();
        double totalValueExports = 0;
        if (conn != null) {
            try {
                String sql = "SELECT SUM(TongTien) AS TotalValueExports FROM PHIEUXUATHANG WHERE MONTH(NgayLapPhieu) = ? AND YEAR(NgayLapPhieu) = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, month);
                pstmt.setInt(2, year);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    totalValueExports = rs.getDouble("TotalValueExports");
                }

                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
        return totalValueExports;
    }

    public Map<String, Double> calculateTotalDebtUntilMonth(int year) {
        Map<String, Double> totalDebts = new HashMap<>();

        // Lấy tổng nợ của tất cả các đại lý
        double totalDebtAll = calculateTongNoDaiLy();
        System.out.println(totalDebtAll);

        int currentMonth = LocalDate.now().getMonthValue(); // Lấy tháng hiện tại
        int currentYear = LocalDate.now().getYear(); // Lấy năm hiện tại
        totalDebts.put(currentYear + "-" + currentMonth, totalDebtAll);
        for (int month = currentMonth + 1; month <= 12; month++) {
            totalDebts.put(currentYear + "-" + month, 0.0);
        }
        // Bắt đầu tính toán tổng nợ cho từng tháng của các năm từ năm hiện tại về quá khứ
        for (int yearTest = currentYear; yearTest >= year; yearTest--) {
            // Xác định tháng bắt đầu dựa trên năm
            int startMonthOfYear = (yearTest == currentYear) ? currentMonth : 12;
            int endMonth = (year == currentYear || yearTest != currentYear) ? 2 : 1;

            // Bắt đầu tính toán tổng nợ từ tháng bắt đầu của năm đó
            for (int month = startMonthOfYear; month >= endMonth; month--) {
                if (month == 1 && yearTest != year) {
                    totalDebtAll += calculateTotalReceiptsForMonth(1, yearTest) - calculateTotalValueExportsForMonth(1, yearTest);
                    totalDebts.put((yearTest - 1) + "-" + 12, totalDebtAll);
                    // Xuất dòng lệnh kiểm tra
                    System.out.println("Năm: " + (yearTest - 1) + ", Tháng: " + 12 + ", Tổng nợ: " + totalDebtAll + "Loại 1");
                } else {
                    totalDebtAll += calculateTotalReceiptsForMonth(month, yearTest) - calculateTotalValueExportsForMonth(month, yearTest);
                    totalDebts.put(yearTest + "-" + (month - 1), totalDebtAll);
                    // Xuất dòng lệnh kiểm tra
                    System.out.println("Năm: " + yearTest + ", Tháng: " + (month - 1) + ", Tổng nợ: " + totalDebtAll + "Loại 2");
                }
            }
        }
        return totalDebts;
    }

    public int calSoMatHangTonKho() {
        Connection conn = DatabaseDriver.getConnect();
        int soMatHangTonKho = 0;
        if (conn != null) {
            try {
                String sql = "SELECT COUNT(*) AS SoMatHangTonKho FROM MATHANG WHERE SoLuong > 0";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                if (rs.next()) {
                    soMatHangTonKho = rs.getInt("SoMatHangTonKho");
                }

                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
        return soMatHangTonKho;
    }

    public int calTongSoMatHang() {
        Connection conn = DatabaseDriver.getConnect();
        int tongSoMatHang = 0;
        if (conn != null) {
            try {
                String sql = "SELECT COUNT(*) AS TongSoMatHang FROM MATHANG";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                if (rs.next()) {
                    tongSoMatHang = rs.getInt("TongSoMatHang");
                }

                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
        return tongSoMatHang;
    }

    public int calSoNhanVien() {
        Connection conn = DatabaseDriver.getConnect();
        int soNhanVien = 0;
        if (conn != null) {
            try {
                String sql = "SELECT COUNT(*) AS SoNhanVien FROM NHANVIEN";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                if (rs.next()) {
                    soNhanVien = rs.getInt("SoNhanVien");
                }

                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
        return soNhanVien;
    }

    public int calSoDaiLy() {
        Connection conn = DatabaseDriver.getConnect();
        int soDaiLy = 0;
        if (conn != null) {
            try {
                String sql = "SELECT COUNT(*) AS SoDaiLy FROM DAILY";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                if (rs.next()) {
                    soDaiLy = rs.getInt("SoDaiLy");
                }

                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
        return soDaiLy;
    }

    public double calTongGiaTriKhoHang() {
        Connection conn = DatabaseDriver.getConnect();
        double tongGiaTriKhoHang = 0;
        if (conn != null) {
            try {
                String sql = "SELECT SUM(SoLuong * DonGiaNhap) AS TongGiaTriKhoHang FROM MATHANG";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                if (rs.next()) {
                    tongGiaTriKhoHang = rs.getDouble("TongGiaTriKhoHang");
                }

                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
        return tongGiaTriKhoHang;
    }

    public int calSoLuongMatHang() {
        Connection conn = DatabaseDriver.getConnect();
        int soLuongMatHang = 0;
        if (conn != null) {
            try {
                String sql = "SELECT COUNT(*) AS SoLuongMatHang FROM MATHANG";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                if (rs.next()) {
                    soLuongMatHang = rs.getInt("SoLuongMatHang");
                }

                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
        return soLuongMatHang;
    }

    // Phương thức tính tổng số lượng hàng tồn kho
    public int calSoLuongHangTonKho() {
        Connection conn = DatabaseDriver.getConnect();
        int soLuongHangTonKho = 0;
        if (conn != null) {
            try {
                String sql = "SELECT SUM(SoLuong) AS SoLuongHangTonKho FROM MATHANG";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                if (rs.next()) {
                    soLuongHangTonKho = rs.getInt("SoLuongHangTonKho");
                }

                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
        return soLuongHangTonKho;
    }

    public Map<Integer, Integer> calSoPhieuXuatVoiDaiLyTheoThang(int month, int year) {
        Map<Integer, Integer> result = new HashMap<>();
        Connection conn = DatabaseDriver.getConnect();
        if (conn != null) {
            try {
                String sql = "SELECT MaDaiLy, COUNT(*) AS SoPhieuXuat " +
                        "FROM PHIEUXUATHANG " +
                        "WHERE MONTH(NgayLapPhieu) = ? AND YEAR(NgayLapPhieu) = ? " +
                        "GROUP BY MaDaiLy";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, month);
                stmt.setInt(2, year);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int maDaiLy = rs.getInt("MaDaiLy");
                    int soPhieuXuat = rs.getInt("SoPhieuXuat");
                    result.put(maDaiLy, soPhieuXuat);
                }

                rs.close();
                stmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }
        return result;
    }

    public Map<Integer, Double> calTongGiaTriPhieuXuatVoiDaiLyTheoThang(int month, int year) {
        Map<Integer, Double> result = new HashMap<>();
        Connection conn = DatabaseDriver.getConnect();
        if (conn != null) {
            try {
                String sql = "SELECT MaDaiLy, SUM(TongTien) AS TongGiaTri " +
                        "FROM PHIEUXUATHANG " +
                        "WHERE MONTH(NgayLapPhieu) = ? AND YEAR(NgayLapPhieu) = ? " +
                        "GROUP BY MaDaiLy";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, month);
                stmt.setInt(2, year);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int maDaiLy = rs.getInt("MaDaiLy");
                    double tongGiaTri = rs.getDouble("TongGiaTri");
                    result.put(maDaiLy, tongGiaTri);
                }

                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
        }

        return result;
    }

}
