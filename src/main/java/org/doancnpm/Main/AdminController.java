package org.doancnpm.Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.doancnpm.ManHinhDaiLy.ManHinhDaiLyController;
import org.doancnpm.ManHinhPhieuThu.ManHinhPhieuThuController;
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

    private ManHinhDaiLyController manHinhDaiLyController;
    private ManHinhPhieuThuController manHinhPhieuThuController;


    private NhanVien nhanVienLoggedIn = null;

    ManHinh currentManHinh;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initEvent();
        try {
            initScreens();

        } catch (IOException e) {
            PopDialog.popErrorDialog("Khởi tạo app thất bại",e.toString());
        }
        SwitchScreen(ManHinh.DAI_LY);
    }

    private void initScreens() throws IOException {
        FXMLLoader mhDaiLyLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhDaiLy/MainUI.fxml"));
        Parent mhDaiLyGraph = (Parent) mhDaiLyLoader.load();
        manHinhDaiLyController = mhDaiLyLoader.getController();
        centerScreen.getChildren().add(mhDaiLyGraph);
        AnchorPane.setLeftAnchor(mhDaiLyGraph,0.0);
        AnchorPane.setTopAnchor(mhDaiLyGraph,0.0);
        AnchorPane.setBottomAnchor(mhDaiLyGraph,0.0);
        AnchorPane.setRightAnchor(mhDaiLyGraph,0.0);

        FXMLLoader mhPhieuThuLoader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhPhieuThu/MainUI.fxml"));
        Parent mhPhieuThuGraph = (Parent) mhPhieuThuLoader.load();
        manHinhPhieuThuController = mhPhieuThuLoader.getController();
        centerScreen.getChildren().add(mhPhieuThuGraph);

    }

    private void initEvent(){
        openDaiLyButton.setOnAction(_ ->{
            SwitchScreen(ManHinh.DAI_LY);
        });
        openPhieuThuButton.setOnAction(_ ->{
            SwitchScreen(ManHinh.PHIEU_THU);
        });
    }

    public void SwitchScreen(ManHinh manHinhCode){
        //toggle all off
        manHinhDaiLyController.setVisibility(false);
        manHinhPhieuThuController.setVisibility(false);

        switch (manHinhCode){
            case DAI_LY -> manHinhDaiLyController.setVisibility(true);
            case PHIEU_THU -> manHinhPhieuThuController.setVisibility(true);
        }
        currentManHinh = manHinhCode;
    }

    public void setNhanvienLoggedIn(NhanVien nhanVienLoggedIn) {
        this.nhanVienLoggedIn = nhanVienLoggedIn;
        userNameLabel.setText(nhanVienLoggedIn.getHoTen());
    }

}
