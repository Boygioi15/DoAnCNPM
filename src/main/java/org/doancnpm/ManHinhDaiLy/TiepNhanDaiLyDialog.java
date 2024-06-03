package org.doancnpm.ManHinhDaiLy;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Ultilities.PopDialog;

import java.io.IOException;
import java.sql.SQLException;

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
    Boolean themHaySua;
    public TiepNhanDaiLyDialog(DaiLy initialValue) throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhDaiLy/TiepNhanDaiLyUI.fxml"));

        ButtonType saveButtonType;
        if(initialValue==null){
            saveButtonType = new ButtonType("Thêm mới", ButtonBar.ButtonData.OK_DONE);
            themHaySua = true;
        }
        else{
            saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
            themHaySua = false;
        }
        ButtonType cancelButtonType = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.setTitle("Tiếp nhận đại lý");
        this.getDialogPane().setContent(fxmlLoader.load());

        TiepNhanDaiLyDialogController c = fxmlLoader.getController();

        c.setInitialValue(initialValue); // null safe

        this
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, cancelButtonType);


        //them vao cuoi cung
        final Button btnOk = (Button)this.getDialogPane().lookupButton(saveButtonType);
        btnOk.setOnAction(ob -> {
            //check validator
            DaiLy dl = c.getDaiLy();
            //them
            if(themHaySua){
                try {
                    DaiLyDAO.getInstance().Insert(dl);
                } catch (SQLException e) {
                    PopDialog.popErrorDialog("Thêm mới đại lý thất bại",e.getMessage());
                    ob.consume();
                }
            }
            //sua
            else{
                try {
                    DaiLyDAO.getInstance().Update(dl.getID(),dl);
                } catch (SQLException e) {
                    PopDialog.popErrorDialog("Cập nhật đại lý thất bại",e.getMessage());
                    ob.consume();
                }
            }

        });


        //this.getDialogPane().lookupButton(saveButtonType).disableProperty().bind(c.validProperty().not());
    }
}
