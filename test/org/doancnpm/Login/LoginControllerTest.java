package org.doancnpm.Login;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest extends ApplicationTest {
    private LoginController controller;
    @BeforeAll
    static void initFX() {
        // Khởi động JavaFX chỉ một lần
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login/LoginUI.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
    }
    @BeforeEach
    void setUp() {
        assertNotNull(controller.emailText, "existPhong@gmail.com");
    }
    @Test
    void isValidEmailFormat() {

        Platform.runLater(()->{
            controller.emailText.setText("existPhong@gmail.com");
            Boolean bl = controller.isValidEmailFormat(controller.emailText.getText());
            assertTrue(bl);
        });

    }


}