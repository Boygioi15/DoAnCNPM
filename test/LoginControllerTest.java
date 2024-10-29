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
import static org.mockito.Mockito.mock;

class LoginControllerTest {
    private LoginController loginController;

    @BeforeAll
    static void initToolkit() {
        if (!Platform.isFxApplicationThread()) {
            new Thread(() -> Platform.startup(() -> {})).start();
        }
    }

    @BeforeEach
    void setUp() {
        loginController = new LoginController();
        loginController.user = new TextField();
        loginController.password = new TextField();
        loginController.otpText = new TextField();
        loginController.enterPasswordText = new TextField();
        loginController.reEnterPasswordText = new TextField();
        loginController.errorBox = new VBox();
    loginController.errorConfirmPasswordBox = new VBox();
        // Additional initialization can be added here if necessary
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

    @Test
    void testHandleConfirmOtp_Successful() {
        Platform.runLater(() -> {
            // Setup valid OTP
            loginController.randomCode = 123456;
            loginController.otpText.setText("123456");
            loginController.otpSentTime = System.currentTimeMillis() / 1000 - 60; // Simulate OTP sent 60 seconds ago

            loginController.handleConfirmOtp();

            // Assert that we switched to the reset password pane
            assertEquals("RESET_PASSWORD_PANE", loginController.currentScreenString.getValue());
        });
    }

    @Test
    void testValidatePassword_Successful() {
        Platform.runLater(() -> {
            loginController.enterPasswordText.setText("Password1");
            loginController.reEnterPasswordText.setText("Password1");

            boolean isValid = loginController.validatePassword();

            assertTrue(isValid, "Expected password validation to be successful");
        });
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
    }
}
