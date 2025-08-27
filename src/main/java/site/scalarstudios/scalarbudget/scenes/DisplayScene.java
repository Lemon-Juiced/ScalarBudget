package site.scalarstudios.scalarbudget.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import site.scalarstudios.scalarbudget.model.BudgetItem;
import site.scalarstudios.scalarbudget.model.Period;
import site.scalarstudios.scalarbudget.loader.BudgetSaver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The DisplayScene class represents the main UI scene for displaying and editing budget items.
 * It provides functionality to add, edit, and save budget items, as well as calculate total amounts
 * normalized to a selected period.
 *
 * @author Lemon_Juiced
 */
public class DisplayScene {
    private final Scene scene;

    public DisplayScene(List<BudgetItem> budgetItems, File jsonFile) {
        // Use BudgetItemRow for each item
        List<BudgetItemRow> itemRows = new ArrayList<>();

        final Runnable[] updateTotal = new Runnable[1];

        ComboBox<Period> totalPeriodBox = new ComboBox<>();
        totalPeriodBox.getItems().addAll(Period.getAll());
        totalPeriodBox.setValue(Period.MONTH);
        totalPeriodBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Period item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDisplayName());
                setStyle("-fx-text-fill: #e0e6ed; -fx-background-color: #23272e;");
            }
        });
        totalPeriodBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Period item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDisplayName());
                setStyle("-fx-text-fill: #e0e6ed; -fx-background-color: #23272e;");
            }
        });
        totalPeriodBox.setStyle("-fx-background-color: #23272e; -fx-text-fill: #e0e6ed; -fx-border-color: #444c56; -fx-font-size: 16; -fx-font-weight: bold;");
        Label totalLabel = new Label();
        totalLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 20; -fx-font-weight: bold; -fx-padding: 0 0 0 16;");
        Label totalTextLabel = new Label("Total:");
        totalTextLabel.setStyle("-fx-text-fill: #e0e6ed; -fx-font-size: 18; -fx-font-weight: bold;");
        Label perLabel = new Label("per");
        perLabel.setStyle("-fx-text-fill: #e0e6ed; -fx-font-size: 16; -fx-font-weight: bold;");
        HBox totalBox = new HBox(16, totalTextLabel, totalLabel, perLabel, totalPeriodBox);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        totalBox.setStyle("-fx-background-color: transparent; -fx-padding: 0 0 0 0;");

        VBox itemsBox = new VBox(16);
        itemsBox.setStyle("-fx-padding: 32 32 32 32; -fx-background-color: #181a20;"); // Add more padding
        itemsBox.setMaxWidth(Double.MAX_VALUE);

        // Assign the real updateTotal after all UI components are created
        updateTotal[0] = () -> {
            Period selectedPeriod = totalPeriodBox.getValue();
            double selectedDays = selectedPeriod.getDays();
            double total = 0.0;
            for (BudgetItemRow row : itemRows) {
                String amtStr = row.getAmountField().getText();
                Period per = row.getPeriodBox().getValue();
                if (amtStr.isEmpty() || !amtStr.matches("\\d+(\\.\\d+)?")) continue;
                double amt = Double.parseDouble(amtStr);
                double perDays = per.getDays();
                double normalized = amt * (selectedDays / perDays);
                total += normalized;
            }
            totalLabel.setText(String.format("%.2f", total));
        };

        for (BudgetItem item : budgetItems) {
            BudgetItemRow row = new BudgetItemRow(
                item,
                rowToDelete -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Delete Item");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Are you sure you want to delete '" + rowToDelete.getBudgetItem().getName() + "'?");
                    confirm.getDialogPane().setStyle("-fx-background-color: #23272e; -fx-text-fill: #e0e6ed;");
                    confirm.showAndWait().ifPresent(result -> {
                        if (result == ButtonType.OK) {
                            int idx = itemRows.indexOf(rowToDelete);
                            if (idx >= 0) {
                                itemRows.remove(idx);
                                budgetItems.remove(rowToDelete.getBudgetItem());
                                itemsBox.getChildren().remove(rowToDelete);
                                updateTotal[0].run();
                            }
                        }
                    });
                },
                rowToEdit -> {
                    int idx = itemRows.indexOf(rowToEdit);
                    if (idx < 0) return;
                    final AddItemEditor[] editorRefArr = new AddItemEditor[1];
                    editorRefArr[0] = new AddItemEditor((editedItem, editorRef) -> {
                        // Update the row in place
                        rowToEdit.updateModel();
                        BudgetItemRow newRow = new BudgetItemRow(editedItem, rowToDelete -> {
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                            confirm.setTitle("Delete Item");
                            confirm.setHeaderText(null);
                            confirm.setContentText("Are you sure you want to delete '" + rowToDelete.getBudgetItem().getName() + "'?");
                            confirm.getDialogPane().setStyle("-fx-background-color: #23272e; -fx-text-fill: #e0e6ed;");
                            confirm.showAndWait().ifPresent(result -> {
                                if (result == ButtonType.OK) {
                                    int idx2 = itemRows.indexOf(rowToDelete);
                                    if (idx2 >= 0) {
                                        itemRows.remove(idx2);
                                        budgetItems.remove(rowToDelete.getBudgetItem());
                                        itemsBox.getChildren().remove(rowToDelete);
                                        updateTotal[0].run();
                                    }
                                }
                            });
                        }, thisRow -> {
                            // Recursively allow editing again
                            int idx2 = itemRows.indexOf(thisRow);
                            if (idx2 >= 0) {
                                itemsBox.getChildren().set(idx2, editorRefArr[0]);
                            }
                        });
                        itemRows.set(idx, newRow);
                        itemsBox.getChildren().set(idx, newRow);
                        updateTotal[0].run();
                    }, () -> {
                        // Cancel: restore the original row
                        itemsBox.getChildren().set(idx, rowToEdit);
                    }, rowToEdit.getBudgetItem());
                    itemsBox.getChildren().set(idx, editorRefArr[0]);
                }
            );
            itemRows.add(row);
            // Add listeners for updates
            row.getAmountField().textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*(\\.\\d*)?")) {
                    row.getAmountField().setText(oldVal);
                }
                updateTotal[0].run();
            });
            row.getPeriodBox().valueProperty().addListener((obs, oldVal, newVal) -> updateTotal[0].run());
            itemsBox.getChildren().add(row);
        }

        // --- Add Item Row ---
        HBox addRow = new HBox();
        addRow.setAlignment(Pos.CENTER);
        addRow.setStyle("-fx-background-color: #23272e; -fx-border-color: #333842; -fx-border-radius: 8; -fx-background-radius: 8;");
        addRow.setPadding(new Insets(10));
        addRow.setMaxWidth(Double.MAX_VALUE);
        Button addButton = new Button("+ Add Item");
        addButton.setStyle("-fx-background-color: #23272e; -fx-text-fill: #7fffd4; -fx-font-size: 16; -fx-font-weight: bold; -fx-border-color: #444c56; -fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 8 32;");
        addRow.getChildren().add(addButton);
        itemsBox.getChildren().add(addRow);

        // --- Add Item Logic ---
        addButton.setOnAction(e -> {
            int addRowIndex = itemsBox.getChildren().indexOf(addRow);
            final AddItemEditor[] editorRefArr = new AddItemEditor[1];
            editorRefArr[0] = new AddItemEditor((newItem, editorRef) -> {
                budgetItems.add(newItem);
                BudgetItemRow newRow = new BudgetItemRow(newItem, rowToDelete -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Delete Item");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Are you sure you want to delete '" + rowToDelete.getBudgetItem().getName() + "'?");
                    confirm.getDialogPane().setStyle("-fx-background-color: #23272e; -fx-text-fill: #e0e6ed;");
                    confirm.showAndWait().ifPresent(result -> {
                        if (result == ButtonType.OK) {
                            int idx = itemRows.indexOf(rowToDelete);
                            if (idx >= 0) {
                                itemRows.remove(idx);
                                budgetItems.remove(rowToDelete.getBudgetItem());
                                itemsBox.getChildren().remove(rowToDelete);
                                updateTotal[0].run();
                            }
                        }
                    });
                }, thisRow -> {
                    // Recursively allow editing again
                    int idx2 = itemRows.indexOf(thisRow);
                    if (idx2 >= 0) {
                        itemsBox.getChildren().set(idx2, editorRefArr[0]);
                    }
                });
                itemRows.add(newRow);
                // Add listeners for new fields
                newRow.getAmountField().textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.matches("\\d*(\\.\\d*)?")) {
                        newRow.getAmountField().setText(oldVal);
                    }
                    updateTotal[0].run();
                });
                newRow.getPeriodBox().valueProperty().addListener((obs, oldVal, newVal) -> updateTotal[0].run());
                itemsBox.getChildren().set(addRowIndex, newRow);
                // Re-add the addRow at the end
                itemsBox.getChildren().add(addRow);
                updateTotal[0].run();
            }, () -> {
                itemsBox.getChildren().set(addRowIndex, addRow);
            });
            itemsBox.getChildren().set(addRowIndex, editorRefArr[0]);
        });

        ScrollPane scrollPane = new ScrollPane(itemsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #181a20; -fx-border-color: #23272e;");
        scrollPane.getContent().setStyle("-fx-background-color: #181a20;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFocusTraversable(false);

        // Add listeners after updateTotal is defined
        totalPeriodBox.valueProperty().addListener((obs, oldVal, newVal) -> updateTotal[0].run());

        // Save button
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #2d333b; -fx-text-fill: #e0e6ed; -fx-font-size: 16; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #444c56;");
        saveButton.setOnAction(e -> {
            boolean valid = true;
            for (BudgetItemRow row : itemRows) {
                String text = row.getAmountField().getText();
                if (text.isEmpty() || !text.matches("\\d+(\\.\\d+)?")) {
                    valid = false;
                    row.getAmountField().setStyle(row.getAmountField().getStyle() + ";-fx-border-color: #ff6b6b;");
                }
            }
            if (!valid) {
                showAlert("Validation Error", "Please enter valid numeric amounts.");
                return;
            }
            // Update model
            for (BudgetItemRow row : itemRows) {
                row.updateModel();
            }
            boolean success = BudgetSaver.saveBudgetItems(jsonFile, budgetItems);
            if (success) {
                showAlert("Success", "Budget saved successfully.");
            } else {
                showAlert("Error", "Failed to save budget to file.");
            }
        });

        // --- Bottom bar layout ---
        HBox bottomBar = new HBox();
        bottomBar.setStyle("-fx-background-color: #181a20; -fx-padding: 16 24 16 24;");
        bottomBar.setSpacing(16);
        bottomBar.setAlignment(Pos.CENTER);
        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);
        bottomBar.getChildren().addAll(saveButton, bottomSpacer, totalBox);

        BorderPane root = new BorderPane();
        root.setTop(null);
        root.setCenter(scrollPane);
        BorderPane.setMargin(scrollPane, new Insets(24, 24, 24, 24)); // Add margin around the scroll pane
        root.setBottom(bottomBar);
        BorderPane.setAlignment(bottomBar, Pos.CENTER);
        root.setStyle("-fx-background-color: #181a20;");

        this.scene = new Scene(root, 1280, 720);
        this.scene.setFill(Color.web("#181a20"));

        // Initial calculation
        updateTotal[0].run();
    }

    // Constructor for displaying an error message
    public DisplayScene(String errorMessage) {
        Label errorLabel = new Label(errorMessage);
        errorLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 18; -fx-font-weight: bold;");
        StackPane errorLayout = new StackPane(errorLabel);
        errorLayout.setStyle("-fx-background-color: #181a20;");
        this.scene = new Scene(errorLayout, 400, 300);
        this.scene.setFill(Color.web("#181a20"));
    }

    public Scene getScene() {
        return scene;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #23272e; -fx-text-fill: #e0e6ed;");
        alert.showAndWait();
    }
}
