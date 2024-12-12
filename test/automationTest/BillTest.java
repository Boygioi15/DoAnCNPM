package automationTest;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.doancnpm.AppStart;
import org.doancnpm.DAO.DaiLyDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.SQLException;

@ExtendWith(org.testfx.framework.junit5.ApplicationExtension.class)
class BillTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        AppStart appStart = new AppStart();
        appStart.start(stage);
    }

    @Test
    void Add_new_bill_success(FxRobot robot) throws InterruptedException, SQLException {
        DaiLyDAO.getInstance().InsertWithNoHienTai(5000000); // 10 trieu
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openPhieuThuButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(300);

        // fill textfield
        robot.clickOn("#dlComboBox");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#soTienThuTextField").write("100000"); //100k
        robot.clickOn("#ghiChuTextArea").write("Noting");

        Thread.sleep(300);

        robot.clickOn("#saveButton");
    }

//    @Test
//    void Add_new_bill_false(FxRobot robot) throws InterruptedException, SQLException {
//        robot.clickOn("#user").write("admin");
//        robot.clickOn("#password").write("123456");
//        robot.clickOn("#loginButton");
//
//        robot.clickOn("#openPhieuThuButton");
//
//        robot.clickOn("#openAddNewComboBox");
//        robot.clickOn("#addDirectButton");
//        Thread.sleep(300);
//
//        // fill textfield
//        robot.clickOn("#dlComboBox");
//        Thread.sleep(300);
//        robot.type(KeyCode.DOWN);
//        robot.type(KeyCode.ENTER);
//
//        robot.clickOn("#soTienThuTextField").write("1000000"); //1 trieu
//        robot.clickOn("#ghiChuTextArea").write("Noting");
//
//        Thread.sleep(300);
//
//        robot.clickOn("#saveButton");
//    }







}
