package org.doancnpm.Models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.sql.Date;

public class DaiLy {
    private BooleanProperty selected = new SimpleBooleanProperty(false);
    private Integer ID;
    private String maDaiLy;
    private Integer maQuan;
    private Integer maLoaiDaiLy;

    private String tenDaiLy = "Đại lý mới";
    private String dienThoai = "";
    private String email = "";
    private String diaChi = "";
    private Date ngayTiepNhan;
    private Integer noHienTai = 0;
    private String ghiChu = "";


    public DaiLy() {
    }
    public int getID() {
        return ID;
    }
    public String getMaDaiLy() {
        return maDaiLy;
    }
    public Integer getMaQuan() {
        return maQuan;
    }
    public Integer getMaLoaiDaiLy() {
        return maLoaiDaiLy;
    }
    public String getTenDaiLy() {
        return tenDaiLy;
    }
    public String getDienThoai() {
        return dienThoai;
    }
    public String getEmail() {
        return email;
    }
    public String getDiaChi() {
        return diaChi;
    }
    public Date getNgayTiepNhan() {
        return ngayTiepNhan;
    }
    public Integer getNoHienTai() {
        return noHienTai;
    }
    public String getGhiChu() {
        return ghiChu;
    }


    public void setID(Integer ID) {
        this.ID = ID;
    }
    public void setMaDaiLy(String maDaiLy) {
        this.maDaiLy = maDaiLy;
    }
    public void setMaQuan(Integer maQuan) {
        this.maQuan = maQuan;
    }
    public void setMaLoaiDaiLy(Integer maLoaiDaiLy) {
        this.maLoaiDaiLy = maLoaiDaiLy;
    }
    public void setNoHienTai(Integer noHienTai) {
        this.noHienTai = noHienTai;
    }
    public void setTenDaiLy(String tenDaiLy) {
        this.tenDaiLy = tenDaiLy;
    }
    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
    public void setNgayTiepNhan(Date ngayTiepNhan) {
        this.ngayTiepNhan = ngayTiepNhan;
    }
    public void setNoHienTai(int noHienTai) {
        this.noHienTai = noHienTai;
    }
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
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
