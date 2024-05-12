package org.doancnpm.ManHinhPhieuThu;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ManHinhPhieuThuController implements Initializable {
    @FXML Node manHinhPhieuThu;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void setVisibility(boolean visibility){
        manHinhPhieuThu.setVisible(visibility);
    }
}
