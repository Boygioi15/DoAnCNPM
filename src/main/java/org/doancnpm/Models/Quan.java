package org.doancnpm.Models;

public class Quan {
    private Integer id;
    private String maQuan;
    private String tenQuan;
    private String GhiChu;

    public Quan() {
    }

    public Quan(Integer id, String ghiChu, String tenQuan, String maQuan) {
        this.id = id;
        GhiChu = ghiChu;
        this.tenQuan = tenQuan;
        this.maQuan = maQuan;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGhiChu() {
        return GhiChu;
    }

    public void setGhiChu(String ghiChu) {
        GhiChu = ghiChu;
    }

    public String getTenQuan() {
        return tenQuan;
    }

    public void setTenQuan(String tenQuan) {
        this.tenQuan = tenQuan;
    }

    public String getMaQuan() {
        return maQuan;
    }

    public void setMaQuan(String maQuan) {
        this.maQuan = maQuan;
    }
}