package org.doancnpm.ManHinhPhieuNhap;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.Models.ChiTietPhieuNhap;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.PhieuNhap;
import org.doancnpm.Ultilities.ChiTietPhieu.ChiTietPhieuNhapRow;
import org.doancnpm.Ultilities.ChiTietPhieu.ChiTietPhieuXuatRow;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.MoneyFormatter;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LapPhieuNhapDialogController implements Initializable {
    @FXML private MFXTextField ngayLapPhieuTextField;
    @FXML private MFXTextField nhanVienTextField;
    @FXML private TextField nccTextField;
    @FXML Text tongTienText;

    @FXML private TextArea ghiChuTextArea;

    @FXML Button themCTPNButton;
    @FXML VBox ctpnContainer;

    NhanVien nvLapPhieu = null;
    PhieuNhap phieuNhapGoc;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themCTPNButton.setOnAction(ob -> {
            themCTPN();
        });
        themCTPN();
    }
    public void themCTPN(){
        ChiTietPhieuNhapRow temp = new ChiTietPhieuNhapRow(ctpnContainer);
        ctpnContainer.getChildren().add(ctpnContainer.getChildren().size()-2,temp);
        temp.SetOnXoa(event -> {
            if(ctpnContainer.getChildren().size()<=3){
                event.consume();
            }else{
                ctpnContainer.getChildren().remove(temp);
            }
        });
        temp.thanhTienTextField.textProperty().addListener(ob ->{
            capNhatTongTien();
        });
    }
    private void capNhatTongTien(){
        Long tt = 0L;
        for(int i = 0;i<ctpnContainer.getChildren().size();i++){
            if(ctpnContainer.getChildren().get(i) instanceof ChiTietPhieuNhapRow temp){
                tt += temp.getThanhTien();

            }
        }
        tongTienText.setText(MoneyFormatter.convertLongToString( tt));
    }
    public void setInitialValue(PhieuNhap phieuNhap, NhanVien nvLoggedIn) {
        if(phieuNhap==null){
            nhanVienTextField.setText(nvLoggedIn.getHoTen());
            this.nvLapPhieu = nvLoggedIn;

            Date ngayTiepNhan = new Date(System.currentTimeMillis());
            ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(ngayTiepNhan));
            return;
        }
        phieuNhapGoc = phieuNhap;
        ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(phieuNhap.getNgayLapPhieu()));
        try{
            NhanVien nv = NhanVienDAO.getInstance().QueryID(phieuNhap.getMaNhanVien());
            nhanVienTextField.setText(nv.getHoTen());

        }catch (SQLException _){}

        nccTextField.setText(phieuNhap.getNhaCungCap());
        ghiChuTextArea.setText(phieuNhap.getGhiChu());

        ngayLapPhieuTextField.setDisable(true);
        nhanVienTextField.setDisable(true);
        nccTextField.setDisable(true);
    }
    public PhieuNhap getPhieuNhap(){
        if(phieuNhapGoc==null){
            Date ngayTiepNhan = new Date(System.currentTimeMillis());
            PhieuNhap phieuNhap = new PhieuNhap();

            phieuNhap.setMaNhanVien(nvLapPhieu.getID());
            phieuNhap.setNhaCungCap(nccTextField.getText());
            phieuNhap.setNgayLapPhieu(ngayTiepNhan);
            phieuNhap.setGhiChu(ghiChuTextArea.getText());
            return phieuNhap;
        }
        else{
            phieuNhapGoc.setGhiChu(ghiChuTextArea.getText());
            return phieuNhapGoc;
        }
    }
    public List<ChiTietPhieuNhap> getChiTietPhieuNhap(){
        List<ChiTietPhieuNhap> chiTietPhieuNhapList = new ArrayList<>();
        for(int i = 0;i<ctpnContainer.getChildren().size();i++){
            if(ctpnContainer.getChildren().get(i) instanceof ChiTietPhieuNhapRow temp){
                chiTietPhieuNhapList.add(temp.getChiTietPhieuNhap());
            }
        }
        return chiTietPhieuNhapList;
    }

    public String GetValid() {
        String validRows = "";
        if(nccTextField.getText().isEmpty()){
            validRows = "Nhà cung cấp không được để trống ";
        }
        for(int i = 0; i< ctpnContainer.getChildren().size(); i++){
            if(ctpnContainer.getChildren().get(i) instanceof ChiTietPhieuNhapRow temp){
                String validRow = temp.GetValid();
                if(!validRow.isEmpty()){
                    validRows = validRows.concat("Hàng "+(i+1)+": "+validRow).concat("\n");
                }
            }
        }
        return validRows;
    }
}
