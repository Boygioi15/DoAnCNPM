package org.doancnpm.Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.doancnpm.ManHinhDaiLy.ManHinhDaiLyController;
import org.doancnpm.ManHinhPhieuThu.ManHinhPhieuThuController;
import org.doancnpm.ManHinhTaiKhoan.ManHinhTaiKhoanController;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Ultilities.PopDialog;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    private @FXML BorderPane mainScreen;
    private @FXML AnchorPane centerScreen;

    private @FXML Button openDaiLyButton;
    private @FXML Button openPhieuThuButton;
    private @FXML Label userNameLabel;
    private @FXML MenuItem inforButton;

    private ManHinhDaiLyController manHinhDaiLyController;
    private ManHinhPhieuThuController manHinhPhieuThuController;
    private ManHinhTaiKhoanController manHinhTaiKhoanController;

    ManHinh currentManHinh;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initEvent();
        try {
            initScreens();

        } catch (IOException e) {
            PopDialog.popErrorDialog("Khởi tạo app thất bại", e.toString());
        }
        SwitchScreen(ManHinh.DAI_LY);
    }

    private void initScreens() throws IOException {
        FXMLLoader mhDaiLyLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhDaiLy/MainUI.fxml"));
        Parent mhDaiLyGraph = (Parent) mhDaiLyLoader.load();
        manHinhDaiLyController = mhDaiLyLoader.getController();
        centerScreen.getChildren().add(mhDaiLyGraph);
        AnchorPane.setLeftAnchor(mhDaiLyGraph, 0.0);
        AnchorPane.setTopAnchor(mhDaiLyGraph, 0.0);
        AnchorPane.setBottomAnchor(mhDaiLyGraph, 0.0);
        AnchorPane.setRightAnchor(mhDaiLyGraph, 0.0);

        FXMLLoader mhPhieuThuLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhPhieuThu/MainUI.fxml"));
        Parent mhPhieuThuGraph = (Parent) mhPhieuThuLoader.load();
        manHinhPhieuThuController = mhPhieuThuLoader.getController();
        centerScreen.getChildren().add(mhPhieuThuGraph);
        System.out.println("lasdwdd :" + mhPhieuThuGraph.toString());

        FXMLLoader mhTaiKhoanLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhTaiKhoan/MainUI.fxml"));
        Parent mhTaiKhoanGraph = (Parent) mhTaiKhoanLoader.load();
        manHinhTaiKhoanController = mhTaiKhoanLoader.getController();
        centerScreen.getChildren().add(mhTaiKhoanGraph);
        AnchorPane.setLeftAnchor(mhTaiKhoanGraph, 0.0);
        AnchorPane.setTopAnchor(mhTaiKhoanGraph, 0.0);
        AnchorPane.setBottomAnchor(mhTaiKhoanGraph, 0.0);
        AnchorPane.setRightAnchor(mhTaiKhoanGraph, 0.0);



    }

    private void initEvent() {
        openDaiLyButton.setOnAction(_ -> {
            SwitchScreen(ManHinh.DAI_LY);
        });
        openPhieuThuButton.setOnAction(_ -> {
            SwitchScreen(ManHinh.PHIEU_THU);
        });
        inforButton.setOnAction(_ -> {
            SwitchScreen(ManHinh.INFOR);
        });

    }

    public void SwitchScreen(ManHinh manHinhCode) {
        //toggle all off
        manHinhDaiLyController.setVisibility(false);
        manHinhPhieuThuController.setVisibility(false);
        manHinhTaiKhoanController.setVisibility(false);

        switch (manHinhCode) {
            case DAI_LY -> manHinhDaiLyController.setVisibility(true);
            case PHIEU_THU -> manHinhPhieuThuController.setVisibility(true);
            case INFOR -> manHinhTaiKhoanController.setVisibility(true);
        }
        currentManHinh = manHinhCode;
    }

    public void setNhanvienLoggedIn(NhanVien nhanVienLoggedIn) {
        userNameLabel.setText(nhanVienLoggedIn.getHoTen());
    }

}
