package org.doancnpm.Models;

public class MatHang {
    private int ID;
    private int maDonViTinh;
    private String tenMaHang;
    private String ghiChu;
    private String maMatHang;
    public MatHang(String maMatHang,int maDonViTinh, String tenMaHang, String ghiChu) {
        this.maDonViTinh = maDonViTinh;
        this.tenMaHang = tenMaHang;
        this.ghiChu = ghiChu;
        this.maMatHang = maMatHang;
    }

    public MatHang(int maDonViTinh, String tenMaHang, String ghiChu) {
        this.maDonViTinh = maDonViTinh;
        this.ghiChu = ghiChu;
        this.tenMaHang = tenMaHang;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public int getMaDonViTinh() {
        return maDonViTinh;
    }

    public void setMaDonViTinh(int maDonViTinh) {
        this.maDonViTinh = maDonViTinh;
    }

    public String getMaMatHang() {
        return maMatHang;
    }

    public void setMaMatHang(String maMatHang) {
        this.maMatHang = maMatHang;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTenMaHang() {
        return tenMaHang;
    }

    public void setTenMaHang(String tenMaHang) {
        this.tenMaHang = tenMaHang;
    }


    public int getID() {
        return ID;
    }
}
