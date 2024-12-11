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

class SendOTP_ValidateOTP extends ApplicationTest {
    private LoginController controller;

    @BeforeAll
    static void initFX() {
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
        // Giả lập các giá trị cho randomCode và otpSentTime
        controller.randomCode = 342514; // OTP sinh ngẫu nhiên
        controller.otpSentTime = System.currentTimeMillis() / 1000; // Thời gian gửi OTP
    }

    @Test
    void UTC001() {
        Platform.runLater(() -> {
            controller.otpText.setText("342514");
            boolean isValid = controller.validateOtp();
            assertTrue(isValid, "OTP hợp lệ nhưng kiểm tra thất bại.");
        });
    }

    @Test
    void UTC002() {
        Platform.runLater(() -> {
            controller.otpText.setText("111111");
            boolean isValid = controller.validateOtp();
            assertFalse(isValid, "OTP sai nhưng kiểm tra lại thành công.");
        });
    }

    @Test
    void UTC003() {
        Platform.runLater(() -> {
            controller.otpText.setText("34251"); // OTP chỉ có 5 ký tự
            boolean isValid = controller.validateOtp();
            assertFalse(isValid, "OTP ngắn hơn 6 ký tự nhưng kiểm tra lại thành công.");
        });
    }

    @Test
    void UTC004() {
        Platform.runLater(() -> {
            controller.otpSentTime = (System.currentTimeMillis() / 1000) - 121;
            controller.otpText.setText("342514");
            boolean isValid = controller.validateOtp();
            assertFalse(isValid, "OTP đã hết hạn nhưng kiểm tra lại thành công.");
        });
    }
}
