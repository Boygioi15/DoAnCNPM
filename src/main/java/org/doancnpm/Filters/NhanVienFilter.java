package org.doancnpm.Filters;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Ultilities.CheckExist;

public class NhanVienFilter implements IFilter<NhanVien> {
    ObservableList<NhanVien> input;
    ObservableList<NhanVien> output;

    private String maNhanVien;
    private Integer maChucVu;
    private String tenNhanVien;

    @Override
    public ObservableList<NhanVien> Filter() {
        if (output == null) {
            output = FXCollections.observableArrayList(); // Khởi tạo output nếu nó là null
        } else {
            output.clear(); // Xóa danh sách output cũ nếu nó không null
        }
        output.addAll(input);
        for (NhanVien nhanVien : input) {
            if (maNhanVien != null && !maNhanVien.isEmpty() && !CheckExist.normalize(nhanVien.getMaNhanVien()).contains(CheckExist.normalize(maNhanVien))) {
                output.remove(nhanVien);
                continue;
            }
            
            if (maChucVu != null && !nhanVien.getMaChucVu().equals(maChucVu)) {
                output.remove(nhanVien);
                continue;
            }

            if (tenNhanVien != null && !tenNhanVien.isEmpty() && !CheckExist.normalize(nhanVien.getHoTen()).contains(CheckExist.normalize(tenNhanVien))) {
                output.remove(nhanVien);
            }
        }
        return output;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public Integer getMaChucVu() {
        return maChucVu;
    }

    public void setMaChucVu(Integer maChucVu) {
        this.maChucVu = maChucVu;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public void setInput(ObservableList<NhanVien> input) {
        this.input = input;
    }
}
