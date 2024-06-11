package org.doancnpm.Main;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.doancnpm.DAO.*;
import org.doancnpm.ManHinhBaoCao.ManHinhBaoCaoController;
import org.doancnpm.ManHinhDaiLy.ManHinhDaiLyController;
import org.doancnpm.ManHinhDieuKhien.ManHinhDieuKhienController;
import org.doancnpm.ManHinhKhoHang.ManHinhKhoHangController;
import org.doancnpm.ManHinhNhanVien.ManHinhNhanVienController;
import org.doancnpm.ManHinhPhieuNhap.ManHinhPhieuNhapController;
import org.doancnpm.ManHinhPhieuThu.ManHinhPhieuThuController;
import org.doancnpm.ManHinhPhieuXuat.ManHinhPhieuXuatController;
import org.doancnpm.ManHinhQuyDinh.ManHinhQuyDinhController;
import org.doancnpm.ManHinhTaiKhoan.ManHinhTaiKhoanController;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.NavController;
import org.doancnpm.Ultilities.CurrentNVInfor;
import org.doancnpm.Ultilities.PopDialog;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StaffController implements Initializable {
    public MenuItem openTaiKhoanButton;
    public MenuItem dangXuatButton;
    public Button openBangDieuKhienButton;
    public Button openDaiLyButton;
    public Button openPhieuThuButton;
    public Button openKhoHangButton;
    public Button openPhieuXuatButton;
    public Button openPhieuNhapButton;
    public AnchorPane centerScreen;
    public MenuButton userNameMenuButton;
    public BorderPane mainScreen;

    private ManHinhDaiLyController manHinhDaiLyController;
    private ManHinhPhieuThuController manHinhPhieuThuController;
    private ManHinhKhoHangController manHinhKhoHangController;
    private ManHinhTaiKhoanController manHinhTaiKhoanController;

    private ManHinhPhieuNhapController manHinhPhieuNhapController;
    private ManHinhPhieuXuatController manHinhPhieuXuatController;
    private ManHinhNhanVienController manHinhNhanVienController;
    ManHinh currentManHinh;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initEvent();
        initDatabaseBinding();
        initKeyEventHandling();
        try {
            initScreens();

        } catch (IOException e) {
            e.printStackTrace();
            PopDialog.popErrorDialog("Khởi tạo app thất bại", e.toString());
        }
        SwitchScreen(ManHinh.DAI_LY);
        userNameMenuButton.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getHoTen());
    }
    private void initDatabaseBinding() {
        NhanVienDAO.getInstance().AddDatabaseListener(_ -> {
            userNameMenuButton.setText(CurrentNVInfor.getInstance().getLoggedInNhanVien().getHoTen());
        });
    }
    private void initScreens() throws IOException {

        manHinhDaiLyController = loadAndDoStuff("/fxml/Main/ManHinhDaiLy/MainUI.fxml").getController();
        manHinhPhieuThuController = loadAndDoStuff("/fxml/Main/ManHinhPhieuThu/MainUI.fxml").getController();
        manHinhKhoHangController = loadAndDoStuff("/fxml/Main/ManHinhKhoHang/MainUI.fxml").getController();
        manHinhPhieuNhapController = loadAndDoStuff("/fxml/Main/ManHinhPhieuNhap/MainUI.fxml").getController();
        manHinhPhieuXuatController = loadAndDoStuff("/fxml/Main/ManHinhPhieuXuat/MainUI.fxml").getController();
        manHinhNhanVienController = loadAndDoStuff("/fxml/Main/ManHinhNhanVien/MainUI.fxml").getController();
        manHinhTaiKhoanController = loadAndDoStuff("/fxml/Main/ManHinhTaiKhoan/MainUI.fxml").getController();

    }

    private FXMLLoader loadAndDoStuff(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Parent graph = (Parent) loader.load();
        centerScreen.getChildren().add(graph);
        commonFunc_Anchor(graph);
        return loader;
    }

    private void commonFunc_Anchor(Parent parent) {
        AnchorPane.setLeftAnchor(parent, 0.0);
        AnchorPane.setTopAnchor(parent, 0.0);
        AnchorPane.setBottomAnchor(parent, 0.0);
        AnchorPane.setRightAnchor(parent, 0.0);
    }

    public void SwitchScreen(ManHinh manHinhCode) {
        //toggle all off
        manHinhDaiLyController.setVisibility(false);
        manHinhPhieuThuController.setVisibility(false);
        manHinhKhoHangController.setVisibility(false);
        manHinhNhanVienController.setVisibility(false);
        manHinhPhieuNhapController.setVisibility(false);
        manHinhPhieuXuatController.setVisibility(false);
        manHinhTaiKhoanController.setVisibility(false);

        switch (manHinhCode) {
            case DAI_LY -> manHinhDaiLyController.setVisibility(true);
            case PHIEU_THU -> manHinhPhieuThuController.setVisibility(true);
            case KHO_HANG -> manHinhKhoHangController.setVisibility(true);
            case NHAP -> manHinhPhieuNhapController.setVisibility(true);
            case XUAT -> manHinhPhieuXuatController.setVisibility(true);
            case NHAN_VIEN -> manHinhNhanVienController.setVisibility(true);
            case TAI_KHOAN -> manHinhTaiKhoanController.setVisibility(true);
        }
        currentManHinh = manHinhCode;
    }

    private void initEvent() {
        openDaiLyButton.setOnAction(_ -> {
            SwitchScreen(ManHinh.DAI_LY);
        });
        openPhieuThuButton.setOnAction(_ -> {
            SwitchScreen(ManHinh.PHIEU_THU);
        });
        openKhoHangButton.setOnAction(_ -> {
            SwitchScreen(ManHinh.KHO_HANG);
        });
        openPhieuNhapButton.setOnAction(_ -> {
            SwitchScreen(ManHinh.NHAP);
        });
        openPhieuXuatButton.setOnAction(_ -> {
            SwitchScreen(ManHinh.XUAT);
        });
        openTaiKhoanButton.setOnAction(_ -> {
            SwitchScreen(ManHinh.TAI_KHOAN);
        });
    }

    public void initManager(final NavController navController) {
        dangXuatButton.setOnAction(_ -> handleLogOut(navController));
    }

    private void handleLogOut(NavController navController) {
        navController.logout();
    }
    public void setNhanvienLoggedIn(NhanVien nhanVienLoggedIn) {

        manHinhPhieuThuController.setNhanVienLoggedIn(nhanVienLoggedIn);
        manHinhPhieuNhapController.setNhanVienLoggedIn(nhanVienLoggedIn);
        manHinhPhieuXuatController.setNhanVienLoggedIn(nhanVienLoggedIn);
    }

    private void initKeyEventHandling() {
        mainScreen.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.R) {
                        handleRKeyPress();
                    }
                });
            }
        });
    }

    private void handleRKeyPress() {
        switch (currentManHinh) {
            case DAI_LY -> {
                DaiLyDAO.getInstance().notifyChange();
                System.out.println("refresh DAI_LY");
            }
            case PHIEU_THU -> {
                PhieuThuDAO.getInstance().notifyChange();
                System.out.println("refresh PHIEU_THU");
            }
            case KHO_HANG -> {
                MatHangDAO.getInstance().notifyChange();
                System.out.println("refresh KHO_HANG");
            }
            case NHAP -> {
                PhieuNhapDAO.getInstance().notifyChange();
                System.out.println("refresh NHAP");
            }
            case XUAT -> {
                PhieuXuatDAO.getInstance().notifyChange();
                System.out.println("refresh XUAT");
            }
            case NHAN_VIEN -> {
                NhanVienDAO.getInstance().notifyChange();
                System.out.println("refresh NHAN_VIEN");
            }
            case QUY_DINH -> {
                TaiKhoanDAO.getInstance().notifyChange();
                LoaiDaiLyDAO.getInstance().notifyChange();
                NhanVienDAO.getInstance().notifyChange();
                DaiLyDAO.getInstance().notifyChange();
                MatHangDAO.getInstance().notifyChange();
                System.out.println("refresh QUY_DINH");
            }
            case TAI_KHOAN -> {
                TaiKhoanDAO.getInstance().notifyChange();
                System.out.println("refresh TAI_KHOAN");
            }

        }
    }
}
