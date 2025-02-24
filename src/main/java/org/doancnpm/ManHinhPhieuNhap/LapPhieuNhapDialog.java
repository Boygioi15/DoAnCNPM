package org.doancnpm.ManHinhPhieuNhap;


import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.doancnpm.DAO.CTPNDAO;
import org.doancnpm.DAO.PhieuNhapDAO;
import org.doancnpm.DAO.PhieuXuatDAO;
import org.doancnpm.Models.ChiTietPhieuNhap;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.PhieuNhap;
import org.doancnpm.Ultilities.PopDialog;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LapPhieuNhapDialog extends Dialog<PhieuNhap> {
    Boolean themHaySua;
    public LapPhieuNhapDialog(NhanVien nvLoggedIn) throws IOException {
        this(null,nvLoggedIn);
    }
    public LapPhieuNhapDialog(PhieuNhap initialValue, NhanVien nvLoggedIn) throws IOException {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhPhieuNhap/LapPhieuNhap.fxml"));
        ButtonType saveButtonType;
        if(initialValue==null){
            this.setTitle("Lập phiếu nhập hàng");
            saveButtonType = new ButtonType("Thêm mới", ButtonBar.ButtonData.OK_DONE);
            themHaySua = true;
        }
        else{
            this.setTitle("Cập nhật phiếu nhập hàng");
            saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
            themHaySua = false;
        }
        ButtonType cancelButtonType = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);


        this.getDialogPane().setContent(fxmlLoader.load());

        LapPhieuNhapDialogController c = fxmlLoader.getController();

        c.setInitialValue(initialValue, nvLoggedIn); // null safe

        this.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        final Button btnOk = (Button)this.getDialogPane().lookupButton(saveButtonType);
        btnOk.setId("saveButton");
        btnOk.addEventFilter(ActionEvent.ACTION,
                event -> {
                    if(themHaySua){
                        String validString = c.GetValid();
                        if(!validString.isEmpty()){
                            PopDialog.popErrorDialog("Thêm mới phiếu nhập thất bại",validString);
                            event.consume();
                            return;
                        }
                        PhieuNhap phieuNhap = c.getPhieuNhap();
                        List<ChiTietPhieuNhap> ctpns = c.getChiTietPhieuNhap();
                        try{
                            PhieuNhapDAO.getInstance().Insert(phieuNhap);
                            phieuNhap = PhieuNhapDAO.getInstance().QueryMostRecent();
                            for(ChiTietPhieuNhap ctpn: ctpns){
                                ctpn.setMaPhieuNhap(phieuNhap.getID());
                            }
                            CTPNDAO.getInstance().InsertBlock(ctpns);
                            PopDialog.popSuccessDialog("Thêm mới phiếu nhập thành công");
                        }
                        catch (SQLException e){
                            try {
                                PhieuNhapDAO.getInstance().Delete(phieuNhap.getID());
                            } catch (SQLException _) {}
                            PopDialog.popErrorDialog("Thêm phiếu nhập thất bại",e.getMessage());
                            event.consume();
                            return;
                        }
                    }
                    else{
                        PhieuNhap phieuNhap = c.getPhieuNhap();
                        try {
                            PhieuNhapDAO.getInstance().Update(phieuNhap.getID(),phieuNhap);
                            PopDialog.popSuccessDialog("Cập nhật phiếu nhập thành công");
                        } catch (SQLException e) {
                            PopDialog.popErrorDialog("Cập nhật phiếu nhập thất bại",e.getMessage());
                        }
                    }
                }
        );
        //this.getDialogPane().lookupButton(saveButtonType).disableProperty().bind(c.validProperty().not());
    }
}