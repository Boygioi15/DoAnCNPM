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
import org.doancnpm.Models.ChiTietPhieuNhap;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.PhieuNhap;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LapPhieuNhapDialog extends Dialog<PhieuNhap> {
    public LapPhieuNhapDialog(NhanVien nvLoggedIn) throws IOException {
        this(null,nvLoggedIn);
    }
    public LapPhieuNhapDialog(PhieuNhap initialValue, NhanVien nvLoggedIn) throws IOException {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhPhieuNhap/LapPhieuNhap.fxml"));
        ButtonType saveButtonType;
        if(initialValue==null){
            saveButtonType = new ButtonType("Thêm mới", ButtonBar.ButtonData.OK_DONE);
        }
        else{
            saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        }
        ButtonType cancelButtonType = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.setTitle("Lập phiếu nhập");
        this.getDialogPane().setContent(fxmlLoader.load());

        LapPhieuNhapDialogController c = fxmlLoader.getController();

        c.setInitialValue(initialValue, nvLoggedIn); // null safe

        this.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        final Button btnOk = (Button)this.getDialogPane().lookupButton(saveButtonType);
        btnOk.addEventFilter(ActionEvent.ACTION,
                event -> {
                    PhieuNhap phieuNhap = c.getPhieuNhap();
                    List<ChiTietPhieuNhap> ctpns = c.getChiTietPhieuNhap();
                    try{
                        PhieuNhapDAO.getInstance().Insert(phieuNhap);
                        //turn back to get ID
                        phieuNhap = PhieuNhapDAO.getInstance().QueryMostRecent();
                        System.out.println(phieuNhap.getID());
                        for(ChiTietPhieuNhap ctpn: ctpns){
                            ctpn.setMaPhieuNhap(phieuNhap.getID());
                        }
                        CTPNDAO.getInstance().InsertBlock(ctpns);
                    }catch (SQLException e){
                        e.printStackTrace();
                    }

                }
        );
        //this.getDialogPane().lookupButton(saveButtonType).disableProperty().bind(c.validProperty().not());
    }
}