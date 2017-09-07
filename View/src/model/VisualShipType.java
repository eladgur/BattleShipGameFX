package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class VisualShipType {
    private final StringProperty type;
    private final IntegerProperty amount;

    public VisualShipType(xmlInputManager.ShipType shipType) {
        this.type = new SimpleStringProperty(shipType.getId());
        this.amount = new SimpleIntegerProperty(shipType.getAmount());
    }

    public void decreaseAmount() {
        amount.set(amount.get()-1); //TODO: check this
    }

    public StringProperty getShipTypeProperty() {
        return type;
    }

    public IntegerProperty getShipAmountProperty() {
        return amount;
    }
}
