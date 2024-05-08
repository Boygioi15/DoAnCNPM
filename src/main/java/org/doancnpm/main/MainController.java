package org.doancnpm.main;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.DAO.MatHangDao;
import org.doancnpm.Models.DaiLy;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/** Controls the main application screen */
public class MainController  implements Initializable {
    @FXML
    private Button refreshButton, filterButton;
    @FXML private MenuItem addDirectButton;
    @FXML private MenuItem addExcelButton;
    @FXML private Button lapPhieuThuTienButton;
    @FXML private MenuItem deleteSelectedButton;
    @FXML private MenuItem exportExcelButton;

    @FXML private MFXTextField maDaiLyTextField;
    @FXML private MFXTextField tenDaiLyTextField;
    @FXML private MFXComboBox<String> quanComboBox;
    @FXML private MFXComboBox<String> loaiDaiLyCombobox;

    @FXML private TableView mainTableView;
    @FXML private TableView detailTableView;

    //model part
    private ObservableList<DaiLy> dsDaiLy = FXCollections.observableArrayList();;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //init UI and event
        initTableView();
        initEvent();

        //init binding
        initDatabaseBinding();
        initUIDataBinding();

        //init data
        updateListFromDatabase();
    }

    private void initTableView(){
        // Tạo các cột cho TableView
        TableColumn<DaiLy, String> maDLCol = new TableColumn<>("Mã đại lý");
        maDLCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaDaiLy()));

        TableColumn<DaiLy, String> quanCol = new TableColumn<>("Quận");
        quanCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMaQuan()));

        TableColumn<DaiLy, String> loaiDLCol = new TableColumn<>("Loại đại lý");
        loaiDLCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaLoaiDaiLy()));

        TableColumn<DaiLy, String> tenDLCol = new TableColumn<>("Tên đại lý");
        tenDLCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenDaiLy()));

        TableColumn<DaiLy, String> SDTCol = new TableColumn<>("SDT");
        SDTCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDienThoai()));

        TableColumn<DaiLy, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<DaiLy, String> diaChiCol = new TableColumn<>("Địa chỉ");
        diaChiCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDiaChi()));

        TableColumn<DaiLy, Integer> noHienTaiCol = new TableColumn<>("Nợ hiện tại");
        noHienTaiCol.setCellValueFactory( new PropertyValueFactory<>("noHienTai"));

        TableColumn<DaiLy, String> ghiChuCol = new TableColumn<>("Ghi chú");
        ghiChuCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGhiChu()));

        // Thêm các cột vào TableView
        mainTableView.getColumns().addAll(maDLCol,quanCol,loaiDLCol,tenDLCol,SDTCol,emailCol,diaChiCol,noHienTaiCol,ghiChuCol);
    }
    private void initDatabaseBinding(){
        DaiLyDAO.getInstance().AddDatabaseListener(ob -> {updateListFromDatabase();});
    }
    private void initEvent(){
        addDirectButton.setOnAction(_ -> {
            try {
                OpenDirectAddDialog();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void initUIDataBinding(){
        mainTableView.setItems(dsDaiLy);
    }

    public void OpenDirectAddDialog() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main/TiepNhanDaiLyUI.fxml")
        );
        Parent parent = loader.load();
        DirectAddDialogController dialogController = loader.<DirectAddDialogController>getController();
        //dialogController.setAppMainObservableList(tvObservableList);

        Scene scene = new Scene(parent, 300, 200);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void updateListFromDatabase() {
        dsDaiLy.clear();
        try {
            dsDaiLy.addAll(DaiLyDAO.getInstance().QueryAll());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}