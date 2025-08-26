package site.scalarstudios.scalarbudget.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The BudgetItem class represents a budget item with attributes such as name, amount,
 * period, and optional image details. It provides constructors for creating budget items
 * with or without images, along with getters and setters for each attribute.
 *
 * @author Lemon_Juiced
 */
public class BudgetItem {
    private String name;
    private double amount;
    @JsonSerialize(using = PeriodSerializer.class)
    @JsonDeserialize(using = PeriodDeserializer.class)
    private Period period;
    private boolean usingImage;
    private String imagePath;

    @JsonCreator
    public BudgetItem(
            @JsonProperty("name") String name,
            @JsonProperty("amount") double amount,
            @JsonProperty("period") Period period,
            @JsonProperty("usingImage") boolean usingImage,
            @JsonProperty("imagePath") String imagePath) {
        this.name = name;
        this.amount = amount;
        this.period = period != null ? period : Period.MONTH;
        this.usingImage = usingImage;
        this.imagePath = imagePath;
    }

    public BudgetItem(String name, double amount, Period period, String imagePath) {
        this.name = name;
        this.amount = amount;
        this.period = period;
        this.usingImage = true;
        this.imagePath = imagePath;
    }

    public BudgetItem(String name, double amount, Period period) {
        this.name = name;
        this.amount = amount;
        this.period = period;
        this.usingImage = false;
        this.imagePath = null;
    }

    // No-argument constructor for Jackson
    public BudgetItem() {
        this.period = Period.MONTH;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public boolean isUsingImage() {
        return usingImage;
    }

    public void setUsingImage(boolean usingImage) {
        this.usingImage = usingImage;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
