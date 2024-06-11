package org.doancnpm.ManHinhKhoHang;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.doancnpm.DAO.MatHangDAO;
import org.doancnpm.DAO.PhieuThuDAO;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.MatHang;
import org.doancnpm.Models.PhieuThu;
import org.doancnpm.Ultilities.PopDialog;

import java.io.IOException;
import java.sql.SQLException;

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
    boolean themhaysua;
    public ThemMoiMatHangDialog(MatHang initialValue) throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhKhoHang/ThemMoiMatHang.fxml"));

        ButtonType saveButtonType;
        if(initialValue==null){
            saveButtonType = new ButtonType("Thêm mới", ButtonBar.ButtonData.OK_DONE);
            themhaysua =true;
        }
        else{
            saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
            themhaysua=false;
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


        final Button btnOk = (Button) this.getDialogPane().lookupButton(saveButtonType);
        btnOk.addEventFilter(ActionEvent.ACTION, ob -> {
            String error = c.getValidData();
            if(!error.isEmpty()){
                PopDialog.popErrorDialog("Thêm mới mặt hàng thất bại", error);
                ob.consume();
                return;
            }
            MatHang matHang = c.getMatHang();
            if(themhaysua){
                try {
                    MatHangDAO.getInstance().Insert(matHang);
                    PopDialog.popSuccessDialog("Thêm mới mặt hàng thành công");
                }
                catch (SQLException e) {
                    PopDialog.popErrorDialog("Thêm mới mặt hàng thất bại", e.getMessage());
                }
            }
            else{
                try {
                    MatHangDAO.getInstance().Update(matHang.getID(),matHang);
                    PopDialog.popSuccessDialog("Cập nhật mặt hàng "+matHang.getMaMatHang()+" thành công");
                } catch (SQLException e) {
                    PopDialog.popErrorDialog("Cập nhật mặt hàng "+matHang.getMaMatHang()+" thất bại",
                            e.getMessage());
                }

            }


        });
    }
}
