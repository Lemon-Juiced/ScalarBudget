module site.scalarstudios.scalarbudget {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;


    exports site.scalarstudios.scalarbudget.scenes;
    opens site.scalarstudios.scalarbudget.scenes to javafx.fxml;
    exports site.scalarstudios.scalarbudget.app;
    opens site.scalarstudios.scalarbudget.app to javafx.fxml;
    exports site.scalarstudios.scalarbudget.model;
    opens site.scalarstudios.scalarbudget.model to javafx.fxml;
    exports site.scalarstudios.scalarbudget.loader;
    opens site.scalarstudios.scalarbudget.loader to javafx.fxml;
}