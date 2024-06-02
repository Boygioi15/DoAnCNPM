package org.doancnpm.Models;

public class DonViTinh {
    private Integer id;
    private String maDVT;
    private String tenDVT;
    private String ghiChu;

    public DonViTinh() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaDVT() {
        return maDVT;
    }

    public void setMaDVT(String maDVT) {
        this.maDVT = maDVT;
    }

    public String getTenDVT() {
        return tenDVT;
    }

    public void setTenDVT(String tenDVT) {
        this.tenDVT = tenDVT;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

}