package automationTest;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.doancnpm.AppStart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(org.testfx.framework.junit5.ApplicationExtension.class)
class AgentTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Manually create an instance of your application
        AppStart appStart = new AppStart();
        appStart.start(stage); // Start the application with the test's stage
    }

    @Test
    void Add_new_agent(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openDaiLyButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(300);

        // fill textfield
        robot.clickOn("#quanComboBox");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#loaiDaiLyComboBox");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#tenDaiLyTextField").write("Dai li 1");
        robot.clickOn("#diaChiTextField").write("Thu duc");
        robot.clickOn("#emailTextField").write("thuduc@gmail.com");
        robot.clickOn("#dienThoaiTextField").write("0848761702");
        robot.clickOn("#ghiChuTextArea").write("hoat dong lau dai");
        Thread.sleep(300);

        robot.clickOn("#saveButton");

//        Stage dialogStage = (Stage) robot.window("Thông báo!");
//        assertThat(dialogStage).isNotNull();
//
//        // Kiểm tra nội dung dialog
//        Label headerLabel = robot.lookup(".dialog-header-label").queryAs(Label.class);
//        assertThat(headerLabel.getText()).contains("Thêm mới đại lý thành công");
    }


}
