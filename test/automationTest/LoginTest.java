package automationTest;

import javafx.stage.Stage;
import org.doancnpm.AppStart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(org.testfx.framework.junit5.ApplicationExtension.class)
class LoginTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Manually create an instance of your application
        AppStart appStart = new AppStart();
        appStart.startTest(stage); // Start the application with the test's stage
    }
    @Test
    void Login_Admin_Success(FxRobot robot) throws SQLException, InterruptedException {
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        Assertions.assertThat(robot.lookup("#openDaiLyButton").queryButton()).isNotNull();

        Thread.sleep(2000);
    }
    @Test
    void Login_Staff_Success(FxRobot robot) throws SQLException, InterruptedException {
        robot.clickOn("#user").write("user");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        Assertions.assertThat(robot.lookup("#openDaiLyButton").queryButton()).isNotNull();
        Thread.sleep(2000);
    }
    @Test
    void Login_User_NotFound(FxRobot robot) throws SQLException, InterruptedException {
        robot.clickOn("#user").write("abc");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        Assertions.assertThat(robot.lookup("#error-label").queryLabeled()).isNotNull();
        Thread.sleep(2000);
    }
    @Test
    void Login_WrongPass(FxRobot robot) throws SQLException, InterruptedException {
        robot.clickOn("#user").write("user");
        robot.clickOn("#password").write("1234567");
        robot.clickOn("#loginButton");

        Assertions.assertThat(robot.lookup("#error-label").queryLabeled()).isNotNull();
        Thread.sleep(2000);
    }
    @Test
    void Login_Blank(FxRobot robot) throws SQLException, InterruptedException {
        robot.clickOn("#loginButton");
        Assertions.assertThat(robot.lookup("#error-label").queryLabeled()).isNotNull();
        Thread.sleep(2000);
    }
}
