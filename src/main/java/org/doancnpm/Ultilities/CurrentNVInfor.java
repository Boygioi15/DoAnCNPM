package org.doancnpm.Ultilities;

import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.TaiKhoan;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class CurrentNVInfor {
    private static CurrentNVInfor instance;
    private NhanVien loggedInNhanVien;
    private TaiKhoan taiKhoanOfNhanien;
    private String password;
    public static synchronized CurrentNVInfor getInstance() {
        if (instance == null) {
            instance = new CurrentNVInfor();
        }
        return instance;
    }

    private CurrentNVInfor() {
    }

    public TaiKhoan getTaiKhoanOfNhanien() {
        return taiKhoanOfNhanien;
    }

    public void setTaiKhoanOfNhanien(TaiKhoan taiKhoanOfNhanien) {
        this.taiKhoanOfNhanien = taiKhoanOfNhanien;
    }

    public NhanVien getLoggedInNhanVien() {
        return loggedInNhanVien;
    }

    public void setLoggedInNhanVien(NhanVien loggedInNhanVien) {
        this.loggedInNhanVien = loggedInNhanVien;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
