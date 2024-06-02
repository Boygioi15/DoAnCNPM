package org.doancnpm.ManHinhQuyDinh;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.doancnpm.Models.TaiKhoan;

import java.io.IOException;

/**
 * Custom dialog to return a validated pair of values -- Text and URL --
 * from TextField controls
 *
 * @author carl
 */
public class ThemTKDialog extends Dialog<TaiKhoan>  {

    public ThemTKDialog() throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhQuyDinh/ThemTaiKhoanDialog.fxml"));

        ButtonType saveButtonType = new ButtonType("Thêm mới", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.setTitle("Tiếp nhận đại lý");
        this.getDialogPane().setContent(fxmlLoader.load());

        ThemTKDialogController c = fxmlLoader.getController();
        this.setResultConverter(p -> {
            if( p == saveButtonType ) {
                return c.getTaiKhoan();
            } else {
                return null;
            }
        });

        this
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, cancelButtonType);
    }
}
