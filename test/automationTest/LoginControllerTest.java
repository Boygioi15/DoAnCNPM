package automationTest;

import org.doancnpm.AppStart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.stage.Stage;
import org.testfx.util.WaitForAsyncUtils;

@ExtendWith(org.testfx.framework.junit5.ApplicationExtension.class)
class AppStartTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Manually create an instance of your application
        AppStart appStart = new AppStart();
        appStart.start(stage); // Start the application with the test's stage
    }
//    @Test
//    void testLogin_Admin(FxRobot robot) throws InterruptedException {
//        robot.clickOn("#user").write("admin");
//        robot.clickOn("#password").write("123456");
//        robot.clickOn("#loginButton");
//
//        Thread.sleep(1000);
//
//        // Check if the button with id "openAdminUI" exists
//        Assertions.assertThat(robot.lookup("#openDaiLyButton").queryButton()).isNotNull();
//        Assertions.assertThat(robot.lookup("#openPhieuThuButton").queryButton()).isNotNull();
//        Assertions.assertThat(robot.lookup("#openKhoHangButton").queryButton()).isNotNull();
//        Assertions.assertThat(robot.lookup("#openPhieuNhapButton").queryButton()).isNotNull();
//        Assertions.assertThat(robot.lookup("#openPhieuXuatButton").queryButton()).isNotNull();
//        Assertions.assertThat(robot.lookup("#openBaoCaoButton").queryButton()).isNotNull();
//        Assertions.assertThat(robot.lookup("#openQuyDinhButton").queryButton()).isNotNull();
//
//    }
//
//    @Test
//    void testLogin_Staff(FxRobot robot) throws InterruptedException {
//
//        WaitForAsyncUtils.waitForFxEvents();
//
//        robot.clickOn("#user").write("user");
//        robot.clickOn("#password").write("123456");
//        robot.clickOn("#loginButton");
//
//        Thread.sleep(1000);
//        // Check if the button with id "openAdminUI" exists
//        Assertions.assertThat(robot.lookup("#openDaiLyButton").queryButton()).isNotNull();
//        Assertions.assertThat(robot.lookup("#openPhieuThuButton").queryButton()).isNotNull();
//        Assertions.assertThat(robot.lookup("#openKhoHangButton").queryButton()).isNotNull();
//        Assertions.assertThat(robot.lookup("#openPhieuNhapButton").queryButton()).isNotNull();
//        Assertions.assertThat(robot.lookup("#openPhieuXuatButton").queryButton()).isNotNull();
//
//        Assertions.assertThat(robot.lookup("#openBaoCaoButton").tryQuery()).isEmpty();
//        Assertions.assertThat(robot.lookup("#openQuyDinhButton").tryQuery()).isEmpty();
//    }

    @Test
    void testAuthenticationFlow(FxRobot robot) throws InterruptedException {

        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        Thread.sleep(1000);

        // Check if the button with id "openAdminUI" exists
        Assertions.assertThat(robot.lookup("#openDaiLyButton").queryButton()).isNotNull();
        Assertions.assertThat(robot.lookup("#openPhieuThuButton").queryButton()).isNotNull();
        Assertions.assertThat(robot.lookup("#openKhoHangButton").queryButton()).isNotNull();
        Assertions.assertThat(robot.lookup("#openPhieuNhapButton").queryButton()).isNotNull();
        Assertions.assertThat(robot.lookup("#openPhieuXuatButton").queryButton()).isNotNull();
        Assertions.assertThat(robot.lookup("#openBaoCaoButton").queryButton()).isNotNull();
        Assertions.assertThat(robot.lookup("#openQuyDinhButton").queryButton()).isNotNull();

        robot.clickOn("#userNameMenuButton");
        robot.clickOn("#dangXuatButton");

        robot.clickOn("#user").write("user");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        Thread.sleep(1000);
        // Check if the button with id "openAdminUI" exists
        Assertions.assertThat(robot.lookup("#openDaiLyButton").queryButton()).isNotNull();
        Assertions.assertThat(robot.lookup("#openPhieuThuButton").queryButton()).isNotNull();
        Assertions.assertThat(robot.lookup("#openKhoHangButton").queryButton()).isNotNull();
        Assertions.assertThat(robot.lookup("#openPhieuNhapButton").queryButton()).isNotNull();
        Assertions.assertThat(robot.lookup("#openPhieuXuatButton").queryButton()).isNotNull();

        Assertions.assertThat(robot.lookup("#openBaoCaoButton").tryQuery()).isEmpty();
        Assertions.assertThat(robot.lookup("#openQuyDinhButton").tryQuery()).isEmpty();

        Thread.sleep(1000);
        robot.clickOn("#userNameMenuButton");
        robot.clickOn("#dangXuatButton");
    }



}
