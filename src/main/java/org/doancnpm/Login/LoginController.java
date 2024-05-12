package org.doancnpm.Login;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.DAO.TaiKhoanDAO;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.TaiKhoan;
import org.doancnpm.NavController;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/** Controls the login screen */
public class LoginController implements Initializable {
    @FXML private TextField user;
    @FXML private TextField password;
    @FXML private Button loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private TaiKhoanDAO taiKhoanDAO = TaiKhoanDAO.getInstance();
    public void initManager(final NavController loginManager) {
        loginButton.setOnAction( _ -> {
            NhanVien loggedInNhanVien = authorize();
            if (loggedInNhanVien != null) {
                loginManager.authenticated(loggedInNhanVien);
            }
        });
    }

    private NhanVien authorize() {
        String userName = user.getText();
        String pass = password.getText();
        NhanVienDAO nhanVienDAO = new NhanVienDAO();
        try {
            ArrayList<TaiKhoan> taiKhoanList = taiKhoanDAO.QueryAll();
            for (TaiKhoan taiKhoan : taiKhoanList) {
                if (taiKhoan.getUserName().equals(userName) && taiKhoan.getPassword().equals(pass)) {
                    // Authorized
                    int maNhanVien = taiKhoan.getMaNhanVien();
                    return nhanVienDAO.QueryID(maNhanVien);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}