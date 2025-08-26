package site.scalarstudios.scalarbudget.app;

import javafx.application.Application;

/**
 * The Launcher class serves as the entry point for the ScalarBudget application.
 * It launches the JavaFX application by invoking the Application.launch() method
 * with the ScalarBudgetApplication class and command-line arguments.
 *
 * @author Lemon_Juiced
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(ScalarBudgetApplication.class, args);
    }
}
