package utills;

import controller.GameController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import logic.data.ShipBoard;
import logic.data.TrackBoard;
import logic.data.enums.ShipBoardSquareValue;
import logic.data.enums.TrackBoardSquareValue;
import model.visualShipBoard.VisualShipBoard;
import model.visualTrackBoard.VisualTrackBoard;
import view.gameWindow.GameWindowController;
import view.gameWindow.StyleClasses;
import xmlInputManager.Position;

public class GridPaneUtills {

    public static void setShipBoardGridPane(GridPane shipBoardGridPane, int boardSize, ShipBoard shipBoard, GameWindowController gameWindowController, VisualShipBoard visualShipBoard) {
        int gridPaneSize = boardSize + 1; //+1 for the numbers at the headers

        setGridConstraints(shipBoardGridPane, gridPaneSize);
        addHeadersToGridPane(shipBoardGridPane, gridPaneSize);
        addButtonToShipBoardGridPane(shipBoardGridPane, boardSize, shipBoard, gameWindowController, visualShipBoard);
    }

    private static void setGridConstraints(GridPane gridPane1, int gridSize) {
        for (int i = 0; i < gridSize; i++) {
            //Cul
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHalignment(HPos.CENTER);
            columnConstraints.setPrefWidth(GridPane.USE_COMPUTED_SIZE);
            columnConstraints.setHgrow(Priority.SOMETIMES);
            gridPane1.getColumnConstraints().add(i, columnConstraints);
            //Row
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setValignment(VPos.CENTER);
            rowConstraints.setPrefHeight(GridPane.USE_COMPUTED_SIZE);
            rowConstraints.setVgrow(Priority.SOMETIMES);
            gridPane1.getRowConstraints().add(i, rowConstraints);
        }
    }

    public static void setTrackBoardGridPane(GridPane trackBoardGridPane, int boardSize, TrackBoard trackBoard, GameController gameController, VisualTrackBoard visualTrackBoard) {
        int gridPaneSize = boardSize + 1; //+1 for the numbers at the headers

        //TODO: to add the trackBoard
        setGridConstraints(trackBoardGridPane, gridPaneSize);
        addHeadersToGridPane(trackBoardGridPane, gridPaneSize);
        addButtonToTrackBoardGridPane(trackBoardGridPane, event -> gameController.trackButtonClickEventHandler(event), boardSize, trackBoard, visualTrackBoard);
    }

    private static void addHeadersToGridPane(GridPane gridPane, int gridSize) {

        for (int i = 1; i < gridSize; i++) {
            //Create Labels
            Label rowLabel = new Label(String.valueOf(i));
            Label culLabel = new Label(String.valueOf(i));
            //Size
            rowLabel.setMinWidth(Label.USE_PREF_SIZE);
            //Set style Class
            rowLabel.getStyleClass().add("headerNumbersLabels");
            culLabel.getStyleClass().add("headerNumbersLabels");
            //Set constraints
            GridPane.setConstraints(culLabel, i, 0, 1, 1, HPos.CENTER, VPos.CENTER);        //First row
            GridPane.setConstraints(rowLabel, 0, i, 1, 1, HPos.CENTER, VPos.CENTER);     //First Cul
            //Add to Grid
            gridPane.getChildren().add(rowLabel);
            gridPane.getChildren().add(culLabel);
        }
    }

    private static void addButtonToShipBoardGridPane(GridPane gridPane, int gridSize, ShipBoard shipBoard, GameWindowController gameWindowController, VisualShipBoard visualShipBoard) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                ShipBoardSquareValue shipBoardSquareValue = shipBoard.getShipBoardSquareValue(new Position(i, j));
                Button button = new Button();
                //Add ToolTip for the button
                button.setTooltip(new Tooltip(String.format("%d,%d", i + 1, j + 1)));
                //Set button size and bind them to to grow with the grid
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                //Bind button style
                StyleClasses styleClassName = getStyleClassByShipBoardSquareValue(shipBoardSquareValue);
                visualShipBoard.bindButtonToVisualShipBoardSqaure(i, j, button, styleClassName);
                setMineDragNDripInShipBoard(shipBoard, gameWindowController, i, j, button);
                //add button to the GridPane
                GridPane.setConstraints(button, j + 1, i + 1, 1, 1, HPos.CENTER, VPos.CENTER); // +1 because of the header
                gridPane.getChildren().add(button);
            }
        }
    }

    private static void setMineDragNDripInShipBoard(ShipBoard shipBoard, GameWindowController gameWindowController, int i, int j, Button button) {
        if (shipBoard.isValidMinePosition(i, j)) {
            //Add Drop Event Handler for the button <-> No ship at this Button
            button.setOnDragOver(GameWindowController::onMineButtonDragOverEventHandler);
            button.setOnDragEntered(GameWindowController::onMineButtonDragEnteredEventHandler);
            button.setOnDragExited(GameWindowController::onMineButtonDragExited);
            button.setOnDragDropped(gameWindowController::onMineButtonDragDropped);
        }
    }

    private static void addButtonToTrackBoardGridPane(GridPane trackBoardGridPane, EventHandler<ActionEvent> eventHandler, int boardSize, TrackBoard trackBoard, VisualTrackBoard visualTrackBoard) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                TrackBoardSquareValue trackBoardSquareValue = trackBoard.getSquareValue(new Position(i, j));
                Button button = new Button();
                //Add ToolTip for the button
                button.setTooltip(new Tooltip(String.format("%d,%d", i + 1, j + 1)));
                //Set button size and bind them to to grow with the grid
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                //Bind button style
                visualTrackBoard.bindButtonToVisualTrackBoardSqaure(i, j, button);
                //set button OnClick EventHandler
                button.setOnAction(eventHandler);
                //add button to the GridPane
                GridPane.setConstraints(button, j + 1, i + 1, 1, 1, HPos.CENTER, VPos.CENTER); //+1 because of the Grid header numbers
                trackBoardGridPane.getChildren().add(button);
            }
        }
    }

    private static StyleClasses getStyleClassByShipBoardSquareValue(ShipBoardSquareValue shipBoardSquareValue) {
        StyleClasses styleClass;

        switch (shipBoardSquareValue) {
            case WATER:
                styleClass = StyleClasses.shipBoardButton;
                break;
            case SHIP:
                styleClass = StyleClasses.shipBoardShipButton;
                break;
            case MINE:
                styleClass = StyleClasses.shipBoardMineButton;
                break;
            default:
                styleClass = StyleClasses.shipBoardMineButton;
                break;
        }

        return styleClass;
    }

}
