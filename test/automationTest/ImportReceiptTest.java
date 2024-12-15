package automationTest;

import TestUtilities.TestFunctions;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.doancnpm.AppStart;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(org.testfx.framework.junit5.ApplicationExtension.class)
class ImportReceiptTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Manually create an instance of your application
        AppStart appStart = new AppStart();
        appStart.startTest(stage); // Start the application with the test's stage
    }
    @AfterEach
    void CleanUp(){
        TestFunctions.DeleteAll("ChiTietPhieuNhap");
        TestFunctions.DeleteAll("PhieuNhaphang");
        TestFunctions.DeleteAll("MatHang");
    }
    @Test
    void Add_new_ImportReceipt_Success(FxRobot robot) throws InterruptedException, SQLException {
        TestFunctions.InsertTestMatHang2();
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openPhieuNhapButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(200);

        // Fill textfield
        robot.clickOn("#nccTextField_add").write("Nhà cung cấp Thủ Đức");
        Thread.sleep(200);
        robot.clickOn("#mhComboBox" + 2);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +2).write("2");
        robot.clickOn("#themCTPNButton");
        robot.clickOn("#mhComboBox" + 3);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +3).write("2");
        robot.clickOn("#saveButton");

        DialogPane dialogPane = robot.lookup("#successDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        robot.clickOn("#okButton");
    }
    @Test
    void Add_new_ImportReceipt_DuplicateItem(FxRobot robot) throws InterruptedException, SQLException {
        TestFunctions.InsertTestMatHang();
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openPhieuNhapButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(200);

        // Fill textfield
        robot.clickOn("#nccTextField_add").write("Nhà cung cấp Thủ Đức");
        Thread.sleep(200);
        robot.clickOn("#mhComboBox" + 2);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +2).write("2");
        robot.clickOn("#themCTPNButton");
        robot.clickOn("#mhComboBox" + 3);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +3).write("2");
        robot.clickOn("#saveButton");

        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        robot.clickOn("#okButton");
    }

    @Test
    void Add_new_ImportReceipt_InvalidAmountFormat(FxRobot robot) throws InterruptedException, SQLException {
        TestFunctions.InsertTestMatHang2();
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openPhieuNhapButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(200);

        // Fill textfield
        robot.clickOn("#nccTextField_add").write("Nhà cung cấp Thủ Đức");
        Thread.sleep(200);
        robot.clickOn("#mhComboBox" + 2);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#slTextField" +2).write("a");
        robot.clickOn("#saveButton");
        // Kiểm tra dialog xuất hiện
        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        robot.clickOn("#okButton");
    }
    @Test
    void Add_new_ImportReceipt_LeaveNCCBlank(FxRobot robot) throws InterruptedException, SQLException {
        TestFunctions.InsertTestMatHang2();
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openPhieuNhapButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(200);
        robot.clickOn("#mhComboBox" + 2);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        // Fill textfield
        robot.clickOn("#saveButton");
        // Kiểm tra dialog xuất hiện
        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        robot.clickOn("#okButton");
    }
    @Test
    void Add_new_ImportReceipt_LeaveItemBlank(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openPhieuNhapButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(200);

        // Fill textfield
        robot.clickOn("#nccTextField_add").write("Nhà cung cấp Thủ Đức");
        robot.clickOn("#saveButton");
        // Kiểm tra dialog xuất hiện
        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        robot.clickOn("#okButton");
    }
}

