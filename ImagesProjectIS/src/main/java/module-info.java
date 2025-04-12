module com.example.imagesprojectis {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;



    opens com.example.imagesprojectis to javafx.fxml;
    exports com.example.imagesprojectis;
}