package site.scalarstudios.scalarbudget.app;

import javafx.application.Application;
import javafx.stage.Stage;
import site.scalarstudios.scalarbudget.model.BudgetItem;
import site.scalarstudios.scalarbudget.scenes.DisplayScene;
import site.scalarstudios.scalarbudget.scenes.LoadingScene;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The ScalarBudgetApplication class is the main JavaFX application class for the ScalarBudget program.
 * It manages the primary stage and transitions between different scenes such as loading and displaying budget data.
 *
 * @author Lemon_Juiced
 */
public class ScalarBudgetApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Callback for successful budget load
        BiConsumer<List<BudgetItem>, File> onBudgetLoaded = (budgetItems, file) -> {
            DisplayScene displayScene = new DisplayScene(budgetItems, file);
            primaryStage.setScene(displayScene.getScene());
        };
        // Callback for error
        Consumer<String> onError = errorMessage -> {
            DisplayScene displayScene = new DisplayScene(errorMessage);
            primaryStage.setScene(displayScene.getScene());
        };
        // Show loading scene
        LoadingScene loadingScene = new LoadingScene(primaryStage, onBudgetLoaded, onError);
        primaryStage.setTitle("Scalar Budget");
        // Set application icon
        primaryStage.getIcons().add(
            new javafx.scene.image.Image(
                getClass().getResourceAsStream("/site/scalarstudios/scalarbudget/assets/icon.png")
            )
        );
        primaryStage.setScene(loadingScene.getScene());
        primaryStage.show();
    }
}