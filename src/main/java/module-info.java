module com.example.car.etlapdemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.car.etlapdemo to javafx.fxml;
    exports com.example.car.etlapdemo;
}