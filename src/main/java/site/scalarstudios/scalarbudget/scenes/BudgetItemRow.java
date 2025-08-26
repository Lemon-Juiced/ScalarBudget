package site.scalarstudios.scalarbudget.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import site.scalarstudios.scalarbudget.model.BudgetItem;
import site.scalarstudios.scalarbudget.model.Period;

import java.util.function.Consumer;

/**
 * UI component representing a single editable budget item row.
 *
 * @author Lemon_Juiced
 */
public class BudgetItemRow extends HBox {
    private final BudgetItem item;
    private final TextField amountField;
    private final ComboBox<Period> periodBox;
    private final Button deleteButton;

    public BudgetItemRow(BudgetItem item, Consumer<BudgetItemRow> onDelete) {
        this.item = item;
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(24);
        setStyle("-fx-background-color: #23272e; -fx-border-color: #333842; -fx-border-radius: 8; -fx-background-radius: 8;");
        setPadding(new Insets(10));
        setMaxWidth(Double.MAX_VALUE);

        // --- Begin merged BudgetGraphicComponent logic ---
        VBox leftBox = new VBox(5);
        leftBox.setAlignment(Pos.CENTER_LEFT); // Align name and image to center-left
        leftBox.setFillWidth(true);
        if (item.isUsingImage() && item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            try {
                System.out.println("Attempting to load image: " + item.getImagePath());
                javafx.scene.image.Image image;
                String path = item.getImagePath();
                // Force synchronous loading to catch errors immediately
                if (path.startsWith("http://") || path.startsWith("https://")) {
                    image = new javafx.scene.image.Image(path, 48, 48, true, true, false); // backgroundLoading=false
                } else {
                    java.io.File imgFile = new java.io.File(path);
                    image = new javafx.scene.image.Image(imgFile.toURI().toString(), 48, 48, true, true, false);
                }
                if (image.isError()) {
                    System.out.println("Image failed to load: " + path + " | Exception: " + image.getException());
                } else {
                    System.out.println("Image loaded successfully: " + path);
                    System.out.println("Image width: " + image.getWidth() + ", height: " + image.getHeight());
                }
                javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(image);
                imageView.setFitWidth(48);
                imageView.setFitHeight(48);
                imageView.setPreserveRatio(true);
                imageView.setStyle("-fx-border-color: red; -fx-border-width: 2px;"); // Debug border
                // TEMP: Comment out the clip to see if it is hiding the image
                // javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(48, 48);
                // clip.setArcWidth(12);
                // clip.setArcHeight(12);
                // imageView.setClip(clip);
                leftBox.getChildren().add(imageView);
            } catch (Exception e) {
                System.out.println("Exception loading image: " + e.getMessage());
                e.printStackTrace();
                // If image fails to load, just show name
            }
        }
        javafx.scene.control.Label nameLabel = new javafx.scene.control.Label(item.getName());
        nameLabel.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 16));
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e0e6ed; -fx-background-color: transparent; -fx-padding: 0 0 0 8px;");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.setAlignment(Pos.CENTER_LEFT);
        leftBox.getChildren().add(nameLabel);
        // --- End merged BudgetGraphicComponent logic ---

        amountField = new TextField(String.format("%.2f", item.getAmount()));
        amountField.setPrefWidth(100);
        amountField.setStyle("-fx-background-color: #23272e; -fx-text-fill: #7fffd4; -fx-border-color: #444c56; -fx-font-size: 16; -fx-font-weight: bold;");
        amountField.setFocusTraversable(false);

        periodBox = new ComboBox<>();
        periodBox.getItems().addAll(Period.getAll());
        periodBox.setValue(item.getPeriod());
        periodBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Period p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getDisplayName());
                setStyle("-fx-text-fill: #e0e6ed; -fx-background-color: #23272e;");
            }
        });
        periodBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Period p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getDisplayName());
                setStyle("-fx-text-fill: #e0e6ed; -fx-background-color: #23272e;");
            }
        });
        periodBox.setStyle("-fx-background-color: #23272e; -fx-text-fill: #e0e6ed; -fx-border-color: #444c56; -fx-font-size: 14; -fx-font-weight: bold;");
        periodBox.setFocusTraversable(false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox editBox = new VBox(8, amountField, periodBox);
        editBox.setAlignment(Pos.CENTER_RIGHT);

        deleteButton = new Button("\uD83D\uDDD1"); // Trash can emoji
        deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff6b6b; -fx-font-size: 20; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 8 0 8;");
        deleteButton.setFocusTraversable(false);
        deleteButton.setOnAction(e -> {
            if (onDelete != null) onDelete.accept(this);
        });

        getChildren().addAll(leftBox, spacer, editBox, deleteButton);
    }

    // For backward compatibility
    public BudgetItemRow(BudgetItem item) {
        this(item, null);
    }

    public TextField getAmountField() {
        return amountField;
    }

    public ComboBox<Period> getPeriodBox() {
        return periodBox;
    }

    public BudgetItem getBudgetItem() {
        return item;
    }

    public void updateModel() {
        String amtStr = amountField.getText();
        if (amtStr != null && amtStr.matches("\\d+(\\.\\d+)?")) {
            item.setAmount(Double.parseDouble(amtStr));
        }
        item.setPeriod(periodBox.getValue());
    }
}
