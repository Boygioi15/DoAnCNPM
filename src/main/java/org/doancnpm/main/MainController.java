package org.doancnpm.main;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.Filters.DaiLyFilter;
import org.doancnpm.Models.DaiLy;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

/** Controls the main application screen */
public class MainController  implements Initializable {

    @FXML private VBox daiLyScreen;
    @FXML private Button refreshButton, filterButton;
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
    private final ObservableList<DaiLy> dsDaiLy = FXCollections.observableArrayList();
    private final ObservableList<DaiLy> dsDaiLyFiltered = FXCollections.observableArrayList();
    private final DaiLyFilter filter = new DaiLyFilter();

    StringProperty maDaiLyFilter = new SimpleStringProperty("");
    StringProperty tenDaiLyFilter = new SimpleStringProperty("");
    StringProperty maQuanFilter = new SimpleStringProperty("");
    StringProperty maLoaiDaiLyFilter= new SimpleStringProperty("");

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

        TableColumn<DaiLy, Integer> quanCol = new TableColumn<>("Quận");
        quanCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMaQuan()));

        TableColumn<DaiLy, Integer> loaiDLCol = new TableColumn<>("Loại đại lý");
        loaiDLCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMaLoaiDaiLy()));

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


        //selected collumn:
        TableColumn<DaiLy, Boolean> selectedCol = new TableColumn<>( "Selected" );
        selectedCol.setCellValueFactory( new PropertyValueFactory<>( "selected" ));
        selectedCol.setCellFactory( tc -> new CheckBoxTableCell<>());

        //action column
        TableColumn actionCol = new TableColumn("Action");
        //actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<DaiLy, String>, TableCell<DaiLy, String>> cellFactory
                = //
                new Callback<TableColumn<DaiLy, String>, TableCell<DaiLy, String>>() {
                    @Override
                    public TableCell call(final TableColumn<DaiLy, String> param) {
                        final TableCell<DaiLy, String> cell = new TableCell<DaiLy, String>() {
                            final Button themBtn = new javafx.scene.control.Button("Sửa");
                            final Button xoaBtn = new javafx.scene.control.Button("Xóa");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                }
                                else {
                                    xoaBtn.setOnAction(event -> {
                                        DaiLy dl = getTableView().getItems().get(getIndex());
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Xóa đại lý");
                                        alert.setHeaderText("Đại lý" + dl.getTenDaiLy() + " đã đồng hành với bạn trong 3 tháng\nBạn qua cầu rút ván hả?");
                                        alert.setContentText("Bạn chắc chứ?");

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK){
                                            try {
                                                DaiLyDAO.getInstance().Delete(dl.getID());
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });

                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(themBtn,xoaBtn);
                                    hbox.setSpacing(5);
                                    hbox.setPrefWidth(USE_COMPUTED_SIZE);
                                    hbox.setPrefHeight(USE_COMPUTED_SIZE);
                                    System.out.println("hi");
                                    setGraphic(hbox);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        actionCol.setCellFactory(cellFactory);

        // Thêm các cột vào TableView
        mainTableView.getColumns().addAll(selectedCol,
                maDLCol,
                quanCol,
                loaiDLCol,
                tenDLCol,
                SDTCol,
                emailCol,
                diaChiCol,
                noHienTaiCol,
                ghiChuCol,
                actionCol
        );
        mainTableView.setEditable( true );
    }
    private void initEvent(){
        addDirectButton.setOnAction(_ -> {
            try {
                OpenDirectAddDialog();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        refreshButton.setOnAction(_ -> {
            resetFilter();
        });
        deleteSelectedButton.setOnAction(ob -> DeleteSelectedRow());

    }

    private void initDatabaseBinding(){
        DaiLyDAO.getInstance().AddDatabaseListener(_ -> updateListFromDatabase());
    }
    private void initUIDataBinding(){
        mainTableView.setItems(dsDaiLyFiltered);
        initFilterBinding();
    }
    private void initFilterBinding(){
        maDaiLyFilter.bindBidirectional(maDaiLyTextField.textProperty());
        tenDaiLyFilter.bindBidirectional(tenDaiLyTextField.textProperty());
        maQuanFilter.bindBidirectional(quanComboBox.valueProperty());
        maLoaiDaiLyFilter.bindBidirectional(loaiDaiLyCombobox.valueProperty());

        filter.setInput(dsDaiLy);
        maDaiLyFilter.addListener(_ -> {
            filter.setMaDaiLy(maDaiLyFilter.getValue());
            filterList();
        });
        tenDaiLyFilter.addListener(_ -> {
            filter.setTenDaiLy(tenDaiLyFilter.getValue());
            filterList();
        });
        maQuanFilter.addListener(_ -> {
            filter.setMaQuan(maQuanFilter.getValue());
            filterList();
        });
        maLoaiDaiLyFilter.addListener(_ -> {
            filter.setMaLoaiDaiLy(maLoaiDaiLyFilter.getValue());
            filterList();
        });
    }

    public void OpenDirectAddDialog() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main/TiepNhanDaiLyUI.fxml")
        );
        Parent parent = loader.load();
        DirectAddDialogController dialogController = loader.<DirectAddDialogController>getController();
        //dialogController.setAppMainObservableList(tvObservableList);
        PopupControl test = new PopupControl();
        Scene scene = new Scene(parent, 300, 200);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void DeleteSelectedRow()  {
        final Set<DaiLy> del = new HashSet<>();
        for( Object o : mainTableView.getItems()) {
            DaiLy dl = (DaiLy) o;
            if( dl.isSelected()) {
                del.add( dl );
            }
        }
        //System.out.println(del.size());
        for(DaiLy dl : del){
            int ID = dl.getID();
            try {
                DaiLyDAO.getInstance().Delete(ID);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //mainTableView.getItems().removeAll( del );
    }
    private void updateListFromDatabase() {
        dsDaiLy.clear();
        try {
            dsDaiLy.addAll(DaiLyDAO.getInstance().QueryAll());
            filterList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void filterList(){
        dsDaiLyFiltered.clear();
        dsDaiLyFiltered.addAll(filter.Filter());
    }
    private void resetFilter(){
        quanComboBox.clear();
        maDaiLyTextField.clear();
        tenDaiLyTextField.clear();
        loaiDaiLyCombobox.clear();
    }


}