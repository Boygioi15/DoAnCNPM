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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(org.testfx.framework.junit5.ApplicationExtension.class)
class GoodTest extends ApplicationTest {

    @AfterEach
    void CleanUp(){
        TestFunctions.DeleteAll("MatHang");
    }
    @Override
    public void start(Stage stage) throws Exception {
        // Manually create an instance of your application
        AppStart appStart = new AppStart();
        appStart.startTest(stage); // Start the application with the test's stage
    }

    @Test
    void Add_new_good_success(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openKhoHangButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        robot.clickOn("#tenMHTextField_add").write("Mặt hàng 1");

        // Fill textfield
        robot.clickOn("#dvtComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#donGiaNhapTextField_add").write("7000");
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
    @Test
    void Add_new_good_duplicateName(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openKhoHangButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        robot.clickOn("#tenMHTextField_add").write("Mặt hàng 1");

        // Fill textfield
        robot.clickOn("#dvtComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#donGiaNhapTextField_add").write("7000");
        robot.clickOn("#saveButton");

        // Kiểm tra dialog xuất hiện
        DialogPane dialogPane = robot.lookup("#successDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        robot.clickOn("#okButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        robot.clickOn("#tenMHTextField_add").write("Mặt hàng 1");

        // Fill textfield
        robot.clickOn("#dvtComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#donGiaNhapTextField_add").write("7000");
        robot.clickOn("#saveButton");

        // Kiểm tra dialog xuất hiện
        dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);

        robot.clickOn("#okButton");
    }
    @Test
    void Add_new_good_noName(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openKhoHangButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");

        // Fill textfield
        robot.clickOn("#dvtComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#donGiaNhapTextField_add").write("7000");
        robot.clickOn("#saveButton");

        // Kiểm tra dialog xuất hiện
        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        robot.clickOn("#okButton");
    }
    @Test
    void Add_new_good_noDVT(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openKhoHangButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");

        robot.clickOn("#tenMHTextField_add").write("Mặt hàng 1");
        robot.clickOn("#donGiaNhapTextField_add").write("7000");
        robot.clickOn("#saveButton");

        // Kiểm tra dialog xuất hiện
        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        robot.clickOn("#okButton");
    }

}

