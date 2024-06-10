package org.doancnpm;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.doancnpm.Login.LoginController;
import org.doancnpm.Main.AdminController;
import org.doancnpm.Main.StaffController;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Ultilities.CurrentNVInfor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Manages control flow for logins */
public class NavController {
    private Scene scene;
    private Stage stage;
    public NavController(Scene scene, Stage stage) {
        this.scene = scene;
        this.stage = stage;
    }

    /**
     * Callback method invoked to notify that a user has been authenticated.
     * Will show the main application screen.
     */
    public void authenticated(NhanVien nhanVienLoggedIn) {
        if(CurrentNVInfor.getInstance().getLoggedInNhanVien().getMaChucVu().equals(3)){
            showAdminView(nhanVienLoggedIn); //admin
        }
        else if(CurrentNVInfor.getInstance().getLoggedInNhanVien().getMaChucVu().equals(2)){
            showStaffView(nhanVienLoggedIn);
        }

    }

    /**
     * Callback method invoked to notify that a user has logged out of the main application.
     * Will show the login application screen.
     */
    public void logout() {
        Stage currentStage = (Stage) scene.getWindow();
        currentStage.close(); // Đóng màn hình hiện tại

        Stage stage = new Stage();
        Scene loginScene = new Scene(new Parent() {  });

        NavController navController = new NavController(loginScene,stage);
        navController.showLoginScreen();

        stage.setScene(loginScene);
        stage.show();
    }

    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/Login/LoginUI.fxml")
            );
            scene.setRoot((Parent) loader.load());
            LoginController controller =
                    loader.<LoginController>getController();
            controller.initManager(this);
        } catch (IOException ex) {
            Logger.getLogger(NavController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showAdminView(NhanVien nhanVienLoggedIn) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/Main/AdminUI.fxml")
            );
            Parent objectGraph = (Parent) loader.load();
            AdminController controller = loader.getController();

            if (controller != null) {
                controller.initManager(this);
                controller.setNhanvienLoggedIn(nhanVienLoggedIn);
            } else {
                Logger.getLogger(NavController.class.getName()).log(Level.SEVERE, "MainController is null.");
            }

            scene.setRoot(objectGraph);
            scene.getWindow().sizeToScene();
            scene.getWindow().centerOnScreen();
        } catch (IOException ex) {
            Logger.getLogger(NavController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void showStaffView(NhanVien nhanVienLoggedIn) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/Main/StaffUI.fxml")
            );
            Parent objectGraph = (Parent) loader.load();
            StaffController controller = loader.getController();

            if (controller != null) {
                controller.initManager(this);
                controller.setNhanvienLoggedIn(nhanVienLoggedIn);
            } else {
                Logger.getLogger(NavController.class.getName()).log(Level.SEVERE, "MainController is null.");
            }

            scene.setRoot(objectGraph);
            stage.setMinWidth(1300);
            stage.setMinHeight(700);
            scene.getWindow().centerOnScreen();
        } catch (IOException ex) {
            Logger.getLogger(NavController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}