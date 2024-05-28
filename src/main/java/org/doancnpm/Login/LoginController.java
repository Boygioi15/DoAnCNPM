package org.doancnpm.Login;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.DAO.TaiKhoanDAO;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.TaiKhoan;
import org.doancnpm.NavController;
import org.doancnpm.Ultilities.CurrentNVInfor;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Controls the login screen
 */
public class LoginController implements Initializable {
    public AnchorPane verifyOtpPane;
    public AnchorPane resetPasswordPane;
    public AnchorPane emailInputPane;
    public VBox mainLoginPane;


    // mainLoginPane
    @FXML
    public TextField user;
    @FXML
    public TextField password;
    @FXML
    public Button loginButton;
    @FXML
    public VBox errorBox;
    @FXML
    public Label forgotPasswordLabel;


    //emailInputPane
    @FXML
    public Button backToMainLoginButton;
    @FXML
    public TextField emailText;
    @FXML
    public Button sendOtpButton;

    // verifyOtpPane
    @FXML
    public Button backToEmailInputButton;
    @FXML
    public TextField otpText;
    @FXML
    public Button verifyOtpButton;
    @FXML
    public Label reSendOtpLabel;

    //resetPasswordPane
    @FXML
    public Button backToOtpPaneButton;
    public Button backButton;
    public TextField reEnterPasswordText;
    public TextField enterPasswordText;


    StringProperty currentScreenString = new SimpleStringProperty("");
    private TaiKhoanDAO taiKhoanDAO = TaiKhoanDAO.getInstance();
    private Preferences preferences;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearErrorBox();
        currentScreenString.addListener((observableValue, s, t1) -> {
            switchScreen();
        });
        initializeMainLoginPane();
        initializeEmailInputPane();
        initializeVerifyOtpPane();
        initializeResetPasswordPane();
    }

    private void initializeResetPasswordPane() {
        backToOtpPaneButton.setOnAction(event -> {
            currentScreenString.setValue("VERIFY_OTP_PANE");
        });
        backButton.setOnAction(event -> {
            currentScreenString.setValue("");
        });
        //
    }

    private void initializeVerifyOtpPane() {
        backToEmailInputButton.setOnAction(event -> {
            currentScreenString.setValue("EMAIL_INPUT_PANE");
        });
        verifyOtpButton.setOnAction(event -> {
            // handle

            currentScreenString.setValue("RESET_PASSWORD_PANE");
        });
    }

    private void initializeEmailInputPane() {
        backToMainLoginButton.setOnAction(event -> {
            currentScreenString.setValue("");
        });
        sendOtpButton.setOnAction(event -> {
            // handle
            currentScreenString.setValue("VERIFY_OTP_PANE");
        });

    }

    private void switchScreen() {
        //
        verifyOtpPane.setVisible(false);
        emailInputPane.setVisible(false);
        resetPasswordPane.setVisible(false);
        mainLoginPane.setVisible(false);

        switch (currentScreenString.getValue()) {
            case "VERIFY_OTP_PANE" -> verifyOtpPane.setVisible(true);
            case "EMAIL_INPUT_PANE" -> emailInputPane.setVisible(true);
            case "RESET_PASSWORD_PANE" -> resetPasswordPane.setVisible(true);
            default -> mainLoginPane.setVisible(true);
        }

    }


    // main login pane
    private void initializeMainLoginPane() {
        loginButton.setOnAction(_ -> handleLogin(null));
        forgotPasswordLabel.setOnMouseClicked(mouseEvent -> {
            currentScreenString.setValue("EMAIL_INPUT_PANE");
        });
    }

    public void initManager(final NavController loginManager) {
        loginButton.setOnAction(_ -> handleLogin(loginManager));
    }

    public void handleLogin(NavController loginManager) {
        clearErrorBox();
        if (validateInput()) {
            NhanVien loggedInNhanVien = authorize();
            if (loggedInNhanVien != null) {
                loginManager.authenticated(loggedInNhanVien);
            }
        } else {
            addErrorMessage("Email hoặc mật khẩu đang trống ");
        }
    }

    private boolean validateInput() {
        String userName = user.getText();
        String pass = password.getText();

        return !(userName == null || userName.trim().isEmpty() || pass == null || pass.trim().isEmpty());
    }

    private NhanVien authorize() {
        String userName = user.getText();
        String pass = password.getText();
        NhanVienDAO nhanVienDAO = new NhanVienDAO();

        try {
            ArrayList<TaiKhoan> taiKhoanList = taiKhoanDAO.QueryAll();
            boolean userFound = false;
            for (TaiKhoan taiKhoan : taiKhoanList) {
                if (taiKhoan.getUserName().equals(userName)) {
                    userFound = true;
                    if (taiKhoan.getPassword().equals(pass)) {
                        // Authorized
                        int maNhanVien = taiKhoan.getMaNhanVien();
                        CurrentNVInfor.getInstance().setLoggedInNhanVien(nhanVienDAO.QueryID(maNhanVien));
                        return nhanVienDAO.QueryID(maNhanVien);
                    } else {
                        addErrorMessage("Mật khẩu không đúng ");
                        return null;
                    }
                }
            }
            if (!userFound) {
                addErrorMessage("Tài khoản không tồn tại");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void clearErrorBox() {
        errorBox.getChildren().clear();
    }

    private void addErrorMessage(String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorBox.getChildren().add(errorLabel);
    }

}