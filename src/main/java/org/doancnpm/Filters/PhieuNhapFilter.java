package org.doancnpm.Filters;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.doancnpm.Models.PhieuNhap;

public class PhieuNhapFilter implements IFilter{
    ObservableList<PhieuNhap> input;
    ObservableList<PhieuNhap> output;

    private String maPhieuNhap;


    private String nhaCungCap;
    private Integer maNhanVien;

    @Override
    public ObservableList<PhieuNhap> Filter() {
        if (output == null) {
            output = FXCollections.observableArrayList(); // Khởi tạo output nếu nó là null
        } else {
            output.clear(); // Xóa danh sách output cũ nếu nó không null
        }

        for (PhieuNhap phieuNhap : input) {
            boolean matches = true;
            if (maPhieuNhap != null && !maPhieuNhap.isEmpty() && !phieuNhap.getMaPhieuNhap().contains(maPhieuNhap)) {
                matches = false;
            }
            if (nhaCungCap != null && !nhaCungCap.isEmpty() && !phieuNhap.getNhaCungCap().contains(nhaCungCap)) {
                matches = false;
            }
            if (maNhanVien != null && !phieuNhap.getMaNhanVien().equals(maNhanVien)) {
                matches = false;
            }

            if (matches) {
                output.add(phieuNhap);
            }
        }

        return output;
    }
    public String getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(String nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public void setInput(ObservableList<PhieuNhap> input) {
        this.input = input;
    }

    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public Integer getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(Integer maNhanVien) {
        this.maNhanVien = maNhanVien;
    }
}
