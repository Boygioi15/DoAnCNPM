package org.doancnpm.Filters;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.doancnpm.Models.MatHang;
import org.doancnpm.Models.PhieuThu;

public class PhieuThuFilter implements IFilter{
    ObservableList<PhieuThu> input;
    ObservableList<PhieuThu> output;

    private String maPhieuThu;
    private Integer maDaiLy;
    private Integer maNhanVien;

    @Override
    public ObservableList<PhieuThu> Filter() {
        if (output == null) {
            output = FXCollections.observableArrayList(); // Khởi tạo output nếu nó là null
        } else {
            output.clear(); // Xóa danh sách output cũ nếu nó không null
        }

        for (PhieuThu phieuThu : input) {
            boolean matches = true;

            if (maPhieuThu != null && !maPhieuThu.isEmpty() && !phieuThu.getMaPhieuThu().contains(maPhieuThu)) {
                matches = false;
            }
            if (maDaiLy != null && !phieuThu.getMaDaiLy().equals(maDaiLy)) {
                matches = false;
            }

            if (maNhanVien != null && !phieuThu.getMaNhanVien().equals(maNhanVien)) {
                matches = false;
            }

            if (matches) {
                output.add(phieuThu);
            }
        }

        return output;
    }
    public void setInput(ObservableList<PhieuThu> input) {
        this.input = input;
    }
    public String getMaPhieuThu() {return maPhieuThu;}

    public void setMaPhieuThu(String maPhieuThu) {this.maPhieuThu = maPhieuThu;}

    public Integer getMaNhanVien() {return maNhanVien;}

    public void setMaNhanVien(Integer maNhanVien) {this.maNhanVien = maNhanVien;}

    public Integer getMaDaiLy() {return maDaiLy;}

    public void setMaDaiLy(Integer maDaiLy) {this.maDaiLy = maDaiLy;}
}
