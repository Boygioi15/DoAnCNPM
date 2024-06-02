package org.doancnpm.ManHinhPhieuThu;


import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.PhieuThu;

import java.io.IOException;

public class LapPhieuThuDialog extends Dialog<PhieuThu> {

    public LapPhieuThuDialog(NhanVien nvLoggedIn) throws IOException {
        this(null,nvLoggedIn);
    }
    /**
     * HyperlinkDialog can be pre-filled with values from initialValue or
     * left blank when initialValue is null
     *
     * @param initialValue allows null
     * @throws IOException
     */
    public LapPhieuThuDialog(PhieuThu initialValue, NhanVien nvLoggedIn) throws IOException {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhPhieuThu/LapPhieuThu.fxml"));
        ButtonType saveButtonType;
        if(initialValue==null){
            saveButtonType = new ButtonType("Thêm mới", ButtonBar.ButtonData.OK_DONE);
        }
        else{
            saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        }

        ButtonType cancelButtonType = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.setTitle("Lập phiếu thu");
        this.getDialogPane().setContent(fxmlLoader.load());

        LapPhieuThuDialogController c = fxmlLoader.getController();

        c.setInitialValue(initialValue,nvLoggedIn); // null safe

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
    }
}