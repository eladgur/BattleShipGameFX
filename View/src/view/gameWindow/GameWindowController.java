package view.gameWindow;

import controller.GameController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.data.PlayerData;
import logic.data.Ship;
import logic.data.ShipBoard;
import logic.data.TrackBoard;
import logic.data.enums.AttackResult;
import model.PlayerStats;
import model.VisualShipType;
import model.visualShipBoard.VisualShipBoard;
import model.visualTrackBoard.VisualTrackBoard;
import utills.Utills;
import xmlInputManager.GameInfo;
import xmlInputManager.Position;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static utills.GridPaneUtills.setShipBoardGridPane;
import static utills.GridPaneUtills.setTrackBoardGridPane;

public class GameWindowController implements OnMinePutObserverable, OnPlayerRetreatObservable {
    private PlayerData playerData;
    private GameInfo gameInfo;
    private int boardSize;
    private PlayerStats playerStats;
    private Map<String, VisualShipType> shipStatsMap;
    private List<OnMinePutObserver> onMinePutEventListeners;
    private List<OnPlayerRetreatObserver> onPlayerRetreatListeners;
    //Mine Tile Pane
    @FXML TilePane minesTilePane;
    //FX GridPanes
    @FXML GridPane gameStatsGridPane, trackBoardGridPane, shipBoardGridPane;
    @FXML TabPane boardTabPane;
    //Player VisualShipType states Table view
    @FXML TableView<Map.Entry<String, VisualShipType>> playerShipStateTableView;
    @FXML TableColumn<Map.Entry<String, VisualShipType>, String> playerShipTypeColumn;
    @FXML TableColumn<Map.Entry<String, VisualShipType>, Integer> playerShipAmountColumn;
    //Enemy VisualShipType states Table view
    @FXML TableView<Map.Entry<String, VisualShipType>> enemyShipStateTableView;
    @FXML TableColumn<Map.Entry<String, VisualShipType>, String> enemyShipTypeColumn;
    @FXML TableColumn<Map.Entry<String, VisualShipType>, Integer> enemyShipAmountColumn;
    //FX gameStatsGridPane Controls
    @FXML Label playerNameLabel, scoreLabel, hitsLabel, missesLabel, avgAttackTimeLabel, turnLabel;
    //Model Visual Boards
    private VisualShipBoard visualShipBoard;
    private VisualTrackBoard visualTrackBoard;

    public GameWindowController() {
    }

    public void setData(PlayerData playerData, int boardSize, Stage primaryStage, GameInfo gameInfo) {
        this.playerData = playerData;
        this.boardSize = boardSize;
        this.shipStatsMap = new HashMap<>();
        this.playerStats = new PlayerStats(playerData, playerNameLabel, scoreLabel, hitsLabel, missesLabel, avgAttackTimeLabel, turnLabel);
        this.gameInfo = gameInfo;
        //Set Visual Boards
        initVisualBoards(this.boardSize);
    }

    public void setComponents(GameController gameController) {
        addOnMinePutListener(gameController);
        addOnPlayerRetreatListener(gameController);
        setBoards(gameController, this.visualShipBoard, this.visualTrackBoard);
        //Set Mine's TilePane
        setMines(gameInfo.getMineAmount());
        setPlayerShipStateTableView();
    }

    private void setMines(int mineAmount) {
        for (int i = 0; i < mineAmount; i++) {
            Button mineButton = createMineButton(i + 1);
            minesTilePane.getChildren().add(mineButton);
        }
    }

    private Button createMineButton(int buttonNumber) {
        Button mineButton = new Button();
        mineButton.setId("mineButton" + buttonNumber);
        mineButton.getStyleClass().add("mineButton");
        //Drag & Drop Event Handlers
        mineButton.setOnDragDetected(GameWindowController::onMineButtonDragDetectedEventHandler);

        return mineButton;
    }

    //Board's
    public void setBoards(GameController gameController, VisualShipBoard visualShipBoard, VisualTrackBoard visualTrackBoard) {
        setShipBoardGridPane(shipBoardGridPane, boardSize, playerData.getShipBoard(), this, visualShipBoard);
        setTrackBoardGridPane(trackBoardGridPane, boardSize, playerData.getTrackBoard(), gameController, visualTrackBoard);
    }

    public GridPane getTrackBoardGridPane() {
        return trackBoardGridPane;
    }

    public GridPane getShipBoardGridPane() {
        return shipBoardGridPane;
    }

    public void updatePlayerStats(PlayerData playerData) {
        this.playerStats.update(playerData);
    }

    public Button getTrackBoardGridPaneButton(int row, int column) {
        Button result = null;
        ObservableList<Node> childrens = trackBoardGridPane.getChildren();

        for (Node node : childrens) {
            if (node.getClass().equals(Button.class) && trackBoardGridPane.getRowIndex(node) == row && trackBoardGridPane.getColumnIndex(node) == column) {
                result = (Button) node;
                break;
            }
        }

        return result;
    }

