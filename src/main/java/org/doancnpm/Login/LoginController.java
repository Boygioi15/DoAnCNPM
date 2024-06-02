package org.doancnpm.Login;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.DAO.TaiKhoanDAO;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.TaiKhoan;
import org.doancnpm.NavController;
import org.doancnpm.Ultilities.CurrentNVInfor;
import org.doancnpm.Ultilities.PopDialog;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controls the login screen
 */
public class LoginController implements Initializable {
    public AnchorPane verifyOtpPane;
    public AnchorPane resetPasswordPane;
    public AnchorPane emailInputPane;
    public VBox mainLoginPane;
    int randomCode;
    private final int OTP_VALIDITY_SECONDS = 120;
    private long otpSentTime;


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
    @FXML
    public VBox errorVailidateEmailBox;


    // verifyOtpPane
    @FXML
    public Button backToEmailInputButton;
    @FXML
    public TextField otpText;
    @FXML
    public Button verifyOtpButton;
    @FXML
    public Label reSendOtpLabel;
    public VBox errorOtpBox;

    //resetPasswordPane
    @FXML
    public Button backToOtpPaneButton;
    public TextField reEnterPasswordText;
    public TextField enterPasswordText;
    public VBox errorPasswordBox;
    public VBox errorConfirmPasswordBox;
    public Button confirmButton;

