package automationTest;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.doancnpm.AppStart;
import org.doancnpm.Ultilities.PopDialog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
class AgentTest  {

    @Start
    public void start(Stage stage) throws Exception {
        // Manually create an instance of your application
        AppStart appStart = new AppStart();
        appStart.start(stage); // Start the application with the test's stage
    }

    @Test
    void addNewAgent(FxRobot robot) throws InterruptedException {
        // Đăng nhập
        robot.clickOn("#user").write("admin");
        robot.clickOn("#password").write("123456");
        robot.clickOn("#loginButton");
        Thread.sleep(1000);
        // Mở màn hình quản lý đại lý
        robot.clickOn("#openDaiLyButton");
        Thread.sleep(1000);
        // Chọn loại thêm mới
        robot.clickOn("#openAddNewComboBox");
        robot.clickOn("#addDirectButton");
        Thread.sleep(1000);

        // Điền thông tin vào form
        robot.clickOn("#quanComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#loaiDaiLyComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#tenDaiLyTextField").write("Dai li 11213");
        robot.clickOn("#diaChiTextField").write("Thu duc1");
        robot.clickOn("#emailTextField").write("thuduc1223@gmail.com");
        robot.clickOn("#dienThoaiTextField").write("0848763722");
        robot.clickOn("#ghiChuTextArea").write("hoat dong lau dai");

        // Lưu dữ liệu
        robot.clickOn("#saveButton");

        DialogPane dialogPane = robot.lookup("#errorDialog").queryAs(DialogPane.class);
        assertNotNull(dialogPane);


        assertEquals("Thông báo!", dialogPane.getHeaderText());
        assertEquals("Thêm mới đại lý thành công", dialogPane.getContentText());
    }

//
//    @Test
//    void Add_new_agent_with_WrongFormat_Email(FxRobot robot) throws InterruptedException {
//        robot.clickOn("#user").write("admin");
//        robot.clickOn("#password").write("123456");
//        robot.clickOn("#loginButton");
//
//        robot.clickOn("#openDaiLyButton");
//
//        robot.clickOn("#openAddNewComboBox");
//        robot.clickOn("#addDirectButton");
//        Thread.sleep(300);
//
//        // fill textfield
//        robot.clickOn("#quanComboBox");
//        Thread.sleep(300);
//        robot.type(KeyCode.DOWN);
//        robot.type(KeyCode.ENTER);
//        robot.clickOn("#loaiDaiLyComboBox");
//        Thread.sleep(300);
//        robot.type(KeyCode.DOWN);
//        robot.type(KeyCode.ENTER);
//        robot.clickOn("#tenDaiLyTextField").write("Dai li 1");
//        robot.clickOn("#diaChiTextField").write("Thu duc");
//        robot.clickOn("#emailTextField").write("thuduc#gmail.com");
//        robot.clickOn("#dienThoaiTextField").write("0848761702");
//        robot.clickOn("#ghiChuTextArea").write("hoat dong lau dai");
//        Thread.sleep(300);
//
//        robot.clickOn("#saveButton");
//    }
//
//    @Test
//    void Add_new_agent_with_WrongFormat_Phone(FxRobot robot) throws InterruptedException {
//        robot.clickOn("#user").write("admin");
//        robot.clickOn("#password").write("123456");
//        robot.clickOn("#loginButton");
//
//        robot.clickOn("#openDaiLyButton");
//
//        robot.clickOn("#openAddNewComboBox");
//        robot.clickOn("#addDirectButton");
//        Thread.sleep(300);
//
//        // fill textfield
//        robot.clickOn("#quanComboBox");
//        Thread.sleep(300);
//        robot.type(KeyCode.DOWN);
//        robot.type(KeyCode.ENTER);
//        robot.clickOn("#loaiDaiLyComboBox");
//        Thread.sleep(300);
//        robot.type(KeyCode.DOWN);
//        robot.type(KeyCode.ENTER);
//        robot.clickOn("#tenDaiLyTextField").write("Dai li 1");
//        robot.clickOn("#diaChiTextField").write("Thu duc");
//        robot.clickOn("#emailTextField").write("thuduc3#gmail.com");
//        robot.clickOn("#dienThoaiTextField").write("a");
//        robot.clickOn("#ghiChuTextArea").write("hoat dong lau dai");
//        Thread.sleep(300);
//
//        robot.clickOn("#saveButton");
//    }
//
//    @Test
//    void Add_new_agent_with_lack_of_field(FxRobot robot) throws InterruptedException {
//        robot.clickOn("#user").write("admin");
//        robot.clickOn("#password").write("123456");
//        robot.clickOn("#loginButton");
//
//        robot.clickOn("#openDaiLyButton");
//
//        robot.clickOn("#openAddNewComboBox");
//        robot.clickOn("#addDirectButton");
//        Thread.sleep(300);
//
//        // fill textfield
//        robot.clickOn("#quanComboBox");
//        Thread.sleep(300);
//        robot.type(KeyCode.DOWN);
//        robot.type(KeyCode.ENTER);
//        robot.clickOn("#loaiDaiLyComboBox");
//        Thread.sleep(300);
//        robot.type(KeyCode.DOWN);
//        robot.type(KeyCode.ENTER);
//        robot.clickOn("#tenDaiLyTextField").write("");
//        robot.clickOn("#diaChiTextField").write("Thu duc");
//        robot.clickOn("#emailTextField").write("thuduc3#gmail.com");
//        robot.clickOn("#dienThoaiTextField").write("a");
//        robot.clickOn("#ghiChuTextArea").write("hoat dong lau dai");
//        Thread.sleep(300);
//
//        robot.clickOn("#saveButton");
//    }
//

}