    public Button getShipBoardGridPaneButton(int row, int column) {
        Button result = null;
        ObservableList<Node> childrens = shipBoardGridPane.getChildren();

        for (Node node : childrens) {
            if (node.getClass().equals(Button.class) && shipBoardGridPane.getRowIndex(node) == row && shipBoardGridPane.getColumnIndex(node) == column) {
                result = (Button) node;
                break;
            }
        }

        return result;
    }

    //Ship State Table's

    private void createShipStateTable(GameInfo gameInfo) {
        gameInfo.getShipTypes().getShipTypesList().forEach(shipType -> shipStatsMap.put(shipType.getId(), new VisualShipType(shipType)));
    }

    public void updatePlayerVisualShipTypeMapOnShipDrown(Ship drownShip) {
        String shipTypeName = drownShip.getType();
        VisualShipType visualShipType = shipStatsMap.get(shipTypeName);

        visualShipType.decreaseAmount();
        //TRY
        if (visualShipType.getShipAmountProperty().get() == 0) {
            shipStatsMap.remove(shipTypeName);
        }
        playerShipStateTableView.getItems().removeIf(e -> e.getValue().getShipAmountProperty().get() == 0);
    }

    public void updateEnemyVisualShipTypeMapOnEnemyShipDrown(Ship drownShip) {
        enemyShipStateTableView.getItems().removeIf(e -> e.getValue().getShipAmountProperty().get() == 0);
    }

    public TableView<Map.Entry<String, VisualShipType>> getPlayerShipStateTableView() {
        return playerShipStateTableView;
    }

