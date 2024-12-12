package automationTest;

import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.doancnpm.AppStart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(org.testfx.framework.junit5.ApplicationExtension.class)
class GoodTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Manually create an instance of your application
        AppStart appStart = new AppStart();
        appStart.start(stage); // Start the application with the test's stage
    }

    @Test
    void Add_new_good(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin3");
        robot.clickOn("#password").write("thinh123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openKhoHangButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        robot.clickOn("#tenMHTextField").write("Sữa Milo");

        // Fill textfield
        robot.clickOn("#dvtComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#donGiaNhapTextField").write("7000");
        robot.clickOn("#saveButton");

        // Kiểm tra dialog xuất hiện
        DialogPane dialogPane = robot.lookup("#successDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);

        // Kiểm tra nội dung thông báo
        assertEquals("Thêm mới mặt hàng thành công", dialogPane.getHeaderText());
        //  assertEquals("Thành công!", dialogPane.getContentText());
        // Click OK button
        robot.clickOn(".button");
    }

}

