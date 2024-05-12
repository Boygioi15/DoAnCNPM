package org.doancnpm;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.doancnpm.Login.LoginController;
import org.doancnpm.Main.AdminController;
import org.doancnpm.Models.NhanVien;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Manages control flow for logins */
public class NavController {
    private Scene scene;

    public NavController(Scene scene) {
        this.scene = scene;
    }

    /**
     * Callback method invoked to notify that a user has been authenticated.
     * Will show the main application screen.
     */
    public void authenticated(NhanVien nhanVienLoggedIn) {
        showMainView(nhanVienLoggedIn);
    }

    /**
     * Callback method invoked to notify that a user has logged out of the main application.
     * Will show the login application screen.
     */
    public void logout() {
        showLoginScreen();
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

    private void showMainView(NhanVien nhanVienLoggedIn) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/Main/AdminUI.fxml")
            );
            Parent objectGraph = (Parent) loader.load();
            AdminController controller = loader.getController();
            if (controller != null) {
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
}