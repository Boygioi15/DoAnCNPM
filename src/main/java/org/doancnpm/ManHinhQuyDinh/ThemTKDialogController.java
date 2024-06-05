package org.doancnpm.ManHinhQuyDinh;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.Quan;
import org.doancnpm.Models.TaiKhoan;
import org.doancnpm.Ultilities.PopDialog;
import org.doancnpm.Ultilities.SHA256;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ThemTKDialogController implements Initializable {

    @FXML private SearchableComboBox<NhanVien> nhanVien;
    @FXML private TextField username;
    @FXML private TextField password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayDataInCb();
    }
    private void displayDataInCb() {
        try {
            ObservableList<NhanVien> nhanViens = FXCollections.observableArrayList(NhanVienDAO.getInstance().QueryAll());

            // Sử dụng StringConverter để hiển thị dữ liệu trong ComboBox
            StringConverter<NhanVien> nhanVienStringConverter = new StringConverter<NhanVien>() {
                @Override
                public String toString(NhanVien nhanVien) {
                    return nhanVien == null ? null : nhanVien.getMaNhanVien()+ " - " + nhanVien.getHoTen(); //lên hình
                }

                @Override
                public NhanVien fromString(String string) {
                    return null; //Từ hình xuống data
                }
            };

            // Đặt StringConverter cho ComboBox
            nhanVien.setConverter(nhanVienStringConverter);

            // Đặt DataSource cho ComboBox
            nhanVien.setItems(nhanViens);
        }
        catch (SQLException e) {
            PopDialog.popErrorDialog("Lấy dữ liệu nhân viên thất bại",e.toString());
        }
    }
    public TaiKhoan getTaiKhoan(){
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setUserName(username.getText());
        taiKhoan.setPassword(SHA256.getSHA256Hash(password.getText()));
        taiKhoan.setMaNhanVien(nhanVien.getValue().getID());
        return taiKhoan;
    }
}
