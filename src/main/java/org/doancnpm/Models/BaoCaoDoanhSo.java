package org.doancnpm.Models;

import java.util.Date;

public class BaoCaoDoanhSo {
    private int STT;
    private int maDaiLy;
    private Date ngayLapPhieu;
    private int soPhieuXuat;
    private double tongTriGia;
    private double tyLe;

    public BaoCaoDoanhSo() {

    }

    public BaoCaoDoanhSo(int STT, int maDaiLy, Date ngayLapPhieu, int soPhieuXuat, double tongTriGia, double tyLe) {
        this.STT = STT;
        this.maDaiLy = maDaiLy;
        this.ngayLapPhieu = ngayLapPhieu;
        this.soPhieuXuat = soPhieuXuat;
        this.tongTriGia = tongTriGia;
        this.tyLe = tyLe;
    }

    public int getSTT() {
        return STT;
    }

    public void setSTT(int STT) {
        this.STT = STT;
    }

    public int getMaDaiLy() {
        return maDaiLy;
    }

    public void setMaDaiLy(int maDaiLy) {
        this.maDaiLy = maDaiLy;
    }

    public Date getNgayLapPhieu() {
        return ngayLapPhieu;
    }

    public void setNgayLapPhieu(Date ngayLapPhieu) {
        this.ngayLapPhieu = ngayLapPhieu;
    }

    public int getSoPhieuXuat() {
        return soPhieuXuat;
    }

    public void setSoPhieuXuat(int soPhieuXuat) {
        this.soPhieuXuat = soPhieuXuat;
    }

    public double getTongTriGia() {
        return tongTriGia;
    }

    public void setTongTriGia(double tongTriGia) {
        this.tongTriGia = tongTriGia;
    }

    public double getTyLe() {
        return tyLe;
    }

    public void setTyLe(double tyLe) {
        this.tyLe = tyLe;
    }
}
