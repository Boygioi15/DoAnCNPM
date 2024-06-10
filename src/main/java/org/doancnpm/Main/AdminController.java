package org.doancnpm.Main;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
import org.doancnpm.Ultilities.PopDialog;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    private @FXML BorderPane mainScreen;
    private @FXML AnchorPane centerScreen;

    private @FXML Button openDaiLyButton;
    private @FXML Button openPhieuThuButton;
    private @FXML Button openBangDieuKhienButton;
    private @FXML Button openBaoCaoButton;
    private @FXML Button openKhoHangButton;
    private @FXML Button openPhieuNhapButton;
    private @FXML Button openPhieuXuatButton;
    private @FXML Button openNhanVienButton;
    private @FXML Button openQuyDinhButton;
    public @FXML MenuItem openTaiKhoanButton;
    public @FXML MenuItem dangXuatButton;

    private ManHinhDaiLyController manHinhDaiLyController;
    private ManHinhPhieuThuController manHinhPhieuThuController;
    private ManHinhKhoHangController manHinhKhoHangController;

    private ManHinhPhieuNhapController manHinhPhieuNhapController;
    private ManHinhPhieuXuatController manHinhPhieuXuatController;
    private ManHinhNhanVienController manHinhNhanVienController;
  
    private ManHinhQuyDinhController manHinhQuyDinhController;
    private ManHinhTaiKhoanController manHinhTaiKhoanController;
    private ManHinhDieuKhienController manHinhDieuKhienController;
    private ManHinhBaoCaoController manHinhBaoCaoController;

    private NhanVien nhanVienLoggedIn = null;

    ManHinh currentManHinh;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initEvent();
        try {
            initScreens();
            SwitchScreen(ManHinh.DAI_LY);

        } catch (Exception e){
            e.printStackTrace();
            PopDialog.popErrorDialog("Khởi tạo app thất bại",e.toString());
        }

    }

    private void initScreens() throws IOException {

        manHinhDaiLyController = loadAndDoStuff("/fxml/Main/ManHinhDaiLy/MainUI.fxml").getController();
        manHinhPhieuThuController = loadAndDoStuff("/fxml/Main/ManHinhPhieuThu/MainUI.fxml").getController();
        manHinhKhoHangController = loadAndDoStuff("/fxml/Main/ManHinhKhoHang/MainUI.fxml").getController();
        manHinhPhieuNhapController = loadAndDoStuff("/fxml/Main/ManHinhPhieuNhap/MainUI.fxml").getController();
        manHinhPhieuXuatController = loadAndDoStuff("/fxml/Main/ManHinhPhieuXuat/MainUI.fxml").getController();
        manHinhNhanVienController = loadAndDoStuff("/fxml/Main/ManHinhNhanVien/MainUI.fxml").getController();
        manHinhQuyDinhController = loadAndDoStuff("/fxml/Main/ManHinhQuyDinh/MainUI.fxml").getController();
        manHinhTaiKhoanController = loadAndDoStuff("/fxml/Main/ManHinhTaiKhoan/MainUI.fxml").getController();

        manHinhDieuKhienController = loadAndDoStuff("/fxml/Main/ManHinhDieuKhien/MainUI.fxml").getController();
        manHinhBaoCaoController = loadAndDoStuff("/fxml/Main/ManHinhBaoCao/MainUI.fxml").getController();
    }

    private FXMLLoader loadAndDoStuff(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Parent graph = (Parent) loader.load();
        centerScreen.getChildren().add(graph);
        commonFunc_Anchor(graph);
        return loader;
    }
    private void initEvent(){
        openDaiLyButton.setOnAction(_ ->{
            SwitchScreen(ManHinh.DAI_LY);
        });
        openPhieuThuButton.setOnAction(_ ->{
            SwitchScreen(ManHinh.PHIEU_THU);
        });
        openBangDieuKhienButton.setOnAction(_->{
            SwitchScreen(ManHinh.DIEU_KHIEN);
        });
        openBaoCaoButton.setOnAction(_-> {
            SwitchScreen(ManHinh.BAO_CAO);
        });
        openKhoHangButton.setOnAction(_ ->{
            SwitchScreen(ManHinh.KHO_HANG);
        });
        openPhieuNhapButton.setOnAction(_ ->{
            SwitchScreen(ManHinh.NHAP);
        });
        openPhieuXuatButton.setOnAction(_ ->{
            SwitchScreen(ManHinh.XUAT);
        });
        openNhanVienButton.setOnAction(_ ->{
            SwitchScreen(ManHinh.NHAN_VIEN);
        });
        openQuyDinhButton.setOnAction(_ ->{
            SwitchScreen(ManHinh.QUY_DINH);
        });
        openTaiKhoanButton.setOnAction(_->{
            SwitchScreen(ManHinh.TAI_KHOAN);
        });
    }
    final PseudoClass selectedButton = PseudoClass.getPseudoClass("selected");
    public void SwitchScreen(ManHinh manHinhCode){
        //toggle all off

        openBangDieuKhienButton.pseudoClassStateChanged(selectedButton, false);

        openDaiLyButton.pseudoClassStateChanged(selectedButton, false);
        openPhieuThuButton.pseudoClassStateChanged(selectedButton, false);

        openPhieuNhapButton.pseudoClassStateChanged(selectedButton, false);
        openPhieuXuatButton.pseudoClassStateChanged(selectedButton, false);
        openKhoHangButton.pseudoClassStateChanged(selectedButton, false);

        openNhanVienButton.pseudoClassStateChanged(selectedButton, false);
        openQuyDinhButton.pseudoClassStateChanged(selectedButton, false);
        openBaoCaoButton.pseudoClassStateChanged(selectedButton, false);

        manHinhDaiLyController.setVisibility(false);
        manHinhPhieuThuController.setVisibility(false);
        manHinhKhoHangController.setVisibility(false);
        manHinhNhanVienController.setVisibility(false);
        manHinhPhieuNhapController.setVisibility(false);
        manHinhPhieuXuatController.setVisibility(false);
        manHinhQuyDinhController.setVisibility(false);
        manHinhTaiKhoanController.setVisibility(false);
      
        manHinhDieuKhienController.setVisibility(false);

        manHinhBaoCaoController.setVisibility(false);

        switch (manHinhCode){
            case DAI_LY -> {
                manHinhDaiLyController.setVisibility(true);
                openDaiLyButton.pseudoClassStateChanged(selectedButton, true);
            }
            case PHIEU_THU -> {
                manHinhPhieuThuController.setVisibility(true);
                openPhieuThuButton.pseudoClassStateChanged(selectedButton, true);
            }
            case KHO_HANG -> {
                manHinhKhoHangController.setVisibility(true);
                openKhoHangButton.pseudoClassStateChanged(selectedButton, true);
            }
            case NHAP -> {
                manHinhPhieuNhapController.setVisibility(true);
                openPhieuNhapButton.pseudoClassStateChanged(selectedButton, true);
            }
            case XUAT -> {
                manHinhPhieuXuatController.setVisibility(true);
                openPhieuXuatButton.pseudoClassStateChanged(selectedButton, true);
            }
            case NHAN_VIEN -> {
                manHinhNhanVienController.setVisibility(true);
                openNhanVienButton.pseudoClassStateChanged(selectedButton, true);
            }
            case QUY_DINH -> {
                manHinhQuyDinhController.setVisibility(true);
                openQuyDinhButton.pseudoClassStateChanged(selectedButton, true);
            }
            case TAI_KHOAN -> {
                manHinhTaiKhoanController.setVisibility(true);
            }
            
            case DIEU_KHIEN -> {
                manHinhDieuKhienController.setVisibility(true);
                openBangDieuKhienButton.pseudoClassStateChanged(selectedButton, true);
            }
            case BAO_CAO -> {
                manHinhBaoCaoController.setVisibility(true);
                openBaoCaoButton.pseudoClassStateChanged(selectedButton, true);
            }
        }
        currentManHinh = manHinhCode;
    }
    private void commonFunc_Anchor(Parent parent){
        AnchorPane.setLeftAnchor(parent,0.0);
        AnchorPane.setTopAnchor(parent,0.0);
        AnchorPane.setBottomAnchor(parent,0.0);
        AnchorPane.setRightAnchor(parent,0.0);
    }
    public void setNhanvienLoggedIn(NhanVien nhanVienLoggedIn) {
        this.nhanVienLoggedIn = nhanVienLoggedIn;

        manHinhPhieuThuController.setNhanVienLoggedIn(nhanVienLoggedIn);
        manHinhPhieuNhapController.setNhanVienLoggedIn(nhanVienLoggedIn);
        manHinhPhieuXuatController.setNhanVienLoggedIn(nhanVienLoggedIn);
    }
    public void initManager(final NavController navController) {
        dangXuatButton.setOnAction(_ -> handleLogOut(navController));
    }
    private void handleLogOut(NavController navController){
        navController.logout();
    }

}
