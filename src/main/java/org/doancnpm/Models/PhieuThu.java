package org.doancnpm.Models;

import java.sql.Date;

public class PhieuThu {
    private int ID;
    private String MaPhieuThu;
    private String MaDaiLi;
    private String MaNhanVien;
    private Date NgayLap;
    private int SoTienThu;
    private String GhiChu;

    public PhieuThu() {

    }

    public PhieuThu(int ID, String maPhieuThu, String maDaiLi, String maNhanVien, Date ngayLap,int SoTienThu,String GhiChu) {
        this.ID = ID;
        this.MaPhieuThu = maPhieuThu;
        this.MaDaiLi = maDaiLi;
        this.MaNhanVien = maNhanVien;
        this.NgayLap = ngayLap;
        this.SoTienThu = SoTienThu;
        this.GhiChu = GhiChu;
    }

    public int getSoTienThu() {
        return SoTienThu;
    }

    public void setSoTienThu(int soTienThu) {
        SoTienThu = soTienThu;
    }

    public String getGhiChu() {
        return GhiChu;
    }

    public void setGhiChu(String ghiChu) {
        GhiChu = ghiChu;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Date getNgayLap() {
        return NgayLap;
    }

    public void setNgayLap(Date ngayLap) {
        NgayLap = ngayLap;
    }

    public String getMaNhanVien() {
        return MaNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        MaNhanVien = maNhanVien;
    }

    public String getMaDaiLi() {
        return MaDaiLi;
    }

    public void setMaDaiLi(String maDaiLi) {
        MaDaiLi = maDaiLi;
    }

    public String getMaPhieuThu() {
        return MaPhieuThu;
    }

    public void setMaPhieuThu(String maPhieuThu) {
        MaPhieuThu = maPhieuThu;
    }
}
