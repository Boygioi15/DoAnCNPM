package org.doancnpm.ManHinhPhieuXuat;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.Models.ChiTietPhieuXuat;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.PhieuXuat;
import org.doancnpm.Ultilities.ChiTietPhieu.ChiTietPhieuXuatRow;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.MoneyFormatter;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LapPhieuXuatDialogController implements Initializable {
    @FXML private MFXTextField ngayLapPhieuTextField;
    @FXML private MFXTextField nhanVienTextField;
    @FXML private SearchableComboBox<DaiLy> dlComboBox;
    @FXML Text tongTienText;

    @FXML private TextArea ghiChuTextArea;

    @FXML Button themCTPXButton;
    @FXML VBox ctpxContainer;

    NhanVien nvLapPhieu = null;
    PhieuXuat phieuXuatGoc;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themCTPXButton.setOnAction(ob -> {
            themCTPX();
        });
        themCTPX();
        initDaiLyComboBox();

    }
    private void initDaiLyComboBox() {
        try {
            ObservableList<DaiLy> daiLy = FXCollections.observableArrayList(DaiLyDAO.getInstance().QueryAll());

            // Sử dụng StringConverter để hiển thị dữ liệu trong ComboBox
            StringConverter<DaiLy> quanStringConverter = new StringConverter<DaiLy>() {
                @Override
                public String toString(DaiLy dl) {
                    return dl == null ? null : (dl.getMaDaiLy() + "-" + dl.getTenDaiLy()); //lên hình
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
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void themCTPX(){
        ChiTietPhieuXuatRow temp = new ChiTietPhieuXuatRow(ctpxContainer);
        ctpxContainer.getChildren().add(ctpxContainer.getChildren().size()-2,temp);
        temp.SetOnXoa(event -> {
            if(ctpxContainer.getChildren().size()<=3){
                event.consume();
            }else{
                ctpxContainer.getChildren().remove(temp);
            }
        });
        temp.thanhTienTextField.textProperty().addListener(ob ->{
            capNhatTongTien();
        });
    }
    private void capNhatTongTien(){
        Long tt = 0L;
        for(int i = 0; i< ctpxContainer.getChildren().size(); i++){
            if(ctpxContainer.getChildren().get(i) instanceof ChiTietPhieuXuatRow temp){
                tt += temp.getThanhTien();
            }
        }
        tongTienText.setText(MoneyFormatter.convertLongToString(tt));
    }
    public void setInitialValue(PhieuXuat phieuXuat, NhanVien nvLoggedIn) {
        if(phieuXuat==null){
            nhanVienTextField.setText(nvLoggedIn.getHoTen());
            this.nvLapPhieu = nvLoggedIn;

            Date ngayTiepNhan = new Date(System.currentTimeMillis());
            ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(ngayTiepNhan));
            return;
        }
        phieuXuatGoc = phieuXuat;
        ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(phieuXuat.getNgayLapPhieu()));
        try{
            NhanVien nv = NhanVienDAO.getInstance().QueryID(phieuXuat.getMaNhanVien());
            nhanVienTextField.setText(nv.getHoTen());

            DaiLy dl = DaiLyDAO.getInstance().QueryID(phieuXuat.getMaDaiLy());

            dlComboBox.setValue(dl);
        }catch (SQLException _){}

        ghiChuTextArea.setText(phieuXuat.getGhiChu());


        ngayLapPhieuTextField.setDisable(true);
        nhanVienTextField.setDisable(true);
        dlComboBox.setDisable(true);
    }
    public PhieuXuat getPhieuXuat(){
        if(phieuXuatGoc ==null){
            Date ngayTiepNhan = new Date(System.currentTimeMillis());
            PhieuXuat phieuXuat = new PhieuXuat();

            phieuXuat.setMaNhanVien(nvLapPhieu.getID());
            phieuXuat.setMaDaiLy(dlComboBox.getValue().getID());
            phieuXuat.setNgayLapPhieu(ngayTiepNhan);
            phieuXuat.setGhiChu(ghiChuTextArea.getText());
            return phieuXuat;
        }
        else{
            phieuXuatGoc.setGhiChu(ghiChuTextArea.getText());
            return phieuXuatGoc;
        }
    }
    public List<ChiTietPhieuXuat> getChiTietPhieuXuat(){
        List<ChiTietPhieuXuat> chiTietPhieuXuatList = new ArrayList<>();
        for(int i = 0; i< ctpxContainer.getChildren().size(); i++){
            if(ctpxContainer.getChildren().get(i) instanceof ChiTietPhieuXuatRow temp){
                chiTietPhieuXuatList.add(temp.getChiTietPhieuXuat());
            }
        }
        return chiTietPhieuXuatList;
    }

    public String GetValid(){
        Boolean emptyDaiLy = dlComboBox.getValue()==null;
        String validRows = "";
        if(emptyDaiLy){
            validRows = validRows.concat("Đại lý không được để trống. \n");
        }
        for(int i = 0; i< ctpxContainer.getChildren().size(); i++){
            if(ctpxContainer.getChildren().get(i) instanceof ChiTietPhieuXuatRow temp){
                String validRow = temp.GetValid();
                if(!validRow.isEmpty()){
                    validRows = validRows.concat("Hàng "+(i+1)+": "+validRow).concat("\n");
                }
            }
        }
        return validRows;
    }
}
