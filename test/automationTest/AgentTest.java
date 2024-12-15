package automationTest;

import TestUtilities.TestFunctions;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.doancnpm.AppStart;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(org.testfx.framework.junit5.ApplicationExtension.class)
class AgentTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Manually create an instance of your application
        AppStart appStart = new AppStart();
        appStart.startTest(stage); // Start the application with the test's stage
    }

    @AfterEach
    void CleanUp(){
        TestFunctions.DeleteAll("Daily");
    }
    @Test
    void addNewAgentSuccess(FxRobot robot) throws InterruptedException, SQLException {
        // Đăng nhập
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents();
        // Mở màn hình quản lý đại lý
        robot.clickOn("#openDaiLyButton");
        WaitForAsyncUtils.waitForFxEvents();
        // Chọn loại thêm mới
        robot.clickOn("#openAddNewComboBox");

        robot.clickOn("#addDirectButton");

        robot.clickOn("#tenDaiLyTextField_add").write("Dai li Thu duc");
        robot.clickOn("#diaChiTextField").write("Thu duc");
        robot.clickOn("#emailTextField").write("BinhDuong@gmail.com");
        robot.clickOn("#dienThoaiTextField").write("0848763722");
        robot.clickOn("#ghiChuTextArea_add").write("hoat dong lau dai");

        // Điền thông tin vào form
        robot.clickOn("#quanComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#loaiDaiLyComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        // Lưu dữ liệu
        robot.clickOn("#saveButton");

        DialogPane dialogPane = robot.lookup("#successDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);

        Thread.sleep(1000);
        assertEquals("Thêm mới đại lý thành công", dialogPane.getHeaderText());
        robot.clickOn("#okButton");
    }

    @Test
    void addNewAgentWithDuplicateEmail(FxRobot robot) throws InterruptedException, SQLException {
        // Đăng nhập
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents();
        // Mở màn hình quản lý đại lý
        robot.clickOn("#openDaiLyButton");
        WaitForAsyncUtils.waitForFxEvents();
        // Chọn loại thêm mới
        robot.clickOn("#openAddNewComboBox");

        robot.clickOn("#addDirectButton");

        robot.clickOn("#tenDaiLyTextField_add").write("Dai li 1");
        robot.clickOn("#diaChiTextField").write("Thu duc");
        robot.clickOn("#emailTextField").write("test1@gmail.com");
        robot.clickOn("#dienThoaiTextField").write("0848763722");

        // Điền thông tin vào form
        robot.clickOn("#quanComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#loaiDaiLyComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        // Lưu dữ liệu
        robot.clickOn("#saveButton");
        robot.clickOn("#okButton");


        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");

        robot.clickOn("#tenDaiLyTextField_add").write("Dai li 2");
        robot.clickOn("#diaChiTextField").write("Thu duc");
        robot.clickOn("#emailTextField").write("test1@gmail.com");
        robot.clickOn("#dienThoaiTextField").write("0848763723");

        // Điền thông tin vào form
        robot.clickOn("#quanComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#loaiDaiLyComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        // Lưu dữ liệu
        robot.clickOn("#saveButton");

        Thread.sleep(2000);
        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);

    }
    @Test
    void addNewAgentWithDuplicatePhone(FxRobot robot) throws InterruptedException, SQLException {
        // Đăng nhập
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents();
        // Mở màn hình quản lý đại lý
        robot.clickOn("#openDaiLyButton");
        WaitForAsyncUtils.waitForFxEvents();
        // Chọn loại thêm mới
        robot.clickOn("#openAddNewComboBox");

        robot.clickOn("#addDirectButton");

        robot.clickOn("#tenDaiLyTextField_add").write("Dai li 1");
        robot.clickOn("#diaChiTextField").write("Thu duc");
        robot.clickOn("#emailTextField").write("test1@gmail.com");
        robot.clickOn("#dienThoaiTextField").write("0848763722");

        // Điền thông tin vào form
        robot.clickOn("#quanComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#loaiDaiLyComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        // Lưu dữ liệu
        robot.clickOn("#saveButton");
        robot.clickOn("#okButton");


        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");

        robot.clickOn("#tenDaiLyTextField_add").write("Dai li 2");
        robot.clickOn("#diaChiTextField").write("Thu duc");
        robot.clickOn("#emailTextField").write("test2@gmail.com");
        robot.clickOn("#dienThoaiTextField").write("0848763722");

        // Điền thông tin vào form
        robot.clickOn("#quanComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#loaiDaiLyComboBox_add");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        // Lưu dữ liệu
        robot.clickOn("#saveButton");

        Thread.sleep(2000);
        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
    }
    @Test
    void Add_new_agent_with_WrongFormat_Email(FxRobot robot) throws InterruptedException, SQLException {

        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openDaiLyButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(300);

        // fill textfield
        robot.clickOn("#quanComboBox_add");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#loaiDaiLyComboBox_add");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#tenDaiLyTextField_add").write("Dai li 1");
        robot.clickOn("#diaChiTextField").write("Thu duc");
        robot.clickOn("#emailTextField").write("thuduc#gmail.com");
        robot.clickOn("#dienThoaiTextField").write("0848761702");
        Thread.sleep(300);

        robot.clickOn("#saveButton");


        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        assertEquals("Thêm mới đại lý thất bại", dialogPane.getHeaderText());
    }

    @Test
    void Add_new_agent_with_WrongFormat_Phone(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openDaiLyButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(300);

        // fill textfield
        robot.clickOn("#quanComboBox_add");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#loaiDaiLyComboBox_add");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#tenDaiLyTextField_add").write("Dai li 1");
        robot.clickOn("#diaChiTextField").write("Thu duc");
        robot.clickOn("#emailTextField").write("thuduc3@gmail.com");
        robot.clickOn("#dienThoaiTextField").write("a");
        Thread.sleep(300);

        robot.clickOn("#saveButton");

        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);

        assertEquals("Thêm mới đại lý thất bại", dialogPane.getHeaderText());

    }

    @Test
    void Add_new_agent_with_no_quan_field(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openDaiLyButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(300);

        // fill textfield

        robot.clickOn("#loaiDaiLyComboBox_add");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#tenDaiLyTextField_add").write("Dai li 1");
        robot.clickOn("#diaChiTextField").write("Thu duc");
        robot.clickOn("#emailTextField").write("thuduc3@gmail.com");
        robot.clickOn("#dienThoaiTextField").write("09040404040");
        Thread.sleep(300);

        robot.clickOn("#saveButton");

        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        assertEquals("Thêm mới đại lý thất bại", dialogPane.getHeaderText());
    }

    @Test
    void Add_new_agent_with_no_ldl_field(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openDaiLyButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(300);

        // fill textfield


        robot.clickOn("#tenDaiLyTextField_add").write("dai li 1");
        robot.clickOn("#diaChiTextField").write("Thu duc");
        robot.clickOn("#emailTextField").write("thuduc3@gmail.com");
        robot.clickOn("#dienThoaiTextField").write("0909292393");
        Thread.sleep(300);
        robot.clickOn("#quanComboBox_add");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#saveButton");

        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        assertEquals("Thêm mới đại lý thất bại", dialogPane.getHeaderText());
    }

    @Test
    void Add_new_agent_with_no_diachi_field(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openDaiLyButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(300);

        // fill textfield
        robot.clickOn("#quanComboBox_add");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#loaiDaiLyComboBox_add");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#tenDaiLyTextField_add").write("Dai li 1");

        robot.clickOn("#emailTextField").write("thuduc3@gmail.com");
        robot.clickOn("#dienThoaiTextField").write("0303818283");
        Thread.sleep(300);

        robot.clickOn("#saveButton");

        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        assertEquals("Thêm mới đại lý thất bại", dialogPane.getHeaderText());
    }

    @Test
    void Add_new_agent_with_no_tendaili_field(FxRobot robot) throws InterruptedException {
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");

        robot.clickOn("#openDaiLyButton");

        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(300);

        // fill textfield
        robot.clickOn("#quanComboBox_add");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#loaiDaiLyComboBox_add");
        Thread.sleep(300);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#diaChiTextField").write("Thu duc");
        robot.clickOn("#emailTextField").write("thuduc3@gmail.com");
        robot.clickOn("#dienThoaiTextField").write("0939584838");
        Thread.sleep(300);

        robot.clickOn("#saveButton");

        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);
        assertEquals("Thêm mới đại lý thất bại", dialogPane.getHeaderText());

    }
}
