package org.doancnpm.ManHinhNhanVien;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;
import org.doancnpm.DAO.ChucVuDAO;
import org.doancnpm.DAO.LoaiDaiLyDAO;
import org.doancnpm.DAO.QuanDAO;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.MoneyFormatter;
import org.doancnpm.Ultilities.PopDialog;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TiepNhanNhanVienDialogController implements Initializable {

    @FXML private SearchableComboBox<ChucVu> chucVuComboBox;
    @FXML private SearchableComboBox<String> gioiTinhComboBox;
    @FXML private TextField hoTenTextField;
    @FXML private DatePicker ngaySinhDatePicker;
    @FXML private TextField sdtTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField luongTextField;
    @FXML private TextArea ghiChuTextArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initAction();
        displayDataInCb();
        MoneyFormatter.MoneyFormatTextField(luongTextField);
    }
    public void initAction(){
        //thoatButton.setOnAction();
        //themButton.setOnAction(ob -> add());
    }

    public void setInitialValue(NhanVien nhanVien){
        if(nhanVien ==null){
            return;
        }
        try {
            ChucVu queriedChucVu = ChucVuDAO.getInstance().QueryID(nhanVien.getMaChucVu());
            chucVuComboBox.setValue(queriedChucVu);
        } catch (SQLException _) {}

        hoTenTextField.setText(nhanVien.getHoTen());
        ngaySinhDatePicker.setValue((nhanVien.getNgaySinh().toLocalDate()));
        sdtTextField.setText(nhanVien.getSDT());
        emailTextField.setText(nhanVien.getEmail());
        luongTextField.setText(MoneyFormatter.convertLongToString(nhanVien.getLuong()));
        ghiChuTextArea.setText(nhanVien.getGhiChu());
        gioiTinhComboBox.setValue(nhanVien.getGioiTinh());
    }
    private void displayDataInCb() {
        try {
            ObservableList<ChucVu> chucVus = FXCollections.observableArrayList(ChucVuDAO.getInstance().QueryAll());

            // Sử dụng StringConverter để hiển thị dữ liệu trong ComboBox
            StringConverter<ChucVu> quanStringConverter = new StringConverter<ChucVu>() {
                @Override
                public String toString(ChucVu chucVu) {
                    return chucVu == null ? null : chucVu.getTenCV(); //lên hình
                }

                @Override
                public ChucVu fromString(String string) {
                    return null; //Từ hình xuống data
                }
            };
            // Đặt StringConverter cho ComboBox
            chucVuComboBox.setConverter(quanStringConverter);
            // Đặt DataSource cho ComboBox
            chucVuComboBox.setItems(chucVus);

            String[] gioiTinhs = {"Nam","Nữ"};
            gioiTinhComboBox.getItems().addAll(gioiTinhs);
        }
        catch (SQLException e) {
            PopDialog.popErrorDialog("Lấy dữ liệu chức vụ thất bại",e.getMessage());
        }
    }

    public NhanVien getNhanVien(){
        NhanVien nhanVien = new NhanVien();

        nhanVien.setHoTen(hoTenTextField.getText());
        nhanVien.setNgaySinh(Date.valueOf(ngaySinhDatePicker.getValue()));
        nhanVien.setEmail(emailTextField.getText());
        nhanVien.setSDT(sdtTextField.getText());
        nhanVien.setGioiTinh(gioiTinhComboBox.getValue());
        nhanVien.setMaChucVu(chucVuComboBox.getValue().getId());
        nhanVien.setLuong(MoneyFormatter.getLongValueFromTextField(luongTextField));
        nhanVien.setGhiChu( ghiChuTextArea.getText());
        return nhanVien;
    }

    public String GetValid() {
        if (chucVuComboBox.getValue() == null) {
            return "Chức vụ không được để trống";
        }
        if (gioiTinhComboBox.getValue() == null) {
            return "Giới tính không được để trống";
        }
        if (emailTextField.getText().isEmpty()) {
            return "Email không được để trống";
        }
        if (luongTextField.getText().isEmpty()) {
            return "Lương không được để trống";
        }
        if (!isValidEmailFormat(emailTextField.getText().trim())) {
            return "Email không đúng định dạng";
        }
        return "";
    }
    private boolean isValidEmailFormat(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
