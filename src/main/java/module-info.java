module org.example.demofx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires java.logging;
    requires java.sql;
    requires MaterialFX;
    requires org.apache.poi.ooxml;
    requires java.prefs;
    requires java.mail;

    opens org.doancnpm to javafx.fxml;
    opens org.doancnpm.Login to javafx.fxml;
    opens org.doancnpm.ManHinhDaiLy to javafx.fxml;
    opens org.doancnpm.ManHinhPhieuThu to javafx.fxml;
    opens org.doancnpm.ManHinhTaiKhoan to javafx.fxml;
    opens org.doancnpm.Models to javafx.base;
    opens org.doancnpm.Main to javafx.fxml;

    exports org.doancnpm;
    exports org.doancnpm.ManHinhDaiLy;
    exports org.doancnpm.ManHinhPhieuThu;
    exports org.doancnpm.ManHinhTaiKhoan;
    exports org.doancnpm.Models;
    exports org.doancnpm.Main;
}