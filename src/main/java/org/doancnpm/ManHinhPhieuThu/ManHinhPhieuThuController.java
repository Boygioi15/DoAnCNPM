package org.doancnpm.ManHinhPhieuThu;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import javafx.util.Callback;

import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.DAO.PhieuThuDao;

import org.doancnpm.ManHinhDaiLy.DirectAddDialog;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.PhieuThu;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.PopDialog;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManHinhPhieuThuController implements Initializable {
    public MenuItem addDirectButton;
    @FXML
    Node manHinhPhieuThu;
    @FXML
    private TableView mainTableView;


    private final ObservableList<PhieuThu> dsPhieuThu = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTableView();
        initEvent();
        initDatabaseBinding();
        initUIDataBinding();
    }

    private void initEvent() {
        addDirectButton.setOnAction(_ -> {
            OpenDirectAddDialog();
        });
    }

    private void OpenDirectAddDialog() {
        try {
            new LapPhieuThuDialog().showAndWait().ifPresent(
                    phieuThuAdded -> {
                        try {
                            PhieuThuDao.getInstance().Insert(phieuThuAdded);
                            PopDialog.popSuccessDialog("Thêm mới thành công");
                        }
                        catch (SQLException e) {
                            PopDialog.popErrorDialog("Thêm mới đại lý thất bại", e.toString());
                        }
                    }
            );
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Không thể mở dialog thêm đại lý", e.toString());
        }
    }

    public void initTableView() {
        // Tạo các cột cho TableView
        TableColumn<PhieuThu, String> maPTCol = new TableColumn<>("Mã Phiếu Thu");
        maPTCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaPhieuThu()));

        TableColumn<PhieuThu, String> maDLCol = new TableColumn<>("Mã đại lý");
        maDLCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaDaiLi()));

        TableColumn<PhieuThu, String> maNVCol = new TableColumn<>("Mã Nhân Viên");
        maDLCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaNhanVien()));

        TableColumn<PhieuThu, String> ngayCol = new TableColumn<>("Ngày lập phiếu ");
        ngayCol.setCellValueFactory(data -> {
            Date ngay = data.getValue().getNgayLap();
            return new SimpleObjectProperty<>(DayFormat.GetDayStringFormatted(ngay));
        });

        TableColumn<PhieuThu, Integer> tongTienThuCol = new TableColumn<>("Tổng tiền thu");
        tongTienThuCol.setCellValueFactory(new PropertyValueFactory<>("SoTienThu"));

        TableColumn<PhieuThu, String> ghiChuCol = new TableColumn<>("Ghi chú");
        ghiChuCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGhiChu()));

        TableColumn<PhieuThu, Boolean> selectedCol = new TableColumn<>("Selected");
        selectedCol.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedCol.setCellFactory(tc -> new CheckBoxTableCell<>());

        //action column
        TableColumn actionCol = new TableColumn("Action");
        //actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<PhieuThu, String>, TableCell<PhieuThu, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<PhieuThu, String> param) {
                        final TableCell<PhieuThu, String> cell = new TableCell<PhieuThu, String>() {
                            final Button suaBtn = new javafx.scene.control.Button("Sửa");
                            final Button xoaBtn = new javafx.scene.control.Button("Xóa");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    xoaBtn.setOnAction(event -> {
                                        PhieuThu pt = getTableView().getItems().get(getIndex());
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Xóa phiếu thu");

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK) {
                                            try {
                                                PhieuThuDao.getInstance().Delete(pt.getID());
                                                PopDialog.popSuccessDialog("Xóa phiếu thu" + " thành công");
                                            } catch (SQLException e) {
                                                PopDialog.popErrorDialog("Xóa phiếu thu" + " thất bại", e.toString());
                                            }
                                        }
                                    });
                                    suaBtn.setOnAction(_ -> {
                                        try {
                                            PhieuThu phieuThu = getTableView().getItems().get(getIndex());
                                            new LapPhieuThuDialog(phieuThu).showAndWait().ifPresent(_ -> {
                                                try {
                                                    PhieuThuDao.getInstance().Update(phieuThu.getID(),phieuThu);
                                                    PopDialog.popSuccessDialog("Cập nhật phiếu thu tiền "+phieuThu.getMaPhieuThu()+" thành công");
                                                } catch (SQLException e) {
                                                    PopDialog.popErrorDialog("Cập nhật phiếu thu tiền "+phieuThu.getMaPhieuThu()+" thất bại",
                                                            e.toString());
                                                }
                                                //mainTableView.getItems().set(selectedIndex, response);
                                            });
                                        } catch(IOException exc) {
                                            exc.printStackTrace();
                                        }
                                    });
                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(suaBtn, xoaBtn);
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
                maPTCol,
                maDLCol,
                maNVCol,
                ngayCol,
                tongTienThuCol,
                ghiChuCol

        );
        mainTableView.setEditable(true);
        mainTableView.setPrefWidth(1100);
    }

    public void setVisibility(boolean visibility) {
        manHinhPhieuThu.setVisible(visibility);
    }

    private void initDatabaseBinding() {
        PhieuThuDao.getInstance().AddDatabaseListener(_ -> updateListFromDatabase());

    }

    private void initUIDataBinding() {
        mainTableView.setItems(dsPhieuThu);
    }

    private void updateListFromDatabase() {
        dsPhieuThu.clear();
        try {
            dsPhieuThu.addAll(PhieuThuDao.getInstance().QueryAll());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
