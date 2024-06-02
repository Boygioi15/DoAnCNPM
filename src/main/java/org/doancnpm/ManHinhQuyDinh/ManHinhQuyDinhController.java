package org.doancnpm.ManHinhQuyDinh;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.doancnpm.DAO.*;
import org.doancnpm.ManHinhDaiLy.TiepNhanDaiLyDialog;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.PopDialog;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManHinhQuyDinhController implements Initializable {

    @FXML private Node manHinhQuyDinh;

    @FXML TableView quanTableView;
    @FXML Button quanThemMoiBtn;

    @FXML TableView ldlTableView;
    @FXML Button ldlThemMoiBtn;

    @FXML TableView tkTableView;
    @FXML Button tkThemMoiBtn;

    @FXML TableView donViTinhTableView;
    @FXML Button donViTinhThemMoiBtn;

    @FXML TextField slDLToiDaTextField;
    @FXML Button slDLCommitButton;
    @FXML TextField tyleTextField;
    @FXML Button tyLeCommitButton;
    @FXML CheckBox choPhepVuotNoCheckBox;

    private final ObservableList<Quan> dsQuan = FXCollections.observableArrayList();
    private final ObservableList<LoaiDaiLy> dsLoaiDaiLy = FXCollections.observableArrayList();
    private final ObservableList<TaiKhoan> dsTaiKhoan = FXCollections.observableArrayList();
    private final ObservableList<DonViTinh> dsDonViTinh = FXCollections.observableArrayList();
    public void setVisibility(boolean visibility) {
        manHinhQuyDinh.setVisible(visibility);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initQuanTable();
        initQuanDtbBinding();

        initLDLTable();
        initLDLDtbBinding();

        initTaiKhoanTable();
        initTaiKhoanDtbBinding();

        initDonViTinhTable();
        initDonViTinhDtbBinding();

        initEvent();
        initData();
    }
    private void initEvent(){
        quanThemMoiBtn.setOnAction(ob -> {
            themMoiQuan();
        });
        ldlThemMoiBtn.setOnAction(ob -> {
            themMoiLDL();
        });
        tkThemMoiBtn.setOnAction(ob -> {
            OpenThemMoiTKDialog();
        });
        donViTinhThemMoiBtn.setOnAction(ob -> {
            themMoiDonViTinh();
        });

        slDLCommitButton.setVisible(false);
        slDLCommitButton.setOnAction(ob ->{
            capNhatDLToiDa();
        });
        slDLToiDaTextField.textProperty().addListener(ob ->{
            if(slDLToiDaTextField.focusedProperty().get()){
                slDLCommitButton.setVisible(true);
            }
        });
        slDLToiDaTextField.setOnKeyPressed(ob -> {
            if(ob.getCode()== KeyCode.ENTER){
                capNhatDLToiDa();
            }
        });

        tyLeCommitButton.setVisible(false);
        tyLeCommitButton.setOnAction(ob ->{
            capNhatTyLeNhapXuat();
        });
        tyleTextField.textProperty().addListener(ob ->{
            if(tyleTextField.focusedProperty().get()){
                tyLeCommitButton.setVisible(true);
            }
        });
        tyleTextField.setOnKeyPressed(ob -> {
            if(ob.getCode()== KeyCode.ENTER){
                capNhatTyLeNhapXuat();
            }
        });

        choPhepVuotNoCheckBox.selectedProperty().addListener(ob -> {capNhatChoPhepVuotNo();});
    }

    private void initData(){
        loadQuanData();
        loadLDLData();
        loadTaiKhoanData();
        loadDonViTinhData();

        loadThamSoData();
    }
    private void loadThamSoData(){
        try {
            slDLToiDaTextField.setText(Integer.toString(ThamSoDAO.getInstance().GetDLToiDaMoiQuan()));
            tyleTextField.setText(Double.toString(ThamSoDAO.getInstance().GetTyLeDonGiaXuat()));
            choPhepVuotNoCheckBox.setSelected(ThamSoDAO.getInstance().GetChoPhepVuotNo());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initQuanTable(){
        // Tạo các cột cho TableView
        TableColumn<Quan, String> quanCol = new TableColumn<>("Quận");
        quanCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenQuan()));

        TableColumn<Quan, String> ghiChuCol = new TableColumn<>("Ghi chú");
        ghiChuCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGhiChu()));

        TableColumn<Quan,Quan> sttCol = new TableColumn<>("STT");
        sttCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Quan, Quan>, ObservableValue<Quan>>() {
            @Override public ObservableValue<Quan> call(TableColumn.CellDataFeatures<Quan, Quan> p) {
                return new ReadOnlyObjectWrapper<>(p.getValue());
            }
        });

        sttCol.setCellFactory(new Callback<TableColumn<Quan, Quan>, TableCell<Quan, Quan>>() {
            @Override public TableCell<Quan, Quan> call(TableColumn<Quan, Quan> param) {
                return new TableCell<Quan, Quan>() {
                    @Override protected void updateItem(Quan item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null && item != null) {
                            setText(this.getTableRow().getIndex()+1+"");
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });


        //action column
        TableColumn actionCol = new TableColumn("Thao tác");
        //actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<Quan, String>, TableCell<Quan, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<Quan, String> param) {
                        final TableCell<Quan, String> cell = new TableCell<Quan, String>() {
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
                                        Quan qn = getTableView().getItems().get(getIndex());
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Xóa quận");
                                        alert.setHeaderText("Bạn có chắc chắn muốn xóa quận " + qn.getTenQuan() + " ?");

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK){
                                            try {
                                                QuanDAO.getInstance().Delete(qn.getId());
                                                PopDialog.popSuccessDialog("Xóa quận "+ qn.getTenQuan() + " thành công");
                                            }
                                            catch (SQLException e) {
                                                PopDialog.popErrorDialog("Xóa quận "+ qn.getTenQuan() + " thất bại",e.getMessage());
                                            }
                                        }
                                    });
                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(xoaBtn);
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


        quanCol.setCellFactory(TextFieldTableCell.forTableColumn());
        quanCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Quan, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Quan, String> t) {
                        Quan quan = (Quan) t.getTableView().getItems().get(
                                t.getTablePosition().getRow());
                        try {
                            quan.setTenQuan(t.getNewValue());
                            QuanDAO.getInstance().Update(quan.getId(),quan);
                        } catch (SQLException e) {
                            PopDialog.popErrorDialog("Cập nhật quận thất bại",e.getMessage());
                        }
                        loadQuanData();
                    }
                }
        );
        ghiChuCol.setCellFactory(TextFieldTableCell.forTableColumn());
        ghiChuCol.setOnEditCommit(
                t -> {
                    Quan quan = (Quan) t.getTableView().getItems().get(
                            t.getTablePosition().getRow());
                    try {
                        quan.setGhiChu(t.getNewValue());
                        QuanDAO.getInstance().Update(quan.getId(),quan);

                    } catch (SQLException e) {
                        PopDialog.popErrorDialog("Cập nhật quận thất bại",e.getMessage());
                    }
                    loadQuanData();
                }
        );
        quanTableView.getColumns().addAll(
                sttCol,quanCol,ghiChuCol,actionCol
        );
        quanTableView.widthProperty().addListener(ob ->{
            double width = quanTableView.getWidth();
            sttCol.setPrefWidth(width*0.2);
            quanCol.setPrefWidth(width*0.3);
            ghiChuCol.setPrefWidth(width*0.3);
            actionCol.setPrefWidth(width*0.2);
        });
        quanTableView.setEditable(true);
        quanTableView.setItems(dsQuan);
    }
    private void loadQuanData(){
        dsQuan.clear();
        try {
            dsQuan.addAll(QuanDAO.getInstance().QueryAll());
        } catch (SQLException e) {
            PopDialog.popErrorDialog("Load dữ liệu quận thất bại",e.getMessage());
        }
    }
    private void initQuanDtbBinding(){
        QuanDAO.getInstance().AddDatabaseListener(_ -> loadQuanData());
    }
    private void themMoiQuan(){
        Quan newQuan = new Quan();
            try {
                Quan quanLast = QuanDAO.getInstance().QueryMostRecent();
                Integer id = quanLast.getId();
                while(true){
                    newQuan.setTenQuan("Quận " + id.toString());
                    id++;
                    try{
                        QuanDAO.getInstance().Insert(newQuan);
                        break;
                    } catch (SQLException e) {
                        continue;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }
    private void capNhatDLToiDa(){
        try{
            int sl = Integer.parseInt(slDLToiDaTextField.getText());
            if(sl<=0){
                PopDialog.popErrorDialog("Số lượng phải lớn hơn 0");
            }
            else{
                ThamSoDAO.getInstance().UpdateDLToiDaMoiQuan(sl);
                slDLCommitButton.setVisible(false);
            }
        } catch (NumberFormatException e) {
            PopDialog.popErrorDialog("Định dạng số lượng không hợp lệ!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initLDLTable(){
        // Tạo các cột cho TableView
        TableColumn<LoaiDaiLy, String> ldlCol = new TableColumn<>("Loại đại lý");
        ldlCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenLoai()));

        TableColumn<LoaiDaiLy, String> noToiDaCol = new TableColumn<>("Nợ tối đa");
        noToiDaCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSoNoToiDa().toString()));

        TableColumn<LoaiDaiLy, String> ghiChuCol = new TableColumn<>("Ghi chú");
        ghiChuCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGhiChu()));

        TableColumn<LoaiDaiLy,LoaiDaiLy> sttCol = new TableColumn<>("STT");
        sttCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LoaiDaiLy, LoaiDaiLy>, ObservableValue<LoaiDaiLy>>() {
            @Override public ObservableValue<LoaiDaiLy> call(TableColumn.CellDataFeatures<LoaiDaiLy, LoaiDaiLy> p) {
                return new ReadOnlyObjectWrapper<>(p.getValue());
            }
        });

        sttCol.setCellFactory(new Callback<TableColumn<LoaiDaiLy, LoaiDaiLy>, TableCell<LoaiDaiLy, LoaiDaiLy>>() {
            @Override public TableCell<LoaiDaiLy, LoaiDaiLy> call(TableColumn<LoaiDaiLy, LoaiDaiLy> param) {
                return new TableCell<LoaiDaiLy, LoaiDaiLy>() {
                    @Override protected void updateItem(LoaiDaiLy item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null && item != null) {
                            setText(this.getTableRow().getIndex()+1+"");
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });


        //action column
        TableColumn actionCol = new TableColumn("Thao tác");
        //actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<LoaiDaiLy, String>, TableCell<LoaiDaiLy, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<LoaiDaiLy, String> param) {
                        final TableCell<LoaiDaiLy, String> cell = new TableCell<LoaiDaiLy, String>() {
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
                                        LoaiDaiLy ldl = getTableView().getItems().get(getIndex());
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Xóa quận");
                                        alert.setHeaderText("Bạn có chắc chắn muốn xóa loại đại lý " + ldl.getTenLoai() + " ?");

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK){
                                            try {
                                                LoaiDaiLyDAO.getInstance().Delete(ldl.getId());
                                                PopDialog.popSuccessDialog("Xóa loại đại lý "+ ldl.getTenLoai() + " thành công");
                                            }
                                            catch (SQLException e) {
                                                PopDialog.popErrorDialog("Xóa quận "+ ldl.getTenLoai() + " thất bại",e.getMessage());
                                            }
                                        }
                                    });
                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(xoaBtn);
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


        ldlCol.setCellFactory(TextFieldTableCell.forTableColumn());
        ldlCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<LoaiDaiLy, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<LoaiDaiLy, String> t) {
                        LoaiDaiLy ldl = (LoaiDaiLy) t.getTableView().getItems().get(
                                t.getTablePosition().getRow());
                        try {
                            ldl.setTenLoai(t.getNewValue());
                            LoaiDaiLyDAO.getInstance().Update(ldl.getId(),ldl);
                        } catch (SQLException e) {
                            PopDialog.popErrorDialog("Cập nhật quận thất bại",e.getMessage());
                        }
                        loadLDLData();
                    }
                }
        );

        noToiDaCol.setCellFactory(TextFieldTableCell.forTableColumn());
        noToiDaCol.setOnEditCommit(
                t -> {
                    LoaiDaiLy ldl = (LoaiDaiLy) t.getTableView().getItems().get(
                            t.getTablePosition().getRow());
                    try {
                        ldl.setSoNoToiDa(Integer.parseInt(t.getNewValue()));
                        LoaiDaiLyDAO.getInstance().Update(ldl.getId(), ldl);
                    }
                    catch (NumberFormatException e) {
                        PopDialog.popErrorDialog("Không đúng định dạng", e.getMessage());
                    }
                    catch (SQLException e) {
                        PopDialog.popErrorDialog("Cập nhật số tiền thất bại", e.getMessage());
                    }
                    loadLDLData();
                }
        );

        ghiChuCol.setCellFactory(TextFieldTableCell.forTableColumn());
        ghiChuCol.setOnEditCommit(
                t -> {
                    LoaiDaiLy ldl = (LoaiDaiLy) t.getTableView().getItems().get(
                            t.getTablePosition().getRow());
                    try {
                        ldl.setGhiChu(t.getNewValue());
                        LoaiDaiLyDAO.getInstance().Update(ldl.getId(),ldl);

                    } catch (SQLException e) {
                        PopDialog.popErrorDialog("Cập nhật quận thất bại",e.getMessage());
                    }
                    loadQuanData();
                }
        );
        ldlTableView.getColumns().addAll(
                sttCol,ldlCol,noToiDaCol,ghiChuCol,actionCol
        );
        ldlTableView.widthProperty().addListener(ob ->{
            double width = quanTableView.getWidth();
            sttCol.setPrefWidth(width*0.1);
            ldlCol.setPrefWidth(width*0.25);
            noToiDaCol.setPrefWidth(width*0.25);
            ghiChuCol.setPrefWidth(width*0.3);
            actionCol.setPrefWidth(width*0.1);
        });
        ldlTableView.setEditable(true);
        ldlTableView.setItems(dsLoaiDaiLy);
    }
    private void loadLDLData(){
        dsLoaiDaiLy.clear();
        try {
            dsLoaiDaiLy.addAll(LoaiDaiLyDAO.getInstance().QueryAll());
        } catch (SQLException e) {
            PopDialog.popErrorDialog("Load dữ liệu loại đại lý thất bại",e.getMessage());
        }
    }
    private void initLDLDtbBinding(){
        LoaiDaiLyDAO.getInstance().AddDatabaseListener(_ -> loadLDLData());
    }
    private void themMoiLDL(){
        LoaiDaiLy newLDL = new LoaiDaiLy();
        try {
            LoaiDaiLy ldlLast = LoaiDaiLyDAO.getInstance().QueryMostRecent();
            Integer id = ldlLast.getId();
            while(true){
                newLDL.setTenLoai("Loại " + id.toString());
                newLDL.setSoNoToiDa(500000);
                id++;
                try{
                    LoaiDaiLyDAO.getInstance().Insert(newLDL);
                    break;
                } catch (SQLException _) {}
            }
        } catch (SQLException _) {}
    }

    private void initTaiKhoanTable(){
        // Tạo các cột cho TableView
        TableColumn<TaiKhoan, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserName()));

        TableColumn<TaiKhoan, String> nhanVienCol = new TableColumn<>("Nhân viên");
        nhanVienCol.setCellValueFactory(data -> {
            try{
                NhanVien nv = NhanVienDAO.getInstance().QueryID(data.getValue().getMaNhanVien());
                return new SimpleStringProperty(nv.getMaNhanVien()+ " - " + nv.getHoTen());
            } catch (SQLException _) {}
            return null;
        });
        TableColumn<TaiKhoan,TaiKhoan> sttCol = new TableColumn<>("STT");

        sttCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TaiKhoan, TaiKhoan>, ObservableValue<TaiKhoan>>() {
            @Override public ObservableValue<TaiKhoan> call(TableColumn.CellDataFeatures<TaiKhoan, TaiKhoan> p) {
                return new ReadOnlyObjectWrapper<>(p.getValue());
            }
        });
        sttCol.setCellFactory(new Callback<TableColumn<TaiKhoan, TaiKhoan>, TableCell<TaiKhoan, TaiKhoan>>() {
            @Override public TableCell<TaiKhoan, TaiKhoan> call(TableColumn<TaiKhoan, TaiKhoan> param) {
                return new TableCell<TaiKhoan, TaiKhoan>() {
                    @Override protected void updateItem(TaiKhoan item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null && item != null) {
                            setText(this.getTableRow().getIndex()+1+"");
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });


        //action column
        TableColumn actionCol = new TableColumn("Thao tác");
        //actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<TaiKhoan, String>, TableCell<TaiKhoan, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<TaiKhoan, String> param) {
                        final TableCell<TaiKhoan, String> cell = new TableCell<TaiKhoan, String>() {
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
                                        TaiKhoan tk = getTableView().getItems().get(getIndex());
                                        NhanVien nv = null;
                                        try{
                                            nv = NhanVienDAO.getInstance().QueryID(tk.getMaNhanVien());
                                        } catch (SQLException _) {}
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Xóa tài khoản");
                                        alert.setHeaderText("Bạn có chắc chắn muốn xóa tài khoản " + tk.getUserName() + " của nhân viên "+nv.getHoTen()+ " ?");

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK){
                                            try {
                                                TaiKhoanDAO.getInstance().Delete(tk.getUserName());
                                                PopDialog.popSuccessDialog("Xóa tài khoản  " + tk.getUserName() + " của nhân viên "+nv.getHoTen() + " thành công");
                                            }
                                            catch (SQLException e) {
                                                PopDialog.popErrorDialog("Xóa tài khoản  "+  tk.getUserName() + " của nhân viên "+nv.getHoTen() +" thất bại",e.getMessage());
                                            }
                                        }
                                    });
                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(xoaBtn);
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
        tkTableView.getColumns().addAll(
                sttCol,usernameCol,nhanVienCol,actionCol
        );
        tkTableView.widthProperty().addListener(ob ->{
            double width = tkTableView.getWidth();
            sttCol.setPrefWidth(width*0.2);
            usernameCol.setPrefWidth(width*0.3);
            nhanVienCol.setPrefWidth(width*0.3);
            actionCol.setPrefWidth(width*0.2);
        });
        tkTableView.setEditable(true);
        tkTableView.setItems(dsTaiKhoan);
    }
    private void loadTaiKhoanData(){
        dsTaiKhoan.clear();
        try {
            dsTaiKhoan.addAll(TaiKhoanDAO.getInstance().QueryAll());
        } catch (SQLException e) {
            PopDialog.popErrorDialog("Load dữ liệu tài khoản thất bại",e.getMessage());
        }
    }
    private void initTaiKhoanDtbBinding(){
        TaiKhoanDAO.getInstance().AddDatabaseListener(_ -> loadTaiKhoanData());
    }
    private void OpenThemMoiTKDialog(){
        try {
            new ThemTKDialog().showAndWait().ifPresent(
                    taiKhoan -> {
                        try {
                            TaiKhoanDAO.getInstance().Insert(taiKhoan);
                            PopDialog.popSuccessDialog("Thêm mới tài khoản thành công");
                        }
                        catch (SQLException e) {
                            PopDialog.popErrorDialog("Thêm mới tài khoản thất bại", e.getMessage());
                        }
                    }
            );
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Không thể mở dialog thêm tài khoản");
        }
    }


    private void initDonViTinhTable(){
        // Tạo các cột cho TableView
        TableColumn<DonViTinh, String> donViTinhCol = new TableColumn<>("Đơn vị tính");
        donViTinhCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenDVT()));

        TableColumn<DonViTinh, String> ghiChuCol = new TableColumn<>("Ghi chú");
        ghiChuCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGhiChu()));

        TableColumn<DonViTinh,DonViTinh> sttCol = new TableColumn<>("STT");
        sttCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DonViTinh, DonViTinh>, ObservableValue<DonViTinh>>() {
            @Override public ObservableValue<DonViTinh> call(TableColumn.CellDataFeatures<DonViTinh, DonViTinh> p) {
                return new ReadOnlyObjectWrapper<>(p.getValue());
            }
        });

        sttCol.setCellFactory(new Callback<TableColumn<DonViTinh, DonViTinh>, TableCell<DonViTinh, DonViTinh>>() {
            @Override public TableCell<DonViTinh, DonViTinh> call(TableColumn<DonViTinh, DonViTinh> param) {
                return new TableCell<DonViTinh, DonViTinh>() {
                    @Override protected void updateItem(DonViTinh item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null && item != null) {
                            setText(this.getTableRow().getIndex()+1+"");
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });


        //action column
        TableColumn actionCol = new TableColumn("Thao tác");
        //actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<DonViTinh, String>, TableCell<DonViTinh, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<DonViTinh, String> param) {
                        final TableCell<DonViTinh, String> cell = new TableCell<DonViTinh, String>() {
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
                                        DonViTinh dvt = getTableView().getItems().get(getIndex());
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Xóa quận");
                                        alert.setHeaderText("Bạn có chắc chắn muốn xóa đơn vị tính " + dvt.getTenDVT() + " ?");

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK){
                                            try {
                                                DonViTinhDAO.getInstance().Delete(dvt.getId());
                                                PopDialog.popSuccessDialog("Xóa đơn vị tính "+ dvt.getTenDVT() + " thành công");
                                            }
                                            catch (SQLException e) {
                                                PopDialog.popErrorDialog("Xóa đơn vị tính "+ dvt.getTenDVT() + " thất bại",e.getMessage());
                                            }
                                        }
                                    });
                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(xoaBtn);
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


        donViTinhCol.setCellFactory(TextFieldTableCell.forTableColumn());
        donViTinhCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<DonViTinh, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<DonViTinh, String> t) {
                        DonViTinh donViTinh = (DonViTinh) t.getTableView().getItems().get(
                                t.getTablePosition().getRow());
                        try {
                            donViTinh.setTenDVT(t.getNewValue());
                            DonViTinhDAO.getInstance().Update(donViTinh.getId(),donViTinh);
                        } catch (SQLException e) {
                            PopDialog.popErrorDialog("Cập nhật đơn vị tính thất bại",e.getMessage());
                        }
                        loadDonViTinhData();
                    }
                }
        );
        ghiChuCol.setCellFactory(TextFieldTableCell.forTableColumn());
        ghiChuCol.setOnEditCommit(
                t -> {
                    DonViTinh dvt = (DonViTinh) t.getTableView().getItems().get(
                            t.getTablePosition().getRow());
                    try {
                        dvt.setGhiChu(t.getNewValue());
                        DonViTinhDAO.getInstance().Update(dvt.getId(),dvt);

                    } catch (SQLException e) {
                        PopDialog.popErrorDialog("Cập nhật đơn vị tính thất bại",e.getMessage());
                    }
                    loadDonViTinhData();
                }
        );
        donViTinhTableView.getColumns().addAll(
                sttCol,donViTinhCol,ghiChuCol,actionCol
        );
        donViTinhTableView.widthProperty().addListener(ob ->{
            double width = donViTinhTableView.getWidth();
            sttCol.setPrefWidth(width*0.2);
            donViTinhCol.setPrefWidth(width*0.3);
            ghiChuCol.setPrefWidth(width*0.3);
            actionCol.setPrefWidth(width*0.2);
        });
        donViTinhTableView.setEditable(true);
        donViTinhTableView.setItems(dsDonViTinh);
    }
    private void loadDonViTinhData(){
        dsDonViTinh.clear();
        try {
            dsDonViTinh.addAll(DonViTinhDAO.getInstance().QueryAll());
        } catch (SQLException e) {
            PopDialog.popErrorDialog("Load dữ liệu dơn vị tính thất bại",e.getMessage());
        }
    }
    private void initDonViTinhDtbBinding(){
        DonViTinhDAO.getInstance().AddDatabaseListener(_ -> loadDonViTinhData());
    }
    private void themMoiDonViTinh(){
        DonViTinh newDonViTinh = new DonViTinh();
        try {
            DonViTinh donViTinhLast = DonViTinhDAO.getInstance().QueryMostRecent();
            Integer id = donViTinhLast.getId();
            while(true){
                newDonViTinh.setTenDVT("Đơn vị tính " + id.toString());
                id++;
                try{
                    DonViTinhDAO.getInstance().Insert(newDonViTinh);
                    break;
                } catch (SQLException e) {
                    continue;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    private void capNhatTyLeNhapXuat(){
        try{
            double tl = Double.parseDouble(tyleTextField.getText());
            if(tl<=0){
                PopDialog.popErrorDialog("Tỷ lệ nhập xuất phải lớn hơn 0");
            }
            else{
                ThamSoDAO.getInstance().UpdateTyLeDonGiaXuat(tl);
                tyLeCommitButton.setVisible(false);
            }
        } catch (NumberFormatException e) {
            PopDialog.popErrorDialog("Định dạng số lượng không hợp lệ!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void capNhatChoPhepVuotNo(){
        try {
            ThamSoDAO.getInstance().UpdateChoPhepVuotNo(choPhepVuotNoCheckBox.isSelected());
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
