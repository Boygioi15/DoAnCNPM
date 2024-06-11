package org.doancnpm.ManHinhNhanVien;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.doancnpm.DAO.CTPNDAO;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.DAO.PhieuNhapDAO;
import org.doancnpm.Models.ChiTietPhieuNhap;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.PhieuNhap;
import org.doancnpm.Ultilities.PopDialog;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Custom dialog to return a validated pair of values -- Text and URL --
 * from TextField controls
 *
 * @author carl
 */
public class TiepNhanNhanVienDialog extends Dialog<NhanVien>  {

    public TiepNhanNhanVienDialog() throws IOException {
        this(null);
    }

    /**
     * HyperlinkDialog can be pre-filled with values from initialValue or
     * left blank when initialValue is null
     *
     * @param initialValue allows null
     * @throws IOException
     */

    public TiepNhanNhanVienDialog(NhanVien initialValue) throws IOException {
        super();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhNhanVien/TiepNhanNhanVien2.fxml"));

        ButtonType saveButtonType;
        if(initialValue==null){
            this.setTitle("Tiếp nhận nhân viên");
            saveButtonType = new ButtonType("Thêm mới", ButtonBar.ButtonData.OK_DONE);
        }
        else{
            this.setTitle("Cập nhật nhân viên");
            saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        }
        ButtonType cancelButtonType = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);


        this.getDialogPane().setContent(fxmlLoader.load());

        TiepNhanNhanVienDialogController c = fxmlLoader.getController();

        c.setInitialValue(initialValue); // null safe

        this.setResultConverter(p -> {
            if( p == saveButtonType ) {
                return c.getNhanVien();
            } else {
                return null;
            }
        });

        this
                .getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, cancelButtonType);

        //this.getDialogPane().lookupButton(saveButtonType).disableProperty().bind(c.validProperty().not());

        final Button btnOk = (Button)this.getDialogPane().lookupButton(saveButtonType);
        btnOk.addEventFilter(ActionEvent.ACTION,
                event -> {
                    String validString = c.GetValid();
                    if(!validString.isEmpty()){
                        PopDialog.popErrorDialog("Thêm mới nhân viên thất bại",validString);
                        event.consume();
                        return;
                    }
                    NhanVien nhanVien = c.getNhanVien();
                    if(initialValue!=null){
                        try {
                            NhanVienDAO.getInstance().Update(initialValue.getID(),nhanVien);
                            PopDialog.popSuccessDialog("Cập nhật nhân viên "+initialValue.getMaNhanVien()+" thành công");
                        } catch (SQLException e) {
                            PopDialog.popErrorDialog("Cập nhật nhân viên "+initialValue.getMaNhanVien()+" thất bại",
                                    e.getMessage());
                        }
                    }
                    else{
                        try {
                            NhanVienDAO.getInstance().Insert(nhanVien);
                            PopDialog.popSuccessDialog("Thêm mới nhân viên "+ nhanVien.getHoTen()+" thành công");
                        }
                        catch (SQLException e) {
                            PopDialog.popErrorDialog("Thêm mới nhân viên thất bại", e.getMessage());
                        }
                    }


                }
        );
    }
}
