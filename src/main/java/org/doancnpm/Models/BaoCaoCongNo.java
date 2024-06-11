package org.doancnpm.Models;

import java.util.Date;

public class BaoCaoCongNo {
    private int STT;
    private int maDaiLy;
    private Date ngayLapPhieu;
    private double noDau;
    private double noCuoi;
    private double phatSinh;

    public BaoCaoCongNo(int STT, int maDaiLy, Date ngayLapPhieu, double noDau, double noCuoi, double phatSinh) {
        this.STT = STT;
        this.maDaiLy = maDaiLy;
        this.ngayLapPhieu = ngayLapPhieu;
        this.noCuoi = noCuoi;
        this.noDau = noDau;
        this.phatSinh = phatSinh;
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

    public double getNoCuoi() {
        return noCuoi;
    }

    public void setNoCuoi(double noCuoi) {
        this.noCuoi = noCuoi;
    }

    public double getNoDau() {
        return noDau;
    }

    public void setNoDau(double noDau) {
        this.noDau = noDau;
    }

    public int getSTT() {
        return STT;
    }

    public void setSTT(int STT) {
        this.STT = STT;
    }

    public double getPhatSinh() {
        return phatSinh;
    }

    public void setPhatSinh(double phatSinh) {
        this.phatSinh = phatSinh;
    }
}
