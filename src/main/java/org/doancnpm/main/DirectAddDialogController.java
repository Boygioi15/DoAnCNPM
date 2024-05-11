package org.doancnpm.main;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.controlsfx.control.SearchableComboBox;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.Models.DaiLy;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DirectAddDialogController implements Initializable {

    @FXML private SearchableComboBox<String> quanComboBox;
    @FXML private SearchableComboBox<String> loaiDaiLyComboBox;
    @FXML private TextField tenDaiLyTextField;
    @FXML private TextField diaChiTextField;
    @FXML private TextField dienThoaiTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField ghiChuTextField;

    @FXML private Button thoatButton;
    @FXML private Button themButton;

    //validators
    BooleanProperty notSelectedQuan = new SimpleBooleanProperty(false);
    BooleanProperty notSelectedLoaiDaiLy = new SimpleBooleanProperty(false);
    BooleanProperty emptyTenDaily = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initAction();
        initValidator();
    }
    public void initAction(){
        //thoatButton.setOnAction();
        themButton.setOnAction(ob -> add());
    }

    //validator
    private void initValidator(){
        notSelectedQuan.bind(Bindings.createBooleanBinding(this::checkQuanSelected,quanComboBox.valueProperty()));
        notSelectedLoaiDaiLy.bind(Bindings.createBooleanBinding(this::checkLoaiDaiLySelected,loaiDaiLyComboBox.valueProperty()));
        emptyTenDaily.bind(Bindings.createBooleanBinding(this::checkTenDaiLyEmpty,loaiDaiLyComboBox.valueProperty()));
        themButton.disableProperty().bind(
                Bindings.createBooleanBinding(this::allowAdding,
                        notSelectedQuan,
                        notSelectedLoaiDaiLy,
                        emptyTenDaily
                )
        );
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
    public void add(){
        Date ngayTiepNhan = new Date(System.currentTimeMillis());
        DaiLy toAdd = new DaiLy();

        toAdd.setMaQuan(Integer.parseInt(quanComboBox.getValue()));
        toAdd.setMaLoaiDaiLy(Integer.parseInt(loaiDaiLyComboBox.getValue()));

        toAdd.setTenDaiLy(tenDaiLyTextField.getText());
        toAdd.setDiaChi(diaChiTextField.getText());
        toAdd.setEmail(emailTextField.getText());
        toAdd.setDienThoai(dienThoaiTextField.getText());
        toAdd.setNgayTiepNhan( ngayTiepNhan);
        toAdd.setGhiChu( ghiChuTextField.getText());
        try{
            DaiLyDAO.getInstance().Insert(toAdd);
            popSucessDialog();
        }
        catch(SQLException e){
            popErrorDialog(e.getMessage());
        }
    }
    private void popSucessDialog(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
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
