package site.scalarstudios.scalarbudget.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import site.scalarstudios.scalarbudget.loader.PresetImageLoader;
import site.scalarstudios.scalarbudget.model.BudgetItem;
import site.scalarstudios.scalarbudget.model.Period;
import java.util.function.BiConsumer;

/**
 * UI component for adding a new budget item, with callbacks for save/cancel.
 *
 * @author Lemon_Juiced
 */
public class AddItemEditor extends HBox {
    private final TextField nameField;
    private final TextField amountField;
    private final ComboBox<Period> periodBox;
    private final CheckBox usePresetImageBox;
    private BudgetItem editingItem = null;

    public AddItemEditor(BiConsumer<BudgetItem, AddItemEditor> onSave, Runnable onCancel) {
        this(onSave, onCancel, null);
    }

    public AddItemEditor(BiConsumer<BudgetItem, AddItemEditor> onSave, Runnable onCancel, BudgetItem toEdit) {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(16);
        setStyle("-fx-background-color: #23272e; -fx-border-color: #333842; -fx-border-radius: 8; -fx-background-radius: 8;");
        setPadding(new Insets(10));
        setMaxWidth(Double.MAX_VALUE);

        nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setStyle("-fx-background-color: #23272e; -fx-text-fill: #e0e6ed; -fx-border-color: #444c56; -fx-font-size: 16; -fx-font-weight: bold;");
        nameField.setPrefWidth(120);
        amountField = new TextField();
        amountField.setPromptText("Amount");
        amountField.setStyle("-fx-background-color: #23272e; -fx-text-fill: #7fffd4; -fx-border-color: #444c56; -fx-font-size: 16; -fx-font-weight: bold;");
        amountField.setPrefWidth(100);
        periodBox = new ComboBox<>();
        periodBox.getItems().addAll(Period.getAll());
        periodBox.setValue(Period.MONTH);
        periodBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Period p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getDisplayName());
            }
        });
        periodBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Period p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getDisplayName());
            }
        });
        periodBox.setStyle("-fx-background-color: #23272e; -fx-text-fill: #e0e6ed; -fx-border-color: #444c56; -fx-font-size: 14; -fx-font-weight: bold;");
        Button saveNewButton = new Button("Save");
        saveNewButton.setStyle("-fx-background-color: #2d333b; -fx-text-fill: #7fffd4; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 6 18; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #444c56;");
        Button cancelNewButton = new Button("Cancel");
        cancelNewButton.setStyle("-fx-background-color: #23272e; -fx-text-fill: #e0e6ed; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 6 18; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #444c56;");

        getChildren().addAll(nameField, amountField, periodBox, saveNewButton, cancelNewButton);

        usePresetImageBox = new CheckBox();
        usePresetImageBox.setStyle("-fx-text-fill: #7fffd4; -fx-font-size: 13; -fx-font-weight: bold;");
        usePresetImageBox.setVisible(false);
        usePresetImageBox.setManaged(false);
        getChildren().add(3, usePresetImageBox); // Insert before save/cancel buttons

        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            String trimmed = newVal.trim();
            if (PresetImageLoader.hasPresetForName(trimmed)) {
                usePresetImageBox.setText("Use preset image for '" + trimmed + "'");
                usePresetImageBox.setVisible(true);
                usePresetImageBox.setManaged(true);
            } else {
                usePresetImageBox.setVisible(false);
                usePresetImageBox.setManaged(false);
                usePresetImageBox.setSelected(false);
            }
        });

        if (toEdit != null) {
            editingItem = toEdit;
            nameField.setText(toEdit.getName());
            amountField.setText(String.format("%.2f", toEdit.getAmount()));
            periodBox.setValue(toEdit.getPeriod());
            if (toEdit.isUsingImage() && toEdit.getImagePath() != null && !toEdit.getImagePath().isEmpty()) {
                usePresetImageBox.setSelected(true);
                usePresetImageBox.setVisible(true);
                usePresetImageBox.setManaged(true);
            }
        }

        saveNewButton.setOnAction(ev -> {
            String name = nameField.getText().trim();
            String amtStr = amountField.getText().trim();
            Period per = periodBox.getValue();
            if (name.isEmpty() || amtStr.isEmpty() || !amtStr.matches("\\d+(\\.\\d+)?")) {
                nameField.setStyle(nameField.getStyle() + ";-fx-border-color: #ff6b6b;");
                amountField.setStyle(amountField.getStyle() + ";-fx-border-color: #ff6b6b;");
                return;
            }
            double amt = Double.parseDouble(amtStr);
            if (editingItem != null) {
                editingItem.setName(name);
                editingItem.setAmount(amt);
                editingItem.setPeriod(per);
                if (usePresetImageBox.isVisible() && usePresetImageBox.isSelected()) {
                    String url = PresetImageLoader.getImageUrlForName(name);
                    editingItem.setUsingImage(true);
                    editingItem.setImagePath(url);
                } else {
                    editingItem.setUsingImage(false);
                    editingItem.setImagePath(null);
                }
                onSave.accept(editingItem, this);
            } else {
                BudgetItem newItem;
                if (usePresetImageBox.isVisible() && usePresetImageBox.isSelected()) {
                    String url = PresetImageLoader.getImageUrlForName(name);
                    newItem = new BudgetItem(name, amt, per, url);
                } else {
                    newItem = new BudgetItem(name, amt, per);
                }
                onSave.accept(newItem, this);
            }
        });
        cancelNewButton.setOnAction(ev -> onCancel.run());
    }
}
