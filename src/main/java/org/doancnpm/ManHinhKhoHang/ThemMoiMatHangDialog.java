package org.doancnpm.ManHinhKhoHang;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.MatHang;

import java.io.IOException;

/**
 * Custom dialog to return a validated pair of values -- Text and URL --
 * from TextField controls
 *
 * @author carl
 */
public class ThemMoiMatHangDialog extends Dialog<MatHang>  {

    public ThemMoiMatHangDialog() throws IOException {
        this(null);
    }

    /**
     * HyperlinkDialog can be pre-filled with values from initialValue or
     * left blank when initialValue is null
     *
     * @param initialValue allows null
     * @throws IOException
     */
    public ThemMoiMatHangDialog(MatHang initialValue) throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhKhoHang/ThemMoiMatHang.fxml"));

        ButtonType saveButtonType;
        if(initialValue==null){
            saveButtonType = new ButtonType("Thêm mới", ButtonBar.ButtonData.OK_DONE);
        }
        else{
            saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        }
        ButtonType cancelButtonType = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.setTitle("Thêm mới mặt hàng");
        this.getDialogPane().setContent(fxmlLoader.load());

        ThemMoiMatHangDialogController c = fxmlLoader.getController();

        c.setInitialValue(initialValue); // null safe

        this.setResultConverter(p -> {
            if( p == saveButtonType ) {
                return c.getMatHang();
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
