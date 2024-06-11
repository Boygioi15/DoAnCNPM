package org.doancnpm.ManHinhKhoHang;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;
import org.doancnpm.DAO.DonViTinhDAO;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.MoneyFormatter;
import org.doancnpm.Ultilities.PopDialog;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ThemMoiMatHangDialogController implements Initializable {

    @FXML private TextField tenMHTextField;
    @FXML private SearchableComboBox<DonViTinh> dvtComboBox;
    @FXML private TextField donGiaNhapTextField;
    @FXML private TextArea ghiChuTextArea;
    @FXML
    Label title;
    MatHang initialValue;


    public String getValidData() {
        if (tenMHTextField.getText().isEmpty()) {
            return "Tên mặt hàng không được để trống";
        }
        if (dvtComboBox.getValue() == null) {
            return "Đơn vị tính không được để trống";
        }
        if (donGiaNhapTextField.getText().isEmpty()) {
            return "Đơn giá nhập không được để trống";
        }
            return "";
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayDataInCb();
        MoneyFormatter.MoneyFormatTextField(donGiaNhapTextField);
    }

    public void setInitialValue(MatHang matHang){
        if(matHang==null){
            return;
        }
        title.setText("Cập nhật mặt hàng");
        initialValue = matHang;
        try {
            DonViTinh donViTinh = DonViTinhDAO.getInstance().QueryID(matHang.getMaDVT());
            dvtComboBox.setValue(donViTinh);
        } catch (SQLException e) {
            PopDialog.popErrorDialog("Không thể lấy dữ liệu đơn vị tính");
        }
        tenMHTextField.setText(matHang.getTenMatHang());
        donGiaNhapTextField.setText(MoneyFormatter.convertLongToString(matHang.getDonGiaNhap()));
        ghiChuTextArea.setText(matHang.getGhiChu());
    }
    //validator
    private void initValidator(){

    }
    private void displayDataInCb() {
        try {
            ObservableList<DonViTinh> donViTinhs = FXCollections.observableArrayList(DonViTinhDAO.getInstance().QueryAll());

            // Sử dụng StringConverter để hiển thị dữ liệu trong ComboBox
            StringConverter<DonViTinh> quanStringConverter = new StringConverter<DonViTinh>() {
                @Override
                public String toString(DonViTinh donViTinh) {
                    return donViTinh == null ? null : donViTinh.getTenDVT(); //lên hình
                }

                @Override
                public DonViTinh fromString(String string) {
                    return null; //Từ hình xuống data
                }
            };

            // Đặt StringConverter cho ComboBox
            dvtComboBox.setConverter(quanStringConverter);

            // Đặt DataSource cho ComboBox
            dvtComboBox.setItems(donViTinhs);
        }
        catch (SQLException e) {
            PopDialog.popErrorDialog("Lấy dữ liệu các quận/ loại đại lý thất bại",e.toString());
        }
    }

    public MatHang getMatHang(){
        MatHang matHang = new MatHang();
        if(initialValue!=null){
            matHang.setID(initialValue.getID());
            matHang.setMaMatHang(initialValue.getMaMatHang());
        }
        matHang.setTenMatHang(tenMHTextField.getText());
        matHang.setDonGiaNhap(MoneyFormatter.getLongValueFromTextField(donGiaNhapTextField));
        matHang.setMaDVT(dvtComboBox.getValue().getId());
        matHang.setGhiChu( ghiChuTextArea.getText());
        return matHang;
    }


}
