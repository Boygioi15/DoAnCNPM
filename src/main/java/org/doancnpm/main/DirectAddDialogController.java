package org.doancnpm.main;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initAction();
    }
    public void initAction(){
        //thoatButton.setOnAction();
        themButton.setOnAction(ob -> add());
    }
    public void exit(){

    }
    public void add(){
        Date ngayTiepNhan = new Date(System.currentTimeMillis());
        DaiLy toAdd = new DaiLy();

        toAdd.setMaQuan(quanComboBox.getValue());
        toAdd.setMaLoaiDaiLy(loaiDaiLyComboBox.getValue());

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
}
