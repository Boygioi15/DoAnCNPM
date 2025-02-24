package org.doancnpm.ManHinhDaiLy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;
import org.doancnpm.DAO.LoaiDaiLyDAO;
import org.doancnpm.DAO.QuanDAO;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.LoaiDaiLy;
import org.doancnpm.Models.Quan;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.PopDialog;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TiepNhanDaiLyDialogController implements Initializable {

    @FXML
    SearchableComboBox<Quan> quanComboBox_add;
    @FXML
    SearchableComboBox<LoaiDaiLy> loaiDaiLyComboBox_add;
    @FXML
    TextField tenDaiLyTextField_add;
    @FXML
    TextField diaChiTextField;
    @FXML
    TextField dienThoaiTextField;
    @FXML
    TextField emailTextField;
    @FXML
    TextArea ghiChuTextArea_add;
    @FXML
    Label title;

    DaiLy initialValue = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayDataInCb();
    }

    public void setInitialValue(DaiLy daiLy){
        if(daiLy==null){
            return;
        }
        initialValue = daiLy;
        try {
            Quan queriedQuan = QuanDAO.getInstance().QueryID(daiLy.getMaQuan());
            quanComboBox_add.setValue(queriedQuan);

            LoaiDaiLy queriedLoaiDaiLy = LoaiDaiLyDAO.getInstance().QueryID(daiLy.getMaLoaiDaiLy());
            loaiDaiLyComboBox_add.setValue(queriedLoaiDaiLy);
        } catch (SQLException _) {
        }

        tenDaiLyTextField_add.setText(daiLy.getTenDaiLy());
        diaChiTextField.setText(daiLy.getDiaChi());
        dienThoaiTextField.setText(daiLy.getDienThoai());
        emailTextField.setText(daiLy.getEmail());
        ghiChuTextArea_add.setText(daiLy.getGhiChu());
        title.setText("Cập nhật đại lý");
    }

    private void displayDataInCb() {
        try {
            ObservableList<Quan> quans = FXCollections.observableArrayList(QuanDAO.getInstance().QueryAll());
            ObservableList<LoaiDaiLy> loaiDaiLys = FXCollections.observableArrayList(LoaiDaiLyDAO.getInstance().QueryAll());

            // Sử dụng StringConverter để hiển thị dữ liệu trong ComboBox
            StringConverter<Quan> quanStringConverter = new StringConverter<Quan>() {
                @Override
                public String toString(Quan quan) {
                    return quan == null ? null : quan.getTenQuan(); //lên hình
                }

                @Override
                public Quan fromString(String string) {
                    return null; //Từ hình xuống data
                }
            };

            StringConverter<LoaiDaiLy> loaiDaiLyStringConverter = new StringConverter<LoaiDaiLy>() {
                @Override
                public String toString(LoaiDaiLy loaiDaiLy) {
                    return loaiDaiLy == null ? null : loaiDaiLy.getTenLoai();
                }

                @Override
                public LoaiDaiLy fromString(String string) {
                    return null; // Bạn có thể cần triển khai nếu cần
                }
            };

            // Đặt StringConverter cho ComboBox
            quanComboBox_add.setConverter(quanStringConverter);
            loaiDaiLyComboBox_add.setConverter(loaiDaiLyStringConverter);

            // Đặt DataSource cho ComboBox
            quanComboBox_add.setItems(quans);
            loaiDaiLyComboBox_add.setItems(loaiDaiLys);
        } catch (SQLException e) {
            PopDialog.popErrorDialog("Lấy dữ liệu các quận/ loại đại lý thất bại", e.toString());
        }
    }

    public DaiLy getDaiLy() {
        if (initialValue == null) {
            Date ngayTiepNhan = new Date(System.currentTimeMillis());
            DaiLy daiLy = new DaiLy();

            daiLy.setMaQuan(quanComboBox_add.getValue().getId());


            daiLy.setMaLoaiDaiLy(loaiDaiLyComboBox_add.getValue().getId());

            daiLy.setTenDaiLy(tenDaiLyTextField_add.getText());
            daiLy.setDiaChi(diaChiTextField.getText());
            daiLy.setEmail(emailTextField.getText());
            daiLy.setDienThoai(dienThoaiTextField.getText());
            daiLy.setNgayTiepNhan(ngayTiepNhan);
            daiLy.setGhiChu(ghiChuTextArea_add.getText());
            return daiLy;

        } else {
            initialValue.setMaQuan(quanComboBox_add.getValue().getId());
            initialValue.setMaLoaiDaiLy(loaiDaiLyComboBox_add.getValue().getId());

            initialValue.setTenDaiLy(tenDaiLyTextField_add.getText());
            initialValue.setDiaChi(diaChiTextField.getText());
            initialValue.setEmail(emailTextField.getText());
            initialValue.setDienThoai(dienThoaiTextField.getText());
            initialValue.setGhiChu(ghiChuTextArea_add.getText());
            return initialValue;
        }
    }

    public String getValidateData() {
        if (quanComboBox_add.getValue() == null) {
            return "Quận không được để trống";
        }
        if (loaiDaiLyComboBox_add.getValue() == null) {
            return "Loại đại lý không được để trống";
        }
        if (diaChiTextField.getText().isEmpty()) {
            return "Địa chỉ không được để trống";
        }
        if (tenDaiLyTextField_add.getText().isEmpty()) {
            return "Tên đại lý không được để trống";
        }
        if (!isValidEmailFormat(emailTextField.getText().trim())) {
            return "Email không đúng định dạng";
        }

        return "";
    }

    boolean isValidEmailFormat(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
