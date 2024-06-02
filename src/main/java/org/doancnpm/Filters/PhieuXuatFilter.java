package org.doancnpm.Filters;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.doancnpm.Models.PhieuXuat;

public class PhieuXuatFilter implements IFilter{
    ObservableList<PhieuXuat> input;
    ObservableList<PhieuXuat> output;

    private String maPhieuXuat;

    public Integer getMaDaiLy() {
        return maDaiLy;
    }

    public void setMaDaiLy(Integer maDaiLy) {
        this.maDaiLy = maDaiLy;
    }

    private Integer maDaiLy;
    private Integer maNhanVien;

    @Override
    public ObservableList<PhieuXuat> Filter() {
        if (output == null) {
            output = FXCollections.observableArrayList(); // Khởi tạo output nếu nó là null
        } else {
            output.clear(); // Xóa danh sách output cũ nếu nó không null
        }

        for (PhieuXuat phieuXuat : input) {
            boolean matches = true;
            if (maPhieuXuat != null && !maPhieuXuat.isEmpty() && !phieuXuat.getMaPhieuXuat().contains(maPhieuXuat)) {
                matches = false;
            }
            if (maDaiLy != null && !phieuXuat.getMaDaiLy().equals(maDaiLy)) {
                matches = false;
            }
            if (maNhanVien != null && !phieuXuat.getMaNhanVien().equals(maNhanVien)) {
                matches = false;
            }

            if (matches) {
                output.add(phieuXuat);
            }
        }

        return output;
    }
    public void setInput(ObservableList<PhieuXuat> input) {
        this.input = input;
    }

    public String getMaPhieuXuat() {
        return maPhieuXuat;
    }

    public void setMaPhieuXuat(String maPhieuXuat) {
        this.maPhieuXuat = maPhieuXuat;
    }

    public Integer getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(Integer maNhanVien) {
        this.maNhanVien = maNhanVien;
    }
}
