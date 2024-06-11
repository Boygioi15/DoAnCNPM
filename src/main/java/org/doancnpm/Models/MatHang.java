package org.doancnpm.Models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class MatHang {
    private BooleanProperty selected = new SimpleBooleanProperty(false);

    private Integer ID;
    private String maMatHang;
    private String tenMatHang;
    private Integer maDVT;
    private Long donGiaNhap;
    private Long donGiaXuat;
    private Integer soLuong;
    private String ghiChu;
    private Boolean isDeleted;

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getMaMatHang() {
        return maMatHang;
    }

    public void setMaMatHang(String maMatHang) {
        this.maMatHang = maMatHang;
    }

    public String getTenMatHang() {
        return tenMatHang;
    }

    public void setTenMatHang(String tenMatHang) {
        this.tenMatHang = tenMatHang;
    }

    public Integer getMaDVT() {
        return maDVT;
    }

    public void setMaDVT(Integer maDVT) {
        this.maDVT = maDVT;
    }

    public Long getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(Long donGiaNhap) {
        this.donGiaNhap = donGiaNhap;
    }

    public Long getDonGiaXuat() {
        return donGiaXuat;
    }

    public void setDonGiaXuat(Long donGiaXuat) {
        this.donGiaXuat = donGiaXuat;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public String getGhiChu() {
        return ghiChu;
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
