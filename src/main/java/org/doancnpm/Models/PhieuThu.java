package org.doancnpm.Models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.sql.Date;

public class PhieuThu {
    private BooleanProperty selected = new SimpleBooleanProperty(false);
    private Integer ID;
    private String MaPhieuThu;
    private Integer MaDaiLy;
    private Integer MaNhanVien;
    private Date NgayLap;
    private int SoTienThu;
    private String GhiChu;

    public PhieuThu() {

    }

    public PhieuThu(Integer ID, String maPhieuThu, Integer maDaiLy, Integer maNhanVien, Date ngayLap,int SoTienThu,String GhiChu) {
        this.ID = ID;
        this.MaPhieuThu = maPhieuThu;
        this.MaDaiLy = maDaiLy;
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

    public Integer getMaDaiLy() {
        return MaDaiLy;
    }

    public void setMaDaiLy(Integer maDaiLy) {
        MaDaiLy = maDaiLy;
    }

    public String getMaPhieuThu() {
        return MaPhieuThu;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
