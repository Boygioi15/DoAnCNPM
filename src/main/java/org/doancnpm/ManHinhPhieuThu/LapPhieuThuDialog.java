package org.doancnpm.ManHinhPhieuThu;


import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.doancnpm.ManHinhDaiLy.DirectAddDialogController;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.PhieuThu;

import java.io.IOException;

public class LapPhieuThuDialog extends Dialog<PhieuThu> {

    public LapPhieuThuDialog() throws IOException {
        this(null);
    }

    /**
     * HyperlinkDialog can be pre-filled with values from initialValue or
     * left blank when initialValue is null
     *
     * @param initialValue allows null
     * @throws IOException
     */
    public LapPhieuThuDialog(PhieuThu initialValue) throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ManHinhPhieuThu/LapPhieuThuUI.fxml"));

        ButtonType saveButtonType = new ButtonType("Thêm mới", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.setTitle("Lập phiếu thu");
        this.getDialogPane().setContent(fxmlLoader.load());

        LapPhieuThuDialogController c = fxmlLoader.getController();

        c.setInitialValue(initialValue); // null safe

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