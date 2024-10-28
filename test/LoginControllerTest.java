import javafx.application.Platform;
import javafx.scene.control.TextField;
import org.doancnpm.Login.LoginController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        Platform.startup(() -> {
            loginController = new LoginController();
            loginController.user = new TextField();
            loginController.password = new TextField();
        });
    }

    @Test
    public void validateInputTest() {
        Platform.runLater(() -> {
            loginController.user.setText("testUser");
            loginController.password.setText("testPass");
            assertTrue(loginController.validateInput(), "Expected valid input to return true");

            loginController.user.setText("");
            loginController.password.setText("testPass");
            assertFalse(loginController.validateInput(), "Expected empty username to return false");

            loginController.user.setText("testUser");
            loginController.password.setText("");
            assertFalse(loginController.validateInput(), "Expected empty password to return false");

            loginController.user.setText("");
            loginController.password.setText("");
            assertFalse(loginController.validateInput(), "Expected empty fields to return false");
        });
    }
}
