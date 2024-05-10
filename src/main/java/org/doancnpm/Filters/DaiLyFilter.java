package org.doancnpm.Filters;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import org.doancnpm.Models.DaiLy;

public class DaiLyFilter implements IFilter<DaiLy> {
    ObservableList<DaiLy> input;
    ObservableList<DaiLy> output;

    private String maDaiLy;
    private String maQuan;
    private String maLoaiDaiLy;
    private String tenDaiLy;

    @Override
    public ObservableList<DaiLy> Filter() {
        if (output == null) {
            output = FXCollections.observableArrayList(); // Khởi tạo output nếu nó là null
        } else {
            output.clear(); // Xóa danh sách output cũ nếu nó không null
        }
        output.addAll(input);
        for (DaiLy daiLy : input) {
            if (maDaiLy != null && !maDaiLy.isEmpty() && !daiLy.getMaDaiLy().contains(maDaiLy)) {
                output.remove(daiLy);
                continue;
            }
            if (maQuan != null && !maQuan.isEmpty() && !daiLy.getMaQuan().contains(maQuan)) {
                output.remove(daiLy);
                continue;
            }
            if (maLoaiDaiLy != null && !maLoaiDaiLy.isEmpty() && !daiLy.getMaLoaiDaiLy().contains(maLoaiDaiLy)) {
                output.remove(daiLy);
                continue;
            }
            if (tenDaiLy != null && !tenDaiLy.isEmpty() && !daiLy.getTenDaiLy().contains(tenDaiLy)) {
                output.remove(daiLy);
            }
        }
        return output;
    }

    public void setInput(ObservableList<DaiLy> input) {
        this.input = input;
    }
    public void setMaDaiLy(String maDaiLy) {
        this.maDaiLy = maDaiLy;
    }
    public void setMaQuan(String maQuan) {
        this.maQuan = maQuan;
    }
    public void setMaLoaiDaiLy(String maLoaiDaiLy) {
        this.maLoaiDaiLy = maLoaiDaiLy;
    }
    public void setTenDaiLy(String tenDaiLy) {
        this.tenDaiLy = tenDaiLy;
    }

    public ObservableList<DaiLy> getInput() {
        return input;
    }
    public String getMaDaiLy() {
        return maDaiLy;
    }
    public String getMaQuan() {
        return maQuan;
    }
    public String getMaLoaiDaiLy() {
        return maLoaiDaiLy;
    }
    public String getTenDaiLy() {
        return tenDaiLy;
    }
}
