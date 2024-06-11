package org.doancnpm.ManHinhTaiKhoan;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.CustomTextField;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.Ultilities.CurrentNVInfor;
import org.doancnpm.Ultilities.PopDialog;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class ManHinhTaiKhoanController implements Initializable {

    public CustomTextField phonenumberText;
    public CustomTextField emailText;
    public PasswordField newPasswordTextField;
    public PasswordField confirmNewPasswordTextField;
    public Button confirmButton;
    public VBox errorBox;
    public Text nameText;
    public ImageView backgroundImg;
    public ImageView profileImg;

    @FXML
    private VBox manHinhTaiKhoan;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getHoTen());
        phonenumberText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getSDT());
        emailText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getEmail());
        initEvent();
    }
    private void clearErrorBox(VBox errorBox) {
        errorBox.getChildren().clear();
    }
    private void addErrorMessage(VBox errorBox, String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorBox.getChildren().add(errorLabel);
    }
    public void setVisibility(boolean visibility) {
        this.manHinhTaiKhoan.setVisible(visibility);
    }
    private void initEvent(){
        confirmButton.setOnAction(event -> {
            handleConfirmChangePassWord();
        });
    }

    private void handleConfirmChangePassWord() {
        clearErrorBox(errorBox);
        // validate
        if(!validate()){
            return;
        }
        try{
            NhanVienDAO.getInstance().updatePasswordByEmail(emailText.getText(),newPasswordTextField.getText());
            PopDialog.popSuccessDialog("Cập nhật mật khẩu thành công");
            updateUI();
        } catch (Exception e){
            PopDialog.popSuccessDialog("Cập nhật mật khẩu thất bại");
        }
    }
    private boolean validate(){
        String newPassword = newPasswordTextField.getText();
        String confirmNewPassword = confirmNewPasswordTextField.getText();
        if(newPassword.isEmpty()) {
            addErrorMessage(errorBox, "Vui lòng nhập mật khẩu mới ");
            return false;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            addErrorMessage(errorBox, "Mật khẩu không khớp");
            return false;
        }
        return true;
    }
    private void updateUI(){
        newPasswordTextField.clear();
        confirmNewPasswordTextField.clear();
    }

}
