package org.doancnpm.Ultilities;


import org.doancnpm.DAO.*;
import org.doancnpm.Models.*;

import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;

public class CheckExist {
    public static String normalize(String text) {
        if (text == null) {
            return null;
        }

        // Chuẩn hóa tên bằng cách loại bỏ dấu và chuyển thành chữ thường, đồng thời loại bỏ khoảng trắng thừa
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();

        // Thay thế chữ 'đ' bằng 'd'
        normalized = normalized.replace('đ', 'd');

        return normalized;
    }
    public static boolean checkQuan(String quan) throws SQLException {
        ArrayList<Quan> allQuan = QuanDAO.getInstance().QueryAll();
        String normalizedQuan = normalize(quan);

        // Duyệt qua danh sách tất cả các Quận và kiểm tra xem Quận đã tồn tại hay không
        for (Quan q : allQuan) {
            // Chuẩn hóa tên Quận từ danh sách (loại bỏ dấu và chuyển thành chữ thường)
            String normalizedQuanName = normalize(q.getTenQuan());

            if (normalizedQuanName.equals(normalizedQuan)) {
                return true;
            }
        }

        return false;
    }
    public static boolean checkLoaiDaiLy(String loaiDaiLy) throws SQLException {
        ArrayList<LoaiDaiLy> allLoaiDaiLy = LoaiDaiLyDAO.getInstance().QueryAll();

        String normalizedLoaiDaiLy = normalize(loaiDaiLy);

        for (LoaiDaiLy ld : allLoaiDaiLy) {
            // Chuẩn hóa tên Loại đại lý từ danh sách (loại bỏ dấu và chuyển thành chữ thường)
            String normalizedLoaiDaiLyName = normalize(ld.getTenLoai());

            if (normalizedLoaiDaiLyName.equals(normalizedLoaiDaiLy)) {
                return true;
            }
        }
        return false;
    }
    public static boolean checkDVT(String dvt) throws SQLException {
        ArrayList<DonViTinh> allDVTs = DonViTinhDAO.getInstance().QueryAll();

        String normalizedDVT = normalize(dvt);

        for (DonViTinh d : allDVTs) {
            // Chuẩn hóa tên Đơn vị tính từ danh sách (loại bỏ dấu và chuyển thành chữ thường)
            String normalizedDVTName = normalize(d.getTenDVT());

            if (normalizedDVTName.equals(normalizedDVT)) {
                return true;
            }
        }
        return false;
    }
    public static boolean checkChucVu(String chucVuName) throws SQLException {
        ArrayList<ChucVu> allChucVu = ChucVuDAO.getInstance().QueryAll();

        String normalizedChucVu = normalize(chucVuName);

        for (ChucVu cv : allChucVu) {
            String normalizedChucVuName = normalize(cv.getTenCV());

            if (normalizedChucVuName.equals(normalizedChucVu)) {
                return true;
            }
        }
        return false;
    }
    public static boolean checkDaiLy(String daiLyName) throws SQLException {
        // Fetch all DaiLy objects
        ArrayList<DaiLy> allDaiLy = DaiLyDAO.getInstance().QueryAll();

        // Normalize the input DaiLy name
        String normalizedDaiLy = normalize(daiLyName);

        // Iterate through the list of all DaiLy objects and check if the name exists
        for (DaiLy dl : allDaiLy) {
            // Normalize the DaiLy name from the list
            String normalizedDaiLyName = normalize(dl.getTenDaiLy());

            if (normalizedDaiLyName.equals(normalizedDaiLy)) {
                return true;
            }
        }
        return false;
    }


}
