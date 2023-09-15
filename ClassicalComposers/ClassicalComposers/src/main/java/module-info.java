module com.example.classicalcomposers {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.classicalcomposers to javafx.fxml;
    exports com.example.classicalcomposers;
}