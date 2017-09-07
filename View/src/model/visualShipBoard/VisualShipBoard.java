package model.visualShipBoard;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import logic.data.Ship;
import logic.data.ShipBoard;
import logic.data.enums.AttackResult;
import logic.data.enums.Direction;
import view.gameWindow.StyleClasses;
import xmlInputManager.Position;

public class VisualShipBoard {
    private StringProperty[][] board;
    private ShipBoard logicShipBoard;

    public VisualShipBoard(int row, int column, ShipBoard logicShipBoard) {
        this.board = new StringProperty[row][column];
        this.logicShipBoard = logicShipBoard;
    }

    /**
     * Bind a Button Style Class to the StringProperty at Board[row][column]
     *
     * @param row            - Button Row
     * @param column         The Button Column
     * @param buttonToBind   - the button to bind
     * @param styleClass
     */
    public void bindButtonToVisualShipBoardSqaure(int row, int column, Button buttonToBind, StyleClasses styleClass) {
        board[row][column] = new SimpleStringProperty(styleClass.name());

        buttonToBind.getStyleClass().add(styleClass.name());
        board[row][column].addListener(observable -> {
            ObservableList<String> styleClassList = buttonToBind.getStyleClass();
            styleClassList.removeIf(e -> !(e.equalsIgnoreCase("button")));
            styleClassList.add(((StringProperty) observable).getValue());
        });
    }

    public void updateBoardSqaureValue(int row, int column, AttackResult attackResult) {
        String styleClassName = "";
        boolean needToChange = true;

        switch (attackResult) {
            case SHIPHIT:
                styleClassName = StyleClasses.shipHitShipButton.name();
                break;
            case SHIPDROWNHIT:
                markDrownShip(attackResult.getShip());
                needToChange = false;
                break;
            case REPEATEDHIT:
                needToChange = false;
                break;
            case MISSHIT:
                needToChange = false;
                break;
            case MINESHIP:
                styleClassName = StyleClasses.shipHitShipButton.name();
                break;
            case MINEDROWNSHIP:
                markDrownShip(attackResult.getShip());
                needToChange = false;
                break;
            case MINEWATER:
                needToChange = false;
                break;
            case MINEMINE:
                styleClassName = StyleClasses.chosenTrackButton.name();
                break;
            case MINEREAPETEDHIT:
                needToChange = false;
                break;
        }

        if (needToChange) {
            this.board[row][column].set(styleClassName);
        }
    }

    public void markMineAsExploded(int row, int column) {
        this.board[row][column].setValue(StyleClasses.mineExplosionTrackButton.name());
    }

    //Mark VisualShipType Drown at Absoreber VisualShipType board

    private void markDrownShip(Ship drownShip) {
        Direction shipDirection = drownShip.getDirection();
        Position startingPosition = drownShip.getStartPoint();
        int row = startingPosition.getX(), column = startingPosition.getY(), shipLength = drownShip.getLength();

        switch (shipDirection) {
            case ROW:
                setShipButtonOnShipDrownInRow(row, column, shipLength);
                break;
            case COLUMN:
                setShipButtonOnShipDrownInColumn(row, column, shipLength);
                break;
            case DOWN_RIGHT:
                setShipButtonOnShipDrownInColumn(row - shipLength + 1, column, shipLength);
                setShipButtonOnShipDrownInRow(row, column, shipLength);
                break;
            case UP_RIGHT:
                setShipButtonOnShipDrownInColumn(row, column, shipLength);
                setShipButtonOnShipDrownInRow(row, column, shipLength);
                break;
            case RIGHT_UP:
                setShipButtonOnShipDrownInRow(row, column - shipLength + 1, shipLength);
                setShipButtonOnShipDrownInColumn(row - shipLength + 1, column, shipLength);
                break;
            case RIGHT_DOWN:
                setShipButtonOnShipDrownInRow(row, column - shipLength + 1, shipLength);
                setShipButtonOnShipDrownInColumn(row, column, shipLength);
                break;
        }
    }

    private void setShipButtonOnShipDrownInRow(int row, int firstColumn, int shipLength) {
        for (int column = firstColumn; column < firstColumn + shipLength; column++) {
            board[row][column].set(StyleClasses.shipDrownShipButton.name());
        }
    }

    private void setShipButtonOnShipDrownInColumn(int firstRow, int column, int shipLength) {
        for (int row = firstRow; row < firstRow + shipLength; row++) {
            board[row][column].set(StyleClasses.shipDrownShipButton.name());
        }
    }

    public void putMine(int row, int column) {
        this.board[row][column].set(StyleClasses.mineButton.name());
    }
}
