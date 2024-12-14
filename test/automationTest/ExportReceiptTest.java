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
class ExportReceiptTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Manually create an instance of your application
        AppStart appStart = new AppStart();
        appStart.start(stage); // Start the application with the test's stage
    }
    @Test
    void Add_new_ExportReceipt_Case1(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin3");
        robot.clickOn("#password").write("thinh123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openPhieuXuatButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(200);

        // Fill textfield
        robot.clickOn("#dlComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        Thread.sleep(200);
        robot.clickOn("#mhComboBox" + 2);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +2).write("2");
        robot.clickOn("#themCTPXButton");
        robot.clickOn("#mhComboBox" + 3);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +3).write("2");
        robot.clickOn("#saveButton");

        // Kiểm tra dialog xuất hiện
        DialogPane dialogPane = robot.lookup("#successDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);

        // Kiểm tra nội dung thông báo
        assertEquals("Thêm mới phiếu xuất thành công", dialogPane.getHeaderText());
        //  assertEquals("Thành công!", dialogPane.getContentText());
        // Click OK button
        robot.clickOn(".button");
    }
    @Test
    void Add_new_ExportReceipt_Case2(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin3");
        robot.clickOn("#password").write("thinh123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openPhieuXuatButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(200);

        // Fill textfield
        robot.clickOn("#dlComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        Thread.sleep(200);
        robot.clickOn("#mhComboBox" + 2);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +2).write("2");
        robot.clickOn("#themCTPXButton");
        robot.clickOn("#mhComboBox" + 3);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +3).write("2");
        robot.clickOn("#saveButton");

        // Kiểm tra dialog xuất hiện
        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);

        // Kiểm tra nội dung thông báo
        assertEquals("Thêm mới phiếu xuất thất bại", dialogPane.getHeaderText());
        //  assertEquals("Thành công!", dialogPane.getContentText());
        // Click OK button
        robot.clickOn(".button");
    }
    @Test
    void Add_new_ExportReceipt_Case3(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin3");
        robot.clickOn("#password").write("thinh123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openPhieuXuatButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(200);

        // Fill textfield
        robot.clickOn("#dlComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        Thread.sleep(200);
        robot.clickOn("#mhComboBox" + 2);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +2).write("a");
        robot.clickOn("#themCTPXButton");
        robot.clickOn("#mhComboBox" + 3);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +3).write("2");
        robot.clickOn("#saveButton");

        // Kiểm tra dialog xuất hiện
        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);

        // Kiểm tra nội dung thông báo
        assertEquals("Thêm mới phiếu xuất thất bại", dialogPane.getHeaderText());
        //  assertEquals("Thành công!", dialogPane.getContentText());
        // Click OK button
        robot.clickOn(".button");
    }
    @Test
    void Add_new_ExportReceipt_Case4(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin3");
        robot.clickOn("#password").write("thinh123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openPhieuXuatButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(200);

        // Fill textfield
        robot.clickOn("#dlComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        Thread.sleep(200);
        robot.clickOn("#mhComboBox" + 2);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +2).write("100000000");
        robot.clickOn("#themCTPXButton");
        robot.clickOn("#mhComboBox" + 3);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +3).write("2");
        robot.clickOn("#saveButton");

        // Kiểm tra dialog xuất hiện
        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);

        // Kiểm tra nội dung thông báo
        assertEquals("Thêm mới phiếu xuất thất bại", dialogPane.getHeaderText());
        //  assertEquals("Thành công!", dialogPane.getContentText());
        // Click OK button
        robot.clickOn(".button");
    }
}

