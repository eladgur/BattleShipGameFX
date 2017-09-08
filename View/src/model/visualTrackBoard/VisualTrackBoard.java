package model.visualTrackBoard;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.CornerRadii;
import logic.data.Ship;
import logic.data.TrackBoard;
import logic.data.enums.AttackResult;
import logic.data.enums.Direction;
import logic.data.enums.TrackBoardSquareValue;
import view.gameWindow.StyleClasses;
import xmlInputManager.Position;

import javax.naming.Binding;

public class VisualTrackBoard implements Cloneable{
    private StringProperty[][] board;
    private TrackBoard logicTrackBoard;

    public VisualTrackBoard(int row, int column, TrackBoard logicTrackBoard) {
        this.board = new StringProperty[row][column];
        this.logicTrackBoard = logicTrackBoard;

    }

    /**
     * Bind a Button Style Class to the StringProperty at Board[row][column]
     *
     * @param row          - Button Row
     * @param column       The Button Column
     * @param buttonToBind - the button to bind
     */
    public void bindButtonToVisualTrackBoardSqaure(int row, int column, Button buttonToBind) {
        board[row][column] = new SimpleStringProperty(StyleClasses.trackBoardGridPaneButton.name());

        buttonToBind.getStyleClass().add(StyleClasses.trackBoardGridPaneButton.name());
        board[row][column].addListener(observable -> {
            ObservableList<String> styleClassList = buttonToBind.getStyleClass();
            styleClassList.removeIf(e -> !(e.equalsIgnoreCase("button")));
            //TODO: To check if need to clear() the StyleClass list
            //Apple new Style Class to button
            styleClassList.add(((StringProperty) observable).getValue());
        });
    }

    public void updateBoardSqaureValue(int row, int column, AttackResult attackResult) {
        boolean needToChange = true;
        String styleClassName = "";

        switch (attackResult) {
            case SHIPHIT:
                styleClassName = StyleClasses.shipHitTrackButton.name();
                break;
            case SHIPDROWNHIT:
                markDrownShipInAttackerTrackBoard(attackResult.getShip());
                needToChange = false;
                break;
            case REPEATEDHIT:
                needToChange = false;
                break;
            case MISSHIT:
                styleClassName = StyleClasses.chosenTrackButton.name();
                break;
            case MINESHIP:
                styleClassName = StyleClasses.mineExplosionTrackButton.name();
                break;
            case MINEDROWNSHIP:
                styleClassName = StyleClasses.mineExplosionTrackButton.name();
                break;
            case MINEWATER:
                styleClassName = StyleClasses.mineExplosionTrackButton.name();
                break;
            case MINEMINE:
                styleClassName = StyleClasses.mineExplosionTrackButton.name();
                break;
            case MINEREAPETEDHIT:
                styleClassName = StyleClasses.mineExplosionTrackButton.name();
                break;
        }

        if (needToChange) {
            this.board[row][column].set(styleClassName);
        }
    }

    public void updateBoardSqaureValueAtMineSide(int row, int column, AttackResult attackResult) {
        boolean needToChange = true;
        String styleClassName = "";

        switch (attackResult) {
            case MINESHIP:
                styleClassName = StyleClasses.shipHitTrackButton.name();
                break;
            case MINEDROWNSHIP:
                markDrownShipInAttackerTrackBoard(attackResult.getShip());
                needToChange = false;
                break;
            case MINEWATER:
                styleClassName = StyleClasses.chosenTrackButton.name();
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

    //Mark VisualShipType Drown at Attacker track board

    public void markDrownShipInAttackerTrackBoard(Ship drownShip) {
        Direction shipDirection = drownShip.getDirection();
        Position startingPosition = drownShip.getStartPoint();
        int row = startingPosition.getX(), column = startingPosition.getY();
        int shipLength = drownShip.getLength();

        switch (shipDirection) {
            case ROW:
                setTrackButtonOnShipDrownInRow(drownShip, row, column, shipLength);
                break;
            case COLUMN:
                setTrackButtonOnShipDrownInCul(drownShip, row, column, shipLength);
                break;
            case DOWN_RIGHT:
                setTrackButtonOnShipDrownInCul(drownShip, row - shipLength + 1, column, shipLength);
                setTrackButtonOnShipDrownInRow(drownShip, row, column, shipLength);
                break;
            case UP_RIGHT:
                setTrackButtonOnShipDrownInCul(drownShip, row, column, shipLength);
                setTrackButtonOnShipDrownInRow(drownShip, row, column, shipLength);
                break;
            case RIGHT_UP:
                setTrackButtonOnShipDrownInRow(drownShip, row, column - shipLength + 1, shipLength);
                setTrackButtonOnShipDrownInCul(drownShip, row - shipLength + 1, column, shipLength);
                break;
            case RIGHT_DOWN:
                setTrackButtonOnShipDrownInRow(drownShip, row, column - shipLength + 1, shipLength);
                setTrackButtonOnShipDrownInCul(drownShip, row, column, shipLength);
                break;
        }
    }

    private void setTrackButtonOnShipDrownInRow(Ship ship, int row, int firstCul, int shipLength) {
        for (int cul = firstCul; cul < firstCul + shipLength; cul++) {
            board[row][cul].set(StyleClasses.shipDrownTrackButton.name());
        }
    }

    private void setTrackButtonOnShipDrownInCul(Ship ship, int firstRow, int cul, int shipLength) {
        for (int row = firstRow; row < firstRow + shipLength; row++) {
            this.board[row][cul].set(StyleClasses.shipDrownTrackButton.name());
        }
    }

    public void updateOnReplay(TrackBoard trackBoard) {
        String styleClassName = "";
        int boardSize = this.board.length;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Position position = new Position(i,j);
                switch (trackBoard.getSquareValue(position)) {
                    case HIT:
                        styleClassName = StyleClasses.shipHitTrackButton.name();
                        break;
                    case MISS:
                        styleClassName = StyleClasses.chosenTrackButton.name();
                        break;
                    case EMPTY:
                        styleClassName = StyleClasses.trackBoardGridPaneButton.name();
                        break;
                }
                this.board[i][j].set(styleClassName);
            }
        }
    }
}
