package org.doancnpm.ManHinhPhieuThu;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.doancnpm.DAO.PhieuThuDAO;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.PhieuThu;
import org.doancnpm.Ultilities.PopDialog;

import java.io.IOException;
import java.sql.SQLException;

public class LapPhieuThuDialog extends Dialog<PhieuThu> {

    public LapPhieuThuDialog(NhanVien nvLoggedIn) throws IOException {
        this(null, nvLoggedIn);
    }

    /**
     * HyperlinkDialog can be pre-filled with values from initialValue or
     * left blank when initialValue is null
     *
     * @param initialValue allows null
     * @throws IOException
     */

    boolean themhaysua;
    public LapPhieuThuDialog(PhieuThu initialValue, NhanVien nvLoggedIn) throws IOException {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhPhieuThu/LapPhieuThu.fxml"));
        ButtonType saveButtonType;
        if (initialValue == null) {
            this.setTitle("Lập phiếu thu tiền");
            saveButtonType = new ButtonType("Thêm mới", ButtonBar.ButtonData.OK_DONE);
            themhaysua = true;
        } else {
            this.setTitle("Cập nhật phiếu thu tiền");
            saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
            themhaysua = false;
        }

        ButtonType cancelButtonType = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.getDialogPane().setContent(fxmlLoader.load());

        LapPhieuThuDialogController c = fxmlLoader.getController();

        c.setInitialValue(initialValue, nvLoggedIn); // null safe

        this.setResultConverter(p -> {
            if (p == saveButtonType) {
                return c.getPhieuThu();
            } else {
                return null;
            }
        });
        this
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, cancelButtonType);

        //this.getDialogPane().lookupButton(saveButtonType).disableProperty().bind(c.validProperty().not());

        Button saveButton = (Button) this.getDialogPane().lookupButton(saveButtonType);
        if (saveButton != null) {
            saveButton.setId("saveButton");
        }

        saveButton.addEventFilter(ActionEvent.ACTION, ob -> {
            String error = c.getValidData();
            if(!error.isEmpty()){
                PopDialog.popErrorDialog("Thêm mới phiếu thu thất bại", error);
                ob.consume();
                return;
            }
            PhieuThu pt = c.getPhieuThu();
            if(themhaysua){
                try {
                    PhieuThuDAO.getInstance().Insert(pt);
                    PopDialog.popSuccessDialog("Thêm mới phiếu thu thành công");
                } catch (SQLException e) {
                    PopDialog.popErrorDialog("Thêm mới phiếu thu thất bại", e.getMessage());
                    ob.consume();
                }
            }else {
                try {
                    PhieuThuDAO.getInstance().Update(pt.getID(),pt);
                    PopDialog.popSuccessDialog("Cập nhật phiếu thu thành công");
                }
                catch (SQLException e) {
                    PopDialog.popErrorDialog("Cập nhật phiếu thu thất bại",
                            e.getMessage());
                    ob.consume();
                }
            }



        });

    }
}