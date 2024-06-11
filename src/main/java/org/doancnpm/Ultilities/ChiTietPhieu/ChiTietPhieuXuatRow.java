package org.doancnpm.Ultilities.ChiTietPhieu;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;
import org.doancnpm.DAO.DonViTinhDAO;
import org.doancnpm.DAO.MatHangDAO;
import org.doancnpm.Models.ChiTietPhieuXuat;
import org.doancnpm.Models.DonViTinh;
import org.doancnpm.Models.MatHang;
import org.doancnpm.Ultilities.MoneyFormatter;
import org.doancnpm.Ultilities.PopDialog;

import java.io.IOException;
import java.sql.SQLException;

public class ChiTietPhieuXuatRow extends HBox {
    @FXML SearchableComboBox<MatHang> mhComboBox;
    @FXML TextField dvtTextField;
    @FXML TextField slTextField;
    @FXML TextField donGiaTextField;
    @FXML public TextField thanhTienTextField;

    @FXML Button xoaBtn;
    VBox container;
    public ChiTietPhieuXuatRow(VBox container) {
        this.container = container;
        loadFXML();
        initMHComboBox();
        initBinding();
        MoneyFormatter.MoneyFormatTextField(thanhTienTextField);
    }
    public Long getThanhTien(){

        try{
            Long result = MoneyFormatter.getLongValueFromTextField(thanhTienTextField);;
            return  result;
        }catch (Exception e){
            return 0L;
        }

    }
    public void SetOnXoa(EventHandler<ActionEvent> eventHandle){
        xoaBtn.setOnAction(eventHandle);
    }
    private void initMHComboBox() {
        try {
            ObservableList<MatHang> matHangs = FXCollections.observableArrayList(MatHangDAO.getInstance().QueryAll());

            // Sử dụng StringConverter để hiển thị dữ liệu trong ComboBox
            StringConverter<MatHang> matHangStringConverter = new StringConverter<MatHang>() {
                @Override
                public String toString(MatHang mh) {
                    return mh == null ? null : mh.getMaMatHang()+" - " + mh.getTenMatHang(); //lên hình
                }

                @Override
                public MatHang fromString(String string) {
                    return null; //Từ hình xuống data
                }
            };

            // Đặt StringConverter cho ComboBox
            mhComboBox.setConverter(matHangStringConverter);

            // Đặt DataSource cho ComboBox
            mhComboBox.setItems(matHangs);
        }
        catch (SQLException e) {
            PopDialog.popErrorDialog("Lấy dữ liệu các quận/ loại đại lý thất bại",e.toString());
        }
    }
    private void loadFXML() {
        try {
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/Ultilities/ChiTietPhieuNhapRowUI.fxml"));
            mainLoader.setRoot(this);
            mainLoader.setController(this);
            mainLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    public void loadFromChiTietPhieuXuat(ChiTietPhieuXuat cptx){
        try {
            MatHang matHang = MatHangDAO.getInstance().QueryID(cptx.getMaMatHang());
            mhComboBox.setValue(matHang);
            DonViTinh dvt = DonViTinhDAO.getInstance().QueryID(matHang.getMaDVT());
            dvtTextField.setText(dvt.getTenDVT());
            slTextField.setText(Integer.toString(cptx.getSoLuong()));
            donGiaTextField.setText(MoneyFormatter.convertLongToString(matHang.getDonGiaXuat()));
            thanhTienTextField.setText(Double.toString(cptx.getThanhTien()));
        } catch (SQLException _) {}


    }
    private void initBinding(){
        slTextField.setDisable(true);
        dvtTextField.setDisable(true);
        donGiaTextField.setDisable(true);
        thanhTienTextField.setDisable(true);
        mhComboBox.valueProperty().addListener(ob -> {
            if(mhComboBox.getValue()==null){
                slTextField.clear();
                slTextField.setDisable(true);
                return;
            }
            try {
                DonViTinh dvt = DonViTinhDAO.getInstance().QueryID(mhComboBox.getValue().getMaDVT());
                dvtTextField.setText(dvt.getTenDVT());
                donGiaTextField.setText(MoneyFormatter.convertLongToString(mhComboBox.getValue().getDonGiaXuat()));
            }catch (SQLException _) {}

            slTextField.setDisable(false);
        });
        slTextField.textProperty().addListener(ob -> {
            try{
                Integer sl = Integer.parseInt(slTextField.getText());
                Long thanhTien = sl*mhComboBox.getValue().getDonGiaXuat();
                thanhTienTextField.setText(MoneyFormatter.convertLongToString(thanhTien));
            }catch (Exception e){}
        });
    }
    public ChiTietPhieuXuat getChiTietPhieuXuat(){
        ChiTietPhieuXuat chiTietPhieuXuat = new ChiTietPhieuXuat();
        chiTietPhieuXuat.setMaMatHang(mhComboBox.getValue().getID());
        chiTietPhieuXuat.setSoLuong(Integer.parseInt(slTextField.getText()));
        chiTietPhieuXuat.setDonGiaXuat(mhComboBox.getValue().getDonGiaXuat());
        return chiTietPhieuXuat;
    }

    public String GetValid(){
        Boolean emptyMh = mhComboBox.getValue()==null;
        Boolean emptySL = slTextField.getText().isEmpty();
        Boolean saiSL = false;
        try{
            Long.parseLong(slTextField.getText());
        }
        catch (Exception _){saiSL = true;}
        String validate = "";
        if(emptyMh){
            validate = validate.concat("Mặt hàng không được để trống. ");
        }
        if(emptySL){
            validate = validate.concat("Số lượng không được để trống. ");
        }
        if(!emptySL && saiSL){
            validate = validate.concat("Định dạng số lượng sai. ");
        }
        return  validate;
    }
}