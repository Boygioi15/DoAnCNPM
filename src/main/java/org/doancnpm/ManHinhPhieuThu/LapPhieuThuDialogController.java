package org.doancnpm.ManHinhPhieuThu;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.doancnpm.DAO.LoaiDaiLyDAO;
import org.doancnpm.DAO.QuanDAO;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.LoaiDaiLy;
import org.doancnpm.Models.PhieuThu;
import org.doancnpm.Models.Quan;
import org.doancnpm.Ultilities.CurrentNVInfor;
import org.doancnpm.Ultilities.DayFormat;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LapPhieuThuDialogController implements Initializable {
    public MFXTextField ngayLapPhieuTextField;
    public MFXTextField maNhanVienTextField;
    public TextField ghiChuTextField;
    public TextField maDaiLyTextField;
    public TextField tongTienTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
    }
    public void setInitialValue(PhieuThu phieuThu) {
        if(phieuThu==null){
            return;
        }
        ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(phieuThu.getNgayLap()));
        maDaiLyTextField.setText(phieuThu.getMaDaiLi());
        maNhanVienTextField.setText(phieuThu.getMaNhanVien());
        ghiChuTextField.setText(phieuThu.getGhiChu());
        tongTienTextField.setText(Integer.toString(phieuThu.getSoTienThu()));
    }
    public PhieuThu getPhieuThu(){
        Date ngayTiepNhan = new Date(System.currentTimeMillis());
        PhieuThu phieuThu = new PhieuThu();
        try{
            phieuThu.setMaNhanVien(Integer.toString(CurrentNVInfor.getInstance().getLoggedInNhanVien().getID()));
            phieuThu.setMaDaiLi(maDaiLyTextField.getText());
            phieuThu.setGhiChu(ghiChuTextField.getText());
            phieuThu.setSoTienThu(Integer.parseInt(tongTienTextField.getText()));
            phieuThu.setNgayLap(ngayTiepNhan);
            phieuThu.setGhiChu( ghiChuTextField.getText());
        }
        catch(NumberFormatException e){
            System.out.println("Quan not set!");
            return null;
        }
        return phieuThu;
    }
    private void initData() {
        Date ngayTiepNhan = new Date(System.currentTimeMillis());
        ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(ngayTiepNhan));

        maNhanVienTextField.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getMaNhanVien());
    }


}
