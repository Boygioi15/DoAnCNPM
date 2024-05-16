package org.doancnpm.Ultilities;

import org.doancnpm.Models.NhanVien;

public class CurrentNVInfor {
    private static CurrentNVInfor instance;
    private NhanVien loggedInNhanVien;

    private CurrentNVInfor() {}

    public static synchronized CurrentNVInfor getInstance() {
        if (instance == null) {
            instance = new CurrentNVInfor();
        }
        return instance;
    }

    public NhanVien getLoggedInNhanVien() {
        return loggedInNhanVien;
    }

    public void setLoggedInNhanVien(NhanVien loggedInNhanVien) {
        this.loggedInNhanVien = loggedInNhanVien;
    }
}
