package org.doancnpm.main;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;
import org.doancnpm.DAO.LoaiDaiLyDAO;
import org.doancnpm.DAO.QuanDAO;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.LoaiDaiLy;
import org.doancnpm.Models.Quan;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DirectAddDialogController implements Initializable {

    @FXML private SearchableComboBox<Quan> quanComboBox;
    @FXML private SearchableComboBox<LoaiDaiLy> loaiDaiLyComboBox;
    @FXML private TextField tenDaiLyTextField;
    @FXML private TextField diaChiTextField;
    @FXML private TextField dienThoaiTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField ghiChuTextField;


    //validators
    BooleanProperty notSelectedQuan = new SimpleBooleanProperty(false);
    BooleanProperty notSelectedLoaiDaiLy = new SimpleBooleanProperty(false);
    BooleanProperty emptyTenDaily = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initAction();
        initValidator();
        displayDataInCb();
    }
    public void initAction(){
        //thoatButton.setOnAction();
        //themButton.setOnAction(ob -> add());
    }
    public void setInitialValue(DaiLy daiLy){
        if(daiLy==null){
            return;
        }
        try {
            Quan queriedQuan = QuanDAO.getInstance().QueryID(daiLy.getMaQuan());
            quanComboBox.setValue(queriedQuan);
        } catch (SQLException e) {
            System.out.println("Error in setting quan value for combobox");
        }
        try {
            LoaiDaiLy queriedLoaiDaiLy = LoaiDaiLyDAO.getInstance().QueryID(daiLy.getMaLoaiDaiLy());
            loaiDaiLyComboBox.setValue(queriedLoaiDaiLy);
        } catch (SQLException e) {
            System.out.println("Error in setting quan value for combobox");
        }

        tenDaiLyTextField.setText(daiLy.getTenDaiLy());
        diaChiTextField.setText(daiLy.getDiaChi());
        dienThoaiTextField.setText(daiLy.getDienThoai());
        emailTextField.setText(daiLy.getEmail());
        ghiChuTextField.setText(daiLy.getGhiChu());
    }
    //validator
    private void initValidator(){
        notSelectedQuan.bind(Bindings.createBooleanBinding(this::checkQuanSelected,quanComboBox.valueProperty()));
        notSelectedLoaiDaiLy.bind(Bindings.createBooleanBinding(this::checkLoaiDaiLySelected,loaiDaiLyComboBox.valueProperty()));
        emptyTenDaily.bind(Bindings.createBooleanBinding(this::checkTenDaiLyEmpty,loaiDaiLyComboBox.valueProperty()));
        /*
        themButton.disableProperty().bind(
                Bindings.createBooleanBinding(this::allowAdding,
                        notSelectedQuan,
                        notSelectedLoaiDaiLy,
                        emptyTenDaily
                )
        );
        */
    }
    private void displayDataInCb() {
        try {
            ObservableList<Quan> quans = FXCollections.observableArrayList(QuanDAO.getInstance().QueryAll());
            ObservableList<LoaiDaiLy> loaiDaiLys = FXCollections.observableArrayList(LoaiDaiLyDAO.getInstance().QueryAll());

            // Sử dụng StringConverter để hiển thị dữ liệu trong ComboBox
            StringConverter<Quan> quanStringConverter = new StringConverter<Quan>() {
                @Override
                public String toString(Quan quan) {
                    return quan == null ? null : quan.getTenQuan();
                }

                @Override
                public Quan fromString(String string) {
                    return null; // Bạn có thể cần triển khai nếu cần
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
        } catch (Exception e) {
            // Xử lý ngoại lệ nếu cần
        }
    }

    private boolean allowAdding(){
        return notSelectedQuan.getValue()&&notSelectedLoaiDaiLy.getValue()&&emptyTenDaily.getValue();
    }
    private boolean checkQuanSelected(){
        return quanComboBox.getValue()==null;
    }
    private boolean checkLoaiDaiLySelected(){
        return loaiDaiLyComboBox.getValue()==null;
    }
    private boolean checkTenDaiLyEmpty(){
        return tenDaiLyTextField.getText().isEmpty();
    }
    public void exit(){

    }
    public DaiLy getDaiLy(){
        Date ngayTiepNhan = new Date(System.currentTimeMillis());
        DaiLy daiLy = new DaiLy();
        try{
            daiLy.setMaQuan(quanComboBox.getValue().getId());
            daiLy.setMaLoaiDaiLy(loaiDaiLyComboBox.getValue().getId());

            daiLy.setTenDaiLy(tenDaiLyTextField.getText());
            daiLy.setDiaChi(diaChiTextField.getText());
            daiLy.setEmail(emailTextField.getText());
            daiLy.setDienThoai(dienThoaiTextField.getText());
            daiLy.setNgayTiepNhan( ngayTiepNhan);
            daiLy.setGhiChu( ghiChuTextField.getText());
        }
        catch(NumberFormatException e){
            System.out.println("Quan not set!");
            return null;
        }
        return daiLy;
    }
    private void popSucessDialog(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thêm mới đại lý thành công!");
        alert.showAndWait();
    }
    private void popErrorDialog(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Không thể thêm đại lý mới vào!");
        alert.setContentText("Lỗi" + message);
        alert.showAndWait();
    }
    private void initValidation(){
        MFXTextField tf = new MFXTextField();
        tf.updateInvalid(tf,true);
    }
}
