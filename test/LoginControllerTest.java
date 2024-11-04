import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.doancnpm.Login.LoginController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {
    private LoginController loginController;

    @BeforeAll
    static void initToolkit() {
        if (!Platform.isFxApplicationThread()) {
            new Thread(() -> Platform.startup(() -> {
            })).start();
        }
    }

    @BeforeEach
    void setUp() {
        loginController = new LoginController();

        // Khởi tạo thủ công tất cả các thuộc tính
        loginController.user = new TextField();
        loginController.password = new TextField();
        loginController.otpText = new TextField();
        loginController.enterPasswordText = new TextField();
        loginController.reEnterPasswordText = new TextField();

        loginController.errorBox = new VBox();
        loginController.errorConfirmPasswordBox = new VBox();
        loginController.errorOtpBox = new VBox();

        loginController.errorBox.getChildren().add(new Label());
      //  loginController.errorConfirmPasswordBox.getChildren().add(new Label());
      //  loginController.errorOtpBox.getChildren().add(new Label());
    }


    @Test
    void validateInputTest() {
        // Valid input
        Platform.runLater(() -> {
            loginController.user.setText("testUser");
            loginController.password.setText("testPass");
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(loginController.validateInput(), "Expected valid input to return true");

        // Empty username
        Platform.runLater(() -> {
            loginController.user.setText("");
            loginController.password.setText("testPass");
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(loginController.validateInput(), "Expected empty username to return false");

        // Empty password
        Platform.runLater(() -> {
            loginController.user.setText("testUser");
            loginController.password.setText("");
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(loginController.validateInput(), "Expected empty password to return false");

        // Both fields empty
        Platform.runLater(() -> {
            loginController.user.setText("");
            loginController.password.setText("");
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(loginController.validateInput(), "Expected empty fields to return false");
    }

//    @Test
//    void testHandleConfirmOtp_Successful() {
//        Platform.runLater(() -> {
//            loginController.randomCode = 321241;
//            loginController.otpText.setText("321241");
//            loginController.otpSentTime = System.currentTimeMillis() / 1000 - 60; // Simulate OTP sent 60 seconds ago
//
//            loginController.handleConfirmOtp();
//            assertEquals("RESET_PASSWORD_PANE", loginController.currentScreenString.getValue());
//
//            loginController.randomCode = 321241;
//            loginController.otpText.setText("admin");
//            loginController.otpSentTime = System.currentTimeMillis() / 1000 - 60; // Simulate OTP sent 60 seconds ago
//
//            loginController.handleConfirmOtp();
//            assertEquals("RESET_PASSWORD_PANE", loginController.currentScreenString.getValue());
//
//            loginController.randomCode = 321241;
//            loginController.otpText.setText("");
//            loginController.otpSentTime = System.currentTimeMillis() / 1000 - 60;
//
//            loginController.handleConfirmOtp();
//            assertEquals("RESET_PASSWORD_PANE", loginController.currentScreenString.getValue());
//
//
//            loginController.randomCode = 321241;
//            loginController.otpText.setText("231232");
//            loginController.otpSentTime = System.currentTimeMillis() / 1000 - 60;
//
//            loginController.handleConfirmOtp();
//            assertEquals("RESET_PASSWORD_PANE", loginController.currentScreenString.getValue());
//        });
//    }

    @Test
    void testValidatePassword_Successful() {
        Platform.runLater(() -> {
            loginController.enterPasswordText.setText("Password1");
            loginController.reEnterPasswordText.setText("Password1");

            boolean isValid = loginController.validatePassword();

            assertTrue(isValid, "Expected password validation to be successful");
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testValidatePassword_Unmatched() {
        Platform.runLater(() -> {
            loginController.enterPasswordText.setText("Password1");
            loginController.reEnterPasswordText.setText("Password2");

            boolean isValid = loginController.validatePassword();

            assertFalse(isValid, "Expected password validation to fail");
            assertEquals("Mật khẩu không khớp", ((Label) loginController.errorConfirmPasswordBox.getChildren().get(0)).getText());
        });

        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testValidatePassword_ReEnterEmpty() {
        Platform.runLater(() -> {
            loginController.enterPasswordText.setText("Password113");
            loginController.reEnterPasswordText.setText("");

            boolean isValid = loginController.validatePassword();

            assertFalse(isValid, "Expected re-enter password validation to fail");
            assertEquals("Vui lòng nhập lại mật khẩu", ((Label) loginController.errorConfirmPasswordBox.getChildren().get(0)).getText());
        });

        WaitForAsyncUtils.waitForFxEvents();
    }




    /// OTP
    @Test
    void testHandleConfirmOtp_ValidOtp() {
        Platform.runLater(() -> {
            loginController.randomCode = 321241;
            loginController.otpText.setText("321241");
            loginController.otpSentTime = System.currentTimeMillis() / 1000 - 30; // OTP sent within validity period

            loginController.handleConfirmOtp();

            assertEquals("RESET_PASSWORD_PANE", loginController.currentScreenString.getValue());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testHandleConfirmOtp_InvalidLengthOtp() {
        Platform.runLater(() -> {
            loginController.otpText.setText("321241");

            boolean isValid = loginController.validateOtp();

            assertFalse(isValid, "Expected OTP validation to fail for invalid length");
            assertEquals("Mã OTP không chính xác!", ((Label) loginController.errorOtpBox.getChildren().get(0)).getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testHandleConfirmOtp_ExpiredOtp() {
        Platform.runLater(() -> {
            loginController.randomCode = 321241;
            loginController.otpText.setText("admin");
            loginController.otpSentTime = System.currentTimeMillis() / 1000 - 300; // OTP đã quá hạn

            boolean isValid = loginController.validateOtp();

            assertFalse(isValid, "Expected OTP validation to fail for expired OTP");
            assertEquals("Mã OTP phải là mã có 6 chữ số", ((Label) loginController.errorOtpBox.getChildren().get(0)).getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testHandleConfirmOtp_IncorrectOtp() {
        Platform.runLater(() -> {
            loginController.randomCode = 123456;
            loginController.otpText.setText("654321"); // OTP không khớp

            boolean isValid = loginController.validateOtp();

            assertFalse(isValid, "Expected OTP validation to fail for incorrect OTP");
            assertEquals("Mã OTP không chính xác!", ((Label) loginController.errorOtpBox.getChildren().get(0)).getText());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

}
