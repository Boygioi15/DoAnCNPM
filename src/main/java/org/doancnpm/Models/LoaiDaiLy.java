package org.doancnpm.Models;

public class LoaiDaiLy {
    private Integer id;
    private String maLoai;
    private Long soNoToiDa;
    private String tenLoai;
    private String ghiChu;

    public LoaiDaiLy() {
    }

    public LoaiDaiLy(Integer id, String maLoai, Long soNoToiDa, String tenLoai, String ghiChu) {
        this.id = id;
        this.maLoai = maLoai;
        this.soNoToiDa = soNoToiDa;
        this.tenLoai = tenLoai;
        this.ghiChu = ghiChu;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public Long getSoNoToiDa() {
        return soNoToiDa;
    }

    public void setSoNoToiDa(Long soNoToiDa) {
        this.soNoToiDa = soNoToiDa;
    }

    public String getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(String maLoai) {
        this.maLoai = maLoai;
    }
}