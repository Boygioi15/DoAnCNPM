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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(org.testfx.framework.junit5.ApplicationExtension.class)
class BillTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        AppStart appStart = new AppStart();
        appStart.startTest(stage);
    }
    @AfterEach
    void CleanUp(){
        TestFunctions.DeleteAll("PhieuThuTien");
        TestFunctions.DeleteAll("DaiLy");
    }
    @Test
    void Add_new_bill_success(FxRobot robot) throws InterruptedException, SQLException {
        // Insert a DAILY and get its ID
        TestFunctions.InsertDailyWithNoHienTai(5000000); // Insert with 5M

        // Perform the test
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openPhieuThuButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");

        // Fill the text fields
        robot.clickOn("#dlComboBox");

        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#soTienThuTextField").write("100000"); // 1k

        Thread.sleep(300);

        robot.clickOn("#saveButton");

        DialogPane dialogPane = robot.lookup("#successDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);

        robot.clickOn("#okButton");
        Thread.sleep(1000);
    }


    @Test
    void Add_new_bill_excess_amount(FxRobot robot) throws InterruptedException, SQLException {
        // Insert a DAILY and get its ID
        TestFunctions.InsertDailyWithNoHienTai(5000000); // Insert with 5M

        // Perform the test
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openPhieuThuButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");

        // Fill the text fields
        robot.clickOn("#dlComboBox");

        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#soTienThuTextField").write("100000000"); // 1k

        Thread.sleep(300);

        robot.clickOn("#saveButton");

        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);

        robot.clickOn("#okButton");
        Thread.sleep(1000);
    }
}
