module org.example.demofx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires java.logging;
    requires java.sql;
    requires MaterialFX;
    requires org.apache.poi.ooxml;

    opens org.doancnpm to javafx.fxml;
    opens org.doancnpm.login to javafx.fxml;
    opens org.doancnpm.main to javafx.fxml;
    opens org.doancnpm.Models to javafx.base;
    exports org.doancnpm;
    exports org.doancnpm.main;
    exports org.doancnpm.Models;
}