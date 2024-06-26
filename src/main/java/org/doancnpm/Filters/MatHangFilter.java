package org.doancnpm.Filters;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.doancnpm.Models.MatHang;
import org.doancnpm.Ultilities.CheckExist;

public class MatHangFilter implements IFilter<MatHang> {
    ObservableList<MatHang> input;
    ObservableList<MatHang> output;

    private String maMH = null;
    private String tenMH = null;
    private Integer maDVT = null;
    private Boolean tonKho = null;

    @Override
    public ObservableList<MatHang> Filter() {
        if (output == null) {
            output = FXCollections.observableArrayList(); // Khởi tạo output nếu nó là null
        } else {
            output.clear(); // Xóa danh sách output cũ nếu nó không null
        }
        output.addAll(input);
        for (MatHang matHang : input) {
            if (maMH != null && !maMH.isEmpty() && !CheckExist.normalize(matHang.getMaMatHang()).contains(CheckExist.normalize(maMH))) {
                output.remove(matHang);
                continue;
            }
            if (tenMH != null && !tenMH.isEmpty() && !CheckExist.normalize(matHang.getTenMatHang()).contains(CheckExist.normalize(tenMH))) {
                output.remove(matHang);
                continue;
            }
            if (maDVT != null && !matHang.getMaDVT().equals(maDVT)) {
                output.remove(matHang);
                System.out.println(matHang.getMaDVT());
                continue;
            }
            if(tonKho==null){
                continue;
            }
            if(!tonKho){
                if(matHang.getSoLuong().equals(0)){
                    output.remove(matHang);
                }
            }
            else{
                if(!matHang.getSoLuong().equals(0)){
                    output.remove(matHang);
                }
            }
        }
        return output;
    }

    public void setMaMH(String maMH) {
        this.maMH = maMH;
    }

    public void setTenMH(String tenMH) {
        this.tenMH = tenMH;
    }

    public void setMaDVT(Integer maDVT) {
        this.maDVT = maDVT;
    }

    public void setTonKho(Boolean tonKho) {
        this.tonKho = tonKho;
    }

    public void setOutput(ObservableList<MatHang> output) {
        this.output = output;
    }

    public void setInput(ObservableList<MatHang> input) {
        this.input = input;
    }
}
