package org.doancnpm.main;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
                            final Button suaBtn = new javafx.scene.control.Button("Sửa");
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
                                        alert.setHeaderText(dl.getTenDaiLy() + " đã đồng hành với bạn trong 3 tháng\nBạn qua cầu rút ván hả?");

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK){
                                            try {
                                                DaiLyDAO.getInstance().Delete(dl.getID());
                                                popSuccessDialog("Xóa đại lý "+ dl.getMaDaiLy() + " thành công");
                                            } catch (SQLException e) {
                                                popErrorDialog("Xóa đại lý "+ dl.getMaDaiLy() + " thất bại",e.toString());
                                            }
                                        }
                                    });
                                    suaBtn.setOnAction(_ -> {
                                        try {
                                            DaiLy daily = getTableView().getItems().get(getIndex());
                                            new DirectAddDialog(daily).showAndWait().ifPresent(daiLyInfo -> {
                                                daiLyInfo.setID(daily.getID());
                                                daiLyInfo.setNoHienTai(daily.getNoHienTai());
                                                daiLyInfo.setNgayTiepNhan(daily.getNgayTiepNhan());
                                                daiLyInfo.setMaDaiLy(daily.getMaDaiLy());
                                                try {
                                                    DaiLyDAO.getInstance().Update(daily.getID(),daiLyInfo);
                                                    popSuccessDialog("Cập nhật đại lý "+daiLyInfo.getMaDaiLy()+" thành công");
                                                } catch (SQLException e) {
                                                    popErrorDialog("Cập nhật đại lý "+daiLyInfo.getMaDaiLy()+" thất bại",
                                                            e.toString());
                                                }
                                                //mainTableView.getItems().set(selectedIndex, response);
                                            });
                                        } catch(IOException exc) {
                                            exc.printStackTrace();
                                        }
                                    });
                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(suaBtn,xoaBtn);
                                    hbox.setSpacing(5);
                                    hbox.setPrefWidth(USE_COMPUTED_SIZE);
                                    hbox.setPrefHeight(USE_COMPUTED_SIZE);
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
        deleteSelectedButton.setOnAction(_ -> DeleteSelectedRow());
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
        try {
            new DirectAddDialog().showAndWait().ifPresent(
                    daiLyAdded -> {
                        try {
                            DaiLyDAO.getInstance().Insert(daiLyAdded);
                            popSuccessDialog("Thêm mới đại lý "+daiLyAdded.getTenDaiLy()+" thành công");
                        }
                        catch (SQLException e) {
                            popErrorDialog("Thêm mới đại lý thất bại", e.toString());
                        }
                    }
            );
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }
        /*
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

         */
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

    private void popSuccessDialog(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo!!!");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    private void popErrorDialog(String message, String errorMessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Thông báo!!!");
        alert.setHeaderText(message);
        alert.setContentText("Lỗi: " + errorMessage);
        alert.showAndWait();
    }
}