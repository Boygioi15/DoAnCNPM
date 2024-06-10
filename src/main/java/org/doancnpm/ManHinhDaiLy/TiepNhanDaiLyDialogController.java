package org.doancnpm.ManHinhDaiLy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class TiepNhanDaiLyDialogController implements Initializable {

    @FXML private SearchableComboBox<Quan> quanComboBox;
    @FXML private SearchableComboBox<LoaiDaiLy> loaiDaiLyComboBox;
    @FXML private TextField tenDaiLyTextField;
    @FXML private TextField diaChiTextField;
    @FXML private TextField dienThoaiTextField;
    @FXML private TextField emailTextField;
    @FXML private TextArea ghiChuTextArea;

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
            quanComboBox.setValue(queriedQuan);

            LoaiDaiLy queriedLoaiDaiLy = LoaiDaiLyDAO.getInstance().QueryID(daiLy.getMaLoaiDaiLy());
            loaiDaiLyComboBox.setValue(queriedLoaiDaiLy);
        } catch (SQLException _) { }

        tenDaiLyTextField.setText(daiLy.getTenDaiLy());
        diaChiTextField.setText(daiLy.getDiaChi());
        dienThoaiTextField.setText(daiLy.getDienThoai());
        emailTextField.setText(daiLy.getEmail());
        ghiChuTextArea.setText(daiLy.getGhiChu());
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
            quanComboBox.setConverter(quanStringConverter);
            loaiDaiLyComboBox.setConverter(loaiDaiLyStringConverter);

            // Đặt DataSource cho ComboBox
            quanComboBox.setItems(quans);
            loaiDaiLyComboBox.setItems(loaiDaiLys);
        }
        catch (SQLException e) {
            PopDialog.popErrorDialog("Lấy dữ liệu các quận/ loại đại lý thất bại",e.toString());
        }
    }
    public DaiLy getDaiLy(){
        if(initialValue==null){
            Date ngayTiepNhan = new Date(System.currentTimeMillis());
            DaiLy daiLy = new DaiLy();
            daiLy.setMaQuan(quanComboBox.getValue().getId());
            daiLy.setMaLoaiDaiLy(loaiDaiLyComboBox.getValue().getId());

            daiLy.setTenDaiLy(tenDaiLyTextField.getText());
            daiLy.setDiaChi(diaChiTextField.getText());
            daiLy.setEmail(emailTextField.getText());
            daiLy.setDienThoai(dienThoaiTextField.getText());
            daiLy.setNgayTiepNhan(ngayTiepNhan);
            daiLy.setGhiChu( ghiChuTextArea.getText());
            return daiLy;

        }
        else{
            initialValue.setMaQuan(quanComboBox.getValue().getId());
            initialValue.setMaLoaiDaiLy(loaiDaiLyComboBox.getValue().getId());

            initialValue.setTenDaiLy(tenDaiLyTextField.getText());
            initialValue.setDiaChi(diaChiTextField.getText());
            initialValue.setEmail(emailTextField.getText());
            initialValue.setDienThoai(dienThoaiTextField.getText());
            initialValue.setGhiChu( ghiChuTextArea.getText());
            return initialValue;
        }
    }
}
