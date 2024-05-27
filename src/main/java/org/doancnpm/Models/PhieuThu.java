package org.doancnpm.Models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.sql.Date;

public class PhieuThu {
    private BooleanProperty selected = new SimpleBooleanProperty(false);
    private Integer ID;
    private String MaPhieuThu;
    private Integer MaDaiLi;
    private Integer MaNhanVien;
    private Date NgayLap;
    private int SoTienThu;
    private String GhiChu;

    public PhieuThu() {

    }

    public PhieuThu(Integer ID, String maPhieuThu, Integer maDaiLi, Integer maNhanVien, Date ngayLap,int SoTienThu,String GhiChu) {
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

    public Integer getID() {
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

    public Integer getMaNhanVien() {
        return MaNhanVien;
    }

    public void setMaNhanVien(Integer maNhanVien) {
        MaNhanVien = maNhanVien;
    }

    public Integer getMaDaiLi() {
        return MaDaiLi;
    }

    public void setMaDaiLi(Integer maDaiLi) {
        MaDaiLi = maDaiLi;
    }

    public String getMaPhieuThu() {
        return MaPhieuThu;
    }

    public void setMaPhieuThu(String maPhieuThu) {
        MaPhieuThu = maPhieuThu;
    }
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
