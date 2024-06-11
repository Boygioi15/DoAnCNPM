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
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Ultilities.CurrentNVInfor;
import org.doancnpm.Ultilities.PopDialog;
import org.doancnpm.Ultilities.SHA256;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public CustomTextField maNVText;
    public CustomTextField nameEditText;
    public Button updateInforButton;
    public VBox errorUpdateBox;

    @FXML
    private VBox manHinhTaiKhoan;
    @FXML
    private Region imageAnchor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initOrgData();
        initEvent();
    }

    private void initOrgData() {
        nameText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getHoTen());
        phonenumberText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getSDT());
        emailText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getEmail());
        nameEditText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getHoTen());
        maNVText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getMaNhanVien());


        phonenumberText.setEditable(false);
        emailText.setEditable(false);
        nameEditText.setEditable(false);
        maNVText.setEditable(false);
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

    private void initEvent() {
        confirmButton.setOnAction(event -> {
            handleConfirmChangePassWord();
        });
        updateInforButton.setOnAction(event -> {
            handleUpdateInfor();
        });
    }

    boolean isUpdating = false;

    private void handleUpdateInfor() {
        clearErrorBox(errorUpdateBox);
        if (!isUpdating) {
            updateInforButton.setText("Xong");
            phonenumberText.setEditable(true);
            emailText.setEditable(true);
            nameEditText.setEditable(true);
        } else {
            String newName = nameEditText.getText().trim();
            String newEmail = emailText.getText().trim();
            String newPhone = phonenumberText.getText().trim();
            if (!validateUpdateUser(newEmail,newPhone)) {
                return;
            }
            NhanVien newData = CurrentNVInfor.getInstance().getLoggedInNhanVien();
            newData.setEmail(newEmail);
            newData.setSDT(newPhone);
            newData.setHoTen(newName);
            try {
                NhanVienDAO.getInstance().Update(newData.getID(), newData);
                CurrentNVInfor.getInstance().setLoggedInNhanVien(NhanVienDAO.getInstance().QueryID(newData.getID()));

                //
                nameText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getHoTen());
                phonenumberText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getSDT());
                emailText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getEmail());
                nameEditText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getHoTen());
                maNVText.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getMaNhanVien());

                PopDialog.popSuccessDialog("Cập nhật thành công");
            } catch (Exception e) {
                PopDialog.popErrorDialog("Cập nhật thất bại", e.getMessage());
            }


            updateInforButton.setText("Cập nhật");
            phonenumberText.setEditable(false);
            emailText.setEditable(false);
            nameEditText.setEditable(false);
            maNVText.setEditable(false);

        }
        isUpdating = !isUpdating;
    }

    public boolean validateUpdateUser(String email, String phone) {
        if(!isValidEmailFormat(email)){
            addErrorMessage(errorUpdateBox,"Email chưa đúng định dạng");
            return false;
        }
        if (!isValidPhoneNumber(phone)) {
            addErrorMessage(errorUpdateBox,"Số điện thoại chưa đúng dịnh dạng");
            return false;
        }
        return true;
    }
    private boolean isValidEmailFormat(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\+?[0-9]{10,15}$";
        return phoneNumber.matches(phoneRegex);
    }

    private void handleConfirmChangePassWord() {
        clearErrorBox(errorBox);
        // validate
        if (!validate()) {
            return;
        }
        try {
            String pass = newPasswordTextField.getText().trim();
            NhanVienDAO.getInstance().updatePasswordByEmail(emailText.getText(), SHA256.getSHA256Hash(pass));
            PopDialog.popSuccessDialog("Cập nhật mật khẩu thành công");
            updateUI();
        } catch (Exception e) {
            PopDialog.popSuccessDialog("Cập nhật mật khẩu thất bại");
        }
    }

    private boolean validate() {
        String newPassword = newPasswordTextField.getText();
        String confirmNewPassword = confirmNewPasswordTextField.getText();
        if (newPassword.isEmpty()) {
            addErrorMessage(errorBox, "Vui lòng nhập mật khẩu mới ");
            return false;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            addErrorMessage(errorBox, "Mật khẩu không khớp");
            return false;
        }
        return true;
    }

    private void updateUI() {
        newPasswordTextField.clear();
        confirmNewPasswordTextField.clear();
    }

}
