package org.doancnpm.ManHinhPhieuXuat;


import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.doancnpm.DAO.CTPNDAO;
import org.doancnpm.DAO.CTPXDAO;
import org.doancnpm.DAO.PhieuNhapDAO;
import org.doancnpm.DAO.PhieuXuatDAO;
import org.doancnpm.Models.ChiTietPhieuXuat;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.PhieuNhap;
import org.doancnpm.Models.PhieuXuat;
import org.doancnpm.Ultilities.PopDialog;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LapPhieuXuatDialog extends Dialog<PhieuXuat> {
    boolean themHaySua;
    public LapPhieuXuatDialog(NhanVien nvLoggedIn) throws IOException {
        this(null,nvLoggedIn);
    }
    public LapPhieuXuatDialog(PhieuXuat initialValue, NhanVien nvLoggedIn) throws IOException {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhPhieuXuat/LapPhieuXuat.fxml"));
        ButtonType saveButtonType;
        if(initialValue==null){
            this.setTitle("Lập phiếu xuất hàng");
            saveButtonType = new ButtonType("Thêm mới", ButtonBar.ButtonData.OK_DONE);
            themHaySua = true;
        }
        else{
            this.setTitle("Cập nhật phiếu xuất hàng");
            saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
            themHaySua = false;
        }
        ButtonType cancelButtonType = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.getDialogPane().setContent(fxmlLoader.load());

        LapPhieuXuatDialogController c = fxmlLoader.getController();

        c.setInitialValue(initialValue, nvLoggedIn); // null safe

        this.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        final Button btnOk = (Button)this.getDialogPane().lookupButton(saveButtonType);
        btnOk.setId("saveButton");
        btnOk.addEventFilter(ActionEvent.ACTION,
                event -> {
                    if(themHaySua){
                        String validString = c.GetValid();
                        if(!validString.isEmpty()){
                            PopDialog.popErrorDialog("Thêm mới phiếu xuất thất bại",validString);
                            event.consume();
                            return;
                        }
                        PhieuXuat phieuXuat = c.getPhieuXuat();
                        List<ChiTietPhieuXuat> ctpxs = c.getChiTietPhieuXuat();
                        try{
                            PhieuXuatDAO.getInstance().Insert(phieuXuat);
                            //turn back to get ID
                            phieuXuat = PhieuXuatDAO.getInstance().QueryMostRecent();

                            for(ChiTietPhieuXuat ctpx: ctpxs){
                                ctpx.setMaPhieuXuat(phieuXuat.getID());
                            }
                            CTPXDAO.getInstance().InsertBlock(ctpxs);
                            PopDialog.popSuccessDialog("Thêm mới phiếu xuất thành công");
                            return;
                        }catch (SQLException e){
                            try {
                                PhieuXuatDAO.getInstance().Delete(phieuXuat.getID());
                            } catch (SQLException _) {}
                            PopDialog.popErrorDialog("Thêm mới phiếu xuất thất bại",e.getMessage());
                            event.consume();
                        }
                    }
                    else{
                        PhieuXuat phieuXuat = c.getPhieuXuat();
                        try {
                            PhieuXuatDAO.getInstance().Update(phieuXuat.getID(),phieuXuat);
                            PopDialog.popSuccessDialog("Cập nhật phiếu xuất thành công");
                        } catch (SQLException e) {
                            PopDialog.popErrorDialog("Cập nhật phiếu xuất thất bại",e.getMessage());
                        }
                    }

                }
        );
        //this.getDialogPane().lookupButton(saveButtonType).disableProperty().bind(c.validProperty().not());
    }
}