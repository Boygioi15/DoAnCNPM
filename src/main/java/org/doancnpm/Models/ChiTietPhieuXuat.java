package org.doancnpm.Models;

public class ChiTietPhieuXuat {
    Integer maPhieuXuat;
    Integer maMatHang;
    Integer soLuong;
    Long thanhTien;
    Long donGiaXuat;

    public Long getDonGiaXuat() {
        return donGiaXuat;
    }

    public void setDonGiaXuat(Long donGiaXuat) {
        this.donGiaXuat = donGiaXuat;
    }

    public Integer getMaPhieuXuat() {
        return maPhieuXuat;
    }

    public void setMaPhieuXuat(Integer maPhieuXuat) {
        this.maPhieuXuat = maPhieuXuat;
    }

    public Integer getMaMatHang() {
        return maMatHang;
    }

    public void setMaMatHang(Integer maMatHang) {
        this.maMatHang = maMatHang;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public Long getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(Long thanhTien) {
        this.thanhTien = thanhTien;
    }
}