    private void setPlayerShipStateTableView() {
        //Set players VisualShipType Map's
        createShipStateTable(gameInfo);
        playerShipTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().getShipTypeProperty());
        playerShipAmountColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().getShipAmountProperty().asObject());
        playerShipStateTableView.setItems(
                FXCollections.synchronizedObservableList(
                        FXCollections.observableArrayList(
                                shipStatsMap.entrySet())));
    }

    public void setEnemyShipStateTableView(Map<String, VisualShipType> enemyShipStateTableView) {
        enemyShipTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().getShipTypeProperty());
        enemyShipAmountColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().getShipAmountProperty().asObject());
        this.enemyShipStateTableView.setItems(FXCollections.observableArrayList(enemyShipStateTableView.entrySet()));
    }

    public Map<String, VisualShipType> getShipStatsMap() {
        return shipStatsMap;
    }

    //Mine Drag & Drop EventHandler's

    public static void onMineButtonDragDetectedEventHandler(MouseEvent mouseEvent) {
        Button draggedButton = (Button) mouseEvent.getSource();
        //Hint the user that on gesture start
        /* drag was detected, start a drag-and-drop gesture*/
        Dragboard db = draggedButton.startDragAndDrop(TransferMode.MOVE);
        /* Put a string on a dragboard */
        ClipboardContent content = new ClipboardContent();
        //Put Style Class Name on content
        content.putString("mineButton");
        db.setContent(content);
        //Set visual drag appearance
        WritableImage snapshot = draggedButton.snapshot(new SnapshotParameters(), null);
        db.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2); //Put the cursor at the middle of the visual drag box

        mouseEvent.consume();
    }

    public static void onMineButtonDragOverEventHandler(DragEvent event) {
        Button targetButton = (Button) event.getTarget();
        if (event.getGestureSource() != event.getTarget() && event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    public static void onMineButtonDragEnteredEventHandler(DragEvent event) {
        Button targetButton = (Button) event.getTarget();

        if (event.getGestureSource() != targetButton && event.getDragboard().hasString()) {
            targetButton.getStyleClass().add("shipButtonDragEntered");
        }
        event.consume();
    }

    public static void onMineButtonDragExited(DragEvent event) {
        Button targetButton = (Button) event.getTarget();

        targetButton.getStyleClass().remove("shipButtonDragEntered");
        event.consume();
    }

    public void onMineButtonDragDropped(DragEvent event) {
        /* data dropped */
        /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString() && db.getString().equalsIgnoreCase(StyleClasses.mineButton.name())) {
            Button targetButton = (Button) event.getGestureTarget();
            int row = GridPane.getRowIndex(targetButton) - 1;
            int column = GridPane.getColumnIndex(targetButton) - 1;
            insertMine(row, column);
            targetButton.getStyleClass().add(db.getString());
            targetButton.setOnDragOver(null);
            targetButton.setOnDragEntered(null);
            notifyAllOnMinePutListenersOnMinePut(new Position(row, column));
            minesTilePane.getChildren().remove(event.getGestureSource());
            //Set as Success
            success = true;
        }
        /* let the source know whether the string was successfully transferred and used */
        event.setDropCompleted(success);
        event.consume();
    }

    public void insertMine(int row, int column) {
        this.visualShipBoard.putMine(row, column);
    }

    //OnMinePut Event
    @Override
    public void addOnMinePutListener(OnMinePutObserver listener) {
        if (onMinePutEventListeners == null) {
            onMinePutEventListeners = new LinkedList<>();
        }
        onMinePutEventListeners.add(listener);
    }

    @Override
    public void removeOnMinePutListener(OnMinePutObserver listener) {
        if (onMinePutEventListeners != null) {
            onMinePutEventListeners.remove(listener);
        }
    }

    @Override
    public void notifyAllOnMinePutListenersOnMinePut(Position minePosition) {
        if (onMinePutEventListeners != null) {
            onMinePutEventListeners.forEach(listener -> listener.onMinePutEventHandler(minePosition));
        }
    }

    public void onRetreatButtonClickedEventHandler(ActionEvent actionEvent) {
        notifyAllOnPlayerRetreatListeners(playerNameLabel.getText());
    }

    //On player Retreat Event
    @Override
    public void addOnPlayerRetreatListener(OnPlayerRetreatObserver listener) {
        if (onPlayerRetreatListeners == null) {
            onPlayerRetreatListeners = new LinkedList<>();
        }
        onPlayerRetreatListeners.add(listener);
    }

    @Override
    public void removeOnPlayerRetreatListener(OnPlayerRetreatObserver listener) {
        if (onPlayerRetreatListeners != null) {
            onPlayerRetreatListeners.remove(listener);
        }
    }

    @Override
    public void notifyAllOnPlayerRetreatListeners(String playerName) {
        if (onPlayerRetreatListeners != null) {
            onPlayerRetreatListeners.forEach(listener -> listener.OnPlayerRetreatEventHandler(playerName));
        }
    }

    public GridPane getGameStatsGridPane() {
        return this.gameStatsGridPane;
    }

    public TabPane getBoardTabPane() {
        return this.boardTabPane;
    }

    public void initVisualBoards(int boardSize) {
        //Get Logic Boards From Logic
        ShipBoard shipBoard = playerData.getShipBoard();
        TrackBoard trackBoard = playerData.getTrackBoard();
        //Set Visual Boards
        this.visualShipBoard = new VisualShipBoard(boardSize, boardSize, shipBoard);
        this.visualTrackBoard = new VisualTrackBoard(boardSize, boardSize, trackBoard);
    }

    //Visual Boards update on attack
    public void updateVisualBoardsOnAttack(AttackResult attackResult, int row, int column) {
        this.visualTrackBoard.updateBoardSqaureValue(row, column, attackResult);
        if (attackResult.name().contains("MINE")) {
            this.visualShipBoard.updateBoardSqaureValue(row, column, attackResult);
        }
    }

    public void updateVisualBoardsOnBeenAttacked(AttackResult attackResult, int row, int column) {
        this.visualShipBoard.updateBoardSqaureValue(row, column, attackResult);
        if (attackResult.name().contains("MINE")) {
            getTrackBoardGridPaneButton(row + 1, column + 1).setDisable(true);
            this.visualTrackBoard.updateBoardSqaureValueAtMineSide(row, column, attackResult);
            this.visualShipBoard.markMineAsExploded(row, column);
        }
    }

    //Visual Boards update on Replay
    public void updateVisualComponantsOnReplay(PlayerData playerData) {
        TrackBoard trackBoard = playerData.getTrackBoard();
        ShipBoard shipBoard = playerData.getShipBoard();
        //Mark The player who did the move
//        markPlayerOnReplay(playerData.getPlayerName());
        //Update Boards
        visualTrackBoard.updateOnReplay(trackBoard);
        visualShipBoard.updateOnReplay(shipBoard);
        //Update Stats
        this.updatePlayerStats(playerData);
    }

    public void markPlayerOnReplay() {
        playerNameLabel.getStyleClass().remove("unMarkedPlayerNameLabel");
        playerNameLabel.getStyleClass().add("markedPlayerNameLabel");
    }

    public void unMarkPlayerOnReplay() {
        playerNameLabel.getStyleClass().remove("markedPlayerNameLabel");
        playerNameLabel.getStyleClass().add("unMarkedPlayerNameLabel");
    }

    public void onGameInstuctionsMenuItemClicked(ActionEvent actionEvent) {
        try {
            URL fxmlLocation = this.getClass().getResource("/view/gameWindow/GameInstructions.fxml");
            Scene scene = Utills.generateSceneFromFxml(fxmlLocation); //TODO: check
            Stage instructionsWIndow = new Stage();
            instructionsWIndow.setScene(scene);
            instructionsWIndow.initModality(Modality.APPLICATION_MODAL);
            instructionsWIndow.showAndWait();
        } catch (IOException e) {e.printStackTrace();}
    }
}