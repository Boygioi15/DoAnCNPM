package org.doancnpm.Filters;

import javafx.collections.ObservableList;
import org.doancnpm.Models.DaiLy;

public class DaiLyFilter implements IFilter<DaiLy> {
    ObservableList<DaiLy> input;

    private String maDaiLy;
    private String maQuan;
    private String maLoaiDaiLy;
    private String tenDaiLy;

    @Override
    public ObservableList<DaiLy> Filter(){
        return input;
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
