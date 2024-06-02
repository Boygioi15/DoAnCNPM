package org.doancnpm.ManHinhDaiLy;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.doancnpm.Models.DaiLy;

import java.io.IOException;

/**
 * Custom dialog to return a validated pair of values -- Text and URL --
 * from TextField controls
 *
 * @author carl
 */
public class TiepNhanDaiLyDialog extends Dialog<DaiLy>  {

    public TiepNhanDaiLyDialog() throws IOException {
        this(null);
    }

    /**
     * HyperlinkDialog can be pre-filled with values from initialValue or
     * left blank when initialValue is null
     *
     * @param initialValue allows null
     * @throws IOException
     */
    public TiepNhanDaiLyDialog(DaiLy initialValue) throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhDaiLy/TiepNhanDaiLyUI.fxml"));

        ButtonType saveButtonType;
        if(initialValue==null){
            saveButtonType = new ButtonType("Thêm mới", ButtonBar.ButtonData.OK_DONE);
        }
        else{
            saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        }
        ButtonType cancelButtonType = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.setTitle("Tiếp nhận đại lý");
        this.getDialogPane().setContent(fxmlLoader.load());

        TiepNhanDaiLyDialogController c = fxmlLoader.getController();

        c.setInitialValue(initialValue); // null safe

        this.setResultConverter(p -> {
            if( p == saveButtonType ) {
                return c.getDaiLy();
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