    StringProperty currentScreenString = new SimpleStringProperty("");
    private TaiKhoanDAO taiKhoanDAO = TaiKhoanDAO.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearErrorBox(errorBox);
        currentScreenString.addListener((observableValue, s, t1) -> {
            switchScreen();
        });
        initializeMainLoginPane();
        initializeEmailInputPane();
        initializeVerifyOtpPane();
        initializeResetPasswordPane();
    }


    private void switchScreen() {
        //
        verifyOtpPane.setVisible(false);
        emailInputPane.setVisible(false);
        resetPasswordPane.setVisible(false);
        mainLoginPane.setVisible(false);

        switch (currentScreenString.getValue()) {
            case "VERIFY_OTP_PANE" -> {
                verifyOtpPane.setVisible(true);
                reSendOtpLabel.setDisable(true);
                startCountdown(reSendOtpLabel, OTP_VALIDITY_SECONDS);
            }
            case "EMAIL_INPUT_PANE" -> emailInputPane.setVisible(true);
            case "RESET_PASSWORD_PANE" -> resetPasswordPane.setVisible(true);
            default -> mainLoginPane.setVisible(true);
        }

    }


    // main login pane
    private void initializeMainLoginPane() {
      //  loginButton.setOnAction(_ -> handleLogin(null));
        forgotPasswordLabel.setOnMouseClicked(mouseEvent -> {
            currentScreenString.setValue("EMAIL_INPUT_PANE");
        });
    }

    public void initManager(final NavController loginManager) {
        loginButton.setOnAction(_ -> handleLogin(loginManager));
    }

    public void handleLogin(NavController loginManager) {
        clearErrorBox(errorBox);
        if (validateInput()) {
            NhanVien loggedInNhanVien = authorize();
            if (loggedInNhanVien != null) {
                loginManager.authenticated(loggedInNhanVien);
            }
        } else {
            addErrorMessage(errorBox, "Email hoặc mật khẩu đang trống ");
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
        try {
            ArrayList<TaiKhoan> taiKhoanList = TaiKhoanDAO.getInstance().QueryAll();
            boolean userFound = false;
            for (TaiKhoan taiKhoan : taiKhoanList) {
                if (taiKhoan.getUserName().equals(userName)) {
                    userFound = true;
                    if (taiKhoan.getPassword().equals(pass)) {
                        // Authorized
                        int maNhanVien = taiKhoan.getMaNhanVien();
                        CurrentNVInfor.getInstance().setTaiKhoanOfNhanien(taiKhoan);
                        CurrentNVInfor.getInstance().setLoggedInNhanVien(NhanVienDAO.getInstance().QueryID(maNhanVien));
                        return NhanVienDAO.getInstance().QueryID(maNhanVien);
                    } else {
                        addErrorMessage(errorBox, "Mật khẩu không đúng ");
                        return null;
                    }
                }
            }
            if (!userFound) {
                addErrorMessage(errorBox, "Tài khoản không tồn tại");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void clearErrorBox(VBox errorBox) {
        errorBox.getChildren().clear();
    }

    private void addErrorMessage(VBox errorBox, String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorBox.getChildren().add(errorLabel);
    }

    // email input pane
    private void initializeEmailInputPane() {
        clearErrorBox(errorVailidateEmailBox);
        backToMainLoginButton.setOnAction(event -> {
            currentScreenString.setValue("");
        });
        sendOtpButton.setOnAction(event -> {

            handleSendOtp();
        });
    }

    private void handleSendOtp() {
        clearErrorBox(errorVailidateEmailBox);
        if (!validateEmailInput()) {
            return;
        }
        try {
            // Generate a random code
            Random rand = new Random();
            randomCode = rand.nextInt(900000) + 100000;


            // Email configuration
            String host = "smtp.gmail.com";
            String user = "managementagentse@gmail.com";
            String pass = "sqnn uwgv xoxh uhfv";
            String to = emailText.getText();
            String subject = "Resetting Code";
            String message = "Your reset code is " + randomCode;

            Properties props = new Properties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.required", "true");

            // Create a session with an authenticator
            Session mailSession = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass);
                }
            });

            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(user));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setText(message);

            Transport.send(msg);

            otpSentTime = System.currentTimeMillis() / 1000;
            currentScreenString.setValue("VERIFY_OTP_PANE");

        } catch (MessagingException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private boolean validateEmailInput() {
        String email = emailText.getText();


        if (email.isEmpty()) {
            addErrorMessage(errorVailidateEmailBox, "Vui lòng nhập vào email");
            return false;
        }


        if (!isValidEmailFormat(email)) {
            addErrorMessage(errorVailidateEmailBox, "Email không đúng định dạng");
            return false;
        }

        try {
            ArrayList<NhanVien> listNV = NhanVienDAO.getInstance().QueryAll();
            boolean userFound = false;
            for (NhanVien nhanVien : listNV) {
                if (nhanVien.getEmail().equals(email)) {
                    userFound = true;
                    break;
                }
            }
            if (!userFound) {
                addErrorMessage(errorVailidateEmailBox, "Tài khoản không tồn tại");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage(errorVailidateEmailBox, "Có lỗi xảy ra khi kiểm tra tài khoản. Vui lòng thử lại.");
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

    // otp pane
    private void initializeVerifyOtpPane() {
        clearErrorBox(errorOtpBox);

        backToEmailInputButton.setOnAction(event -> {
            currentScreenString.setValue("EMAIL_INPUT_PANE");
        });
        verifyOtpButton.setOnAction(event -> {
            // handle
            clearErrorBox(errorOtpBox);
            handleConfirmOtp();
        });
        reSendOtpLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                handleSendOtp();
                startCountdown(reSendOtpLabel,OTP_VALIDITY_SECONDS);
            }
        });
    }

    public void handleConfirmOtp() {
        if (!validateOtp()) {
            return;
        }
        currentScreenString.setValue("RESET_PASSWORD_PANE");
    }

    private boolean validateOtp() {
        String otp = otpText.getText();
        if (otp.length() != 6) {
            addErrorMessage(errorOtpBox, "Mã OTP phải là mã có 6 chữ số");
            return false;
        }
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - otpSentTime >= OTP_VALIDITY_SECONDS) {
            addErrorMessage(errorOtpBox, "Mã OTP không chính xác!");
            return false;
        }
        if (!otp.equals(Integer.toString(randomCode))) {
            addErrorMessage(errorOtpBox, "Mã OTP không chính xác!");
            return false;
        }
        return true;
    }

    private void startCountdown(Label countdownLabel, int seconds) {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        final int[] timeRemaining = {seconds};

        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(1),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        timeRemaining[0]--;
                        countdownLabel.setText("Gửi lại mã OTP? (" + timeRemaining[0] + "s)");

                        if (timeRemaining[0] <= 0) {
                            timeline.stop();
                            countdownLabel.setText("Gửi lại mã OTP");
                            reSendOtpLabel.setDisable(false);
                        }
                    }
                }
        );

        timeline.getKeyFrames().add(keyFrame);
        timeline.playFromStart();
    }


    // reset password pane
    private void initializeResetPasswordPane() {
        clearErrorBox(errorConfirmPasswordBox);
        clearErrorBox(errorPasswordBox);
        backToOtpPaneButton.setOnAction(event -> {
            currentScreenString.setValue("VERIFY_OTP_PANE");
        });
        confirmButton.setOnAction(event -> {
            handleConfirm();
        });

    }
    private void handleConfirm(){
        clearErrorBox(errorConfirmPasswordBox);
        clearErrorBox(errorPasswordBox);
        if(!validatePassword()){
            return;
        }
        try{
            NhanVienDAO.getInstance().updatePasswordByEmail(emailText.getText(),enterPasswordText.getText());
            PopDialog.popSuccessDialog("Đổi mật khẩu thành công");
            currentScreenString.setValue("");
        }
        catch (Exception e)
        {
            PopDialog.popSuccessDialog("Đổi mật khẩu that bai");
        }

    }


    private boolean validatePassword() {
        String password = enterPasswordText.getText();
        String confirmPassword = reEnterPasswordText.getText();

        if (password.isEmpty()) {
            addErrorMessage(errorPasswordBox, "Vui lòng nhập mật khẩu");
            return false;
        }

        if (confirmPassword.isEmpty()) {
            addErrorMessage(errorConfirmPasswordBox, "Vui lòng nhập lại mật khẩu");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            addErrorMessage(errorConfirmPasswordBox, "Mật khẩu không khớp");
            return false;
        }

        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        if (!password.matches(passwordRegex)) {
            addErrorMessage(errorPasswordBox, "Mật khẩu phải có ít nhất 8 ký tự và bao gồm cả chữ và số");
            return false;
        }

        return true;
    }

}