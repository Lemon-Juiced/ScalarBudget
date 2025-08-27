package site.scalarstudios.scalarbudget.scenes;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import site.scalarstudios.scalarbudget.model.BudgetItem;
import site.scalarstudios.scalarbudget.loader.BudgetLoader;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The LoadingScene class represents the initial scene of the ScalarBudget application.
 * It provides a user interface for loading a budget JSON file and handles file selection
 * and loading logic, including error handling.
 *
 * @author Lemon_Juiced
 */
public class LoadingScene {
    private final Scene scene;

    public LoadingScene(Stage stage, BiConsumer<List<BudgetItem>, File> onBudgetLoaded, Consumer<String> onError) {
        Label infoLabel = new Label("Please load a budget JSON file.");
        Button loadButton = new Button("Load File");
        Button createButton = new Button("Create New");

        loadButton.setStyle("-fx-background-color: #2d333b; -fx-text-fill: #7fffd4; -fx-font-size: 16; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #444c56;");
        infoLabel.setStyle("-fx-text-fill: #e0e6ed; -fx-font-size: 16;");

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Budget JSON File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON Files", "*.json")
            );
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                List<BudgetItem> budgetItems = BudgetLoader.loadBudgetFile(selectedFile);
                if (!budgetItems.isEmpty()) {
                    onBudgetLoaded.accept(budgetItems, selectedFile);
                } else {
                    onError.accept("Failed to load or parse file: " + selectedFile.getName());
                }
            }
        });

        createButton.setStyle("-fx-background-color: #2d333b; -fx-text-fill: #7fffd4; -fx-font-size: 16; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #444c56;");
        createButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Create New Budget JSON File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON Files", "*.json")
            );
            File newFile = fileChooser.showSaveDialog(stage);
            if (newFile != null) {
                try {
                    // Write an empty array as the initial content
                    java.nio.file.Files.write(newFile.toPath(), "[]".getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    onBudgetLoaded.accept(java.util.Collections.emptyList(), newFile);
                } catch (Exception ex) {
                    onError.accept("Failed to create file: " + newFile.getName());
                }
            }
        });

        VBox layout = new VBox(20, infoLabel, loadButton, createButton);
        layout.setStyle("-fx-alignment: center; -fx-background-color: #181a20;");
        this.scene = new Scene(layout, 400, 300);
        this.scene.setFill(javafx.scene.paint.Color.web("#181a20"));
    }

    public Scene getScene() {
        return scene;
    }
}
