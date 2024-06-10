package org.doancnpm.ManHinhPhieuThu;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.DAO.LoaiDaiLyDAO;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.DAO.QuanDAO;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.MoneyFormatter;
import org.doancnpm.Ultilities.PopDialog;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.doancnpm.Ultilities.MoneyFormatter.getLongValueFromTextField;

public class LapPhieuThuDialogController implements Initializable {
    @FXML private TextField ngayLapPhieuTextField;
    @FXML private TextField nhanVienTextField;
    @FXML private SearchableComboBox<DaiLy> dlComboBox;
    @FXML private TextArea ghiChuTextArea;
    @FXML private TextField soTienThuTextField;

    NhanVien nvLapPhieu = null;
    PhieuThu phieuThuGoc;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initDaiLyComboBox();

        MoneyFormatter.MoneyFormatTextField(soTienThuTextField);
    }

    private void initDaiLyComboBox(){
        try {
            ObservableList<DaiLy> daiLy = FXCollections.observableArrayList(DaiLyDAO.getInstance().QueryAll());

            // Sử dụng StringConverter để hiển thị dữ liệu trong ComboBox
            StringConverter<DaiLy> quanStringConverter = new StringConverter<DaiLy>() {
                @Override
                public String toString(DaiLy dl) {
                    return dl == null ? null : (dl.getMaDaiLy()+"-"+dl.getTenDaiLy()); //lên hình
                }
                @Override
                public DaiLy fromString(String string) {
                    return null; //Từ hình xuống data
                }
            };
            // Đặt StringConverter cho ComboBox
            dlComboBox.setConverter(quanStringConverter);

            // Đặt DataSource cho ComboBox
            dlComboBox.setItems(daiLy);
        }
        catch (SQLException e) {
            PopDialog.popErrorDialog("Lấy dữ liệu đại lý thất bại",e.toString());
        }
    }
    public void setInitialValue(PhieuThu phieuThu, NhanVien nvLoggedIn) {
        if(phieuThu==null){
            nhanVienTextField.setText(nvLoggedIn.getHoTen());
            this.nvLapPhieu = nvLoggedIn;

            Date ngayTiepNhan = new Date(System.currentTimeMillis());
            ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(ngayTiepNhan));
            return;
        }
        phieuThuGoc = phieuThu;
        ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(phieuThu.getNgayLap()));
        try{
            NhanVien nv = NhanVienDAO.getInstance().QueryID(phieuThu.getMaNhanVien());
            nhanVienTextField.setText(nv.getHoTen());

            DaiLy dl = DaiLyDAO.getInstance().QueryID(phieuThu.getMaDaiLy());
            dlComboBox.setValue(dl);
        }catch (SQLException _){}

        ghiChuTextArea.setText(phieuThu.getGhiChu());
        soTienThuTextField.setText(Long.toString(phieuThu.getSoTienThu()));

        ngayLapPhieuTextField.setDisable(true);
        nhanVienTextField.setDisable(true);
        dlComboBox.setDisable(true);
        soTienThuTextField.setDisable(true);
    }
    public PhieuThu getPhieuThu(){
        if(phieuThuGoc==null){
            Date ngayTiepNhan = new Date(System.currentTimeMillis());

            PhieuThu phieuThu = new PhieuThu();
            try{
                phieuThu.setMaNhanVien(nvLapPhieu.getID());
                if(dlComboBox.getValue()==null){
                    PopDialog.popErrorDialog("Chưa chọn đại lý!");
                    return null;
                }
                phieuThu.setMaDaiLy(dlComboBox.getValue().getID());
                phieuThu.setGhiChu(ghiChuTextArea.getText());
                phieuThu.setSoTienThu(getLongValueFromTextField(soTienThuTextField));
                phieuThu.setNgayLap(ngayTiepNhan);
                phieuThu.setGhiChu(ghiChuTextArea.getText());
            }
            catch(NumberFormatException e){
                PopDialog.popErrorDialog("Định dạng số tiền thu không đúng");
                return null;
            }
            return phieuThu;
        }
        else{
            phieuThuGoc.setGhiChu(ghiChuTextArea.getText());
            return phieuThuGoc;
        }
    }
}
