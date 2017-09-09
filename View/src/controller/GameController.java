package controller;

import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GameEngine;
import logic.GameStatus;
import logic.ShipDrownListener;
import logic.data.Ship;
import logic.data.enums.AttackResult;
import logic.exceptions.NoShipAtPoisitionException;
import utills.Utills;
import view.gameEndWindow.GameEndWindowController;
import view.gameWindow.GameWindowController;
import view.gameWindow.OnMinePutObserver;
import view.gameWindow.OnPlayerRetreatObserver;
import view.mainMenu.MainMenuController;
import view.mainMenu.onRestartGameListener;
import xmlInputManager.GameInfo;
import xmlInputManager.Position;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static utills.Utills.*;

public class GameController implements ShipDrownListener, OnMinePutObserver, OnPlayerRetreatObserver, onRestartGameEventListenable {
    private final GameInfo gameInfo;
    private GameEngine gameEngine;
    private int boardSize, activeSceneIndex = 0, pasiveSceneIndex = 1;
    private Scene mainMenuScene; //Needed For the End of game
    private Scene[] gameScenes;
    private GameWindowController[] gameWindowControllers;
    public static final int NUM_OF_PLAYERS = 2;
    private Stage primaryStage;
    private long turnTimerInMiliseconds; //Save the time in milliseconds when the Active user player windows has been apeared for the avg attack time calculating
    private List<onRestartGameListener> onRestartGameListeners;

    public GameController(GameEngine gameEngine, Stage primaryStage, GameInfo gameInfo) {
        //Set Game Info and set
        this.gameInfo = gameInfo;
        //Allocate Arrays
        this.gameScenes = new Scene[2];
        this.gameWindowControllers = new GameWindowController[2];
        //Set scenes
        this.primaryStage = primaryStage;
        this.mainMenuScene = primaryStage.getScene(); //Current Scene is the Main Menu Scene
        //Set game Engine and register for VisualShipType drown event
        this.gameEngine = gameEngine;
        this.gameEngine.addShipDrownListener(this);
        this.boardSize = gameEngine.getPlayerData().getBoardSize();
        //Set scenes
        loadGameScenes();
        //Set Game Stats
        updatePlayersStats();
    }

    private void updatePlayersStats() {
        gameWindowControllers[activeSceneIndex].updatePlayerStats(gameEngine.getPlayerData(activeSceneIndex));
        gameWindowControllers[pasiveSceneIndex].updatePlayerStats(gameEngine.getPlayerData(pasiveSceneIndex));
    }

    private void loadGameScenes() {
        try {
            //Generate Scenes from FXML's
            gameScenes[activeSceneIndex] = generateSceneFromFxmlAndSetController(getClass().getResource("/view/gameWindow/GameWindow.fxml"), activeSceneIndex);
            gameScenes[pasiveSceneIndex] = generateSceneFromFxmlAndSetController(getClass().getResource("/view/gameWindow/GameWindow2.fxml"), pasiveSceneIndex);
            //Dynamiclly set visual components due to game data
            initializeVisualComponent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeVisualComponent() {
        for (int i = 0; i < NUM_OF_PLAYERS; i++) {
            gameWindowControllers[i].setComponents(this);
        }
        //Set Enemy's ShipTypeTables
        for (int i = 0; i < NUM_OF_PLAYERS; i++) {
            gameWindowControllers[i].setEnemyShipStateTableView(gameWindowControllers[getOtherSceneIndex(i)].getShipStatsMap());
        }
    }

    private int getOtherSceneIndex(int i) {
        return (i + 1) % NUM_OF_PLAYERS;
    }

    //Return the scene and set the controller
    public Scene generateSceneFromFxmlAndSetController(URL fxmlLocation, int controllerIndex) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Scene scene;

        fxmlLoader.setLocation(fxmlLocation);
        Parent root = fxmlLoader.load();
        gameWindowControllers[controllerIndex] = fxmlLoader.getController();
        gameWindowControllers[controllerIndex].setData(gameEngine.getPlayerData(controllerIndex), boardSize, primaryStage, gameInfo);
        scene = new Scene(root);

        return scene;
    }

    public void startGame() {
        switchToScene(gameScenes[activeSceneIndex], ScenesNames.GAMEWINDOW.value() + (activeSceneIndex + 1));
        setTurnTimerToCurrentTime();
    }

    private void switchToScene(Scene scene, String titleName) {
        primaryStage.setScene(scene);
        primaryStage.setTitle(titleName);
    }

    public void trackButtonClickEventHandler(ActionEvent actionEvent) {
        int column, row;
        Button clickedButton = (Button) actionEvent.getSource();

        //Disabled the button + Mark as Chosen
        clickedButton.setDisable(true);
        //Store attack time in seconds and reset timer
        storeAttackTime();
        //Attack the desired position
        column = GridPane.getColumnIndex(clickedButton);
        row = GridPane.getRowIndex(clickedButton);
        Position positionToAttack = convertVisualPositionToLogicPosition(row, column);
        //Check attack result
        AttackResult attackResult = null;
        try {
            attackResult = gameEngine.attackPosition(positionToAttack, false);
        } catch (NoShipAtPoisitionException e) {
            e.printStackTrace();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        //Check if win
        //Update visual elements with result and Check if user Win
        updateVisualElementsDueToAttackResult(attackResult, row - 1, column - 1);
        //Switch scenes
        handleSceneSwitches();
        setTurnTimerToCurrentTime();
    }

    private void onGameEndActions() {
        showGameWinMsg();
        switchToGameWinScene();
    }

    private void switchToGameWinScene() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Scene scene;
        GameEndWindowController windowController;
        try {
            fxmlLoader.setLocation(getClass().getResource("/view/gameEndWindow/GameEndWindow.fxml"));
            Parent root = fxmlLoader.load();
            windowController = fxmlLoader.getController();
            windowController.set(gameWindowControllers[0], gameWindowControllers[1], gameEngine.getMoveHistory(), this);
            scene = new Scene(root);
            switchToScene(scene, ScenesNames.GAMEENDWINDOW.value());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showGameWinMsg() {
        Alert winMsg = new Alert(Alert.AlertType.INFORMATION, gameEngine.getWinPlayerName() + " Win the game!");
        winMsg.setHeaderText("Win");
        winMsg.setTitle("Win Message");
        winMsg.initModality(Modality.APPLICATION_MODAL);
        // Set the icon (must be included in the project).
        ImageView winImage = new ImageView(this.getClass().getResource("/resources/images/win.png").toString());
        winMsg.setGraphic(winImage);

        winMsg.showAndWait();
    }

    //Switch Scenes
    private boolean needToSwitchScene() {
        return activeSceneIndex != gameEngine.getActivePlayerIndex();
    }

    private void switchGameScene() {
        double height = primaryStage.getHeight();
        double width = primaryStage.getWidth();

        switchActiveSceneIndex();
        fadeOutScene(gameScenes[activeSceneIndex], height, width);
    }

    private void switchActiveSceneIndex() {
        int temp = activeSceneIndex;
        activeSceneIndex = pasiveSceneIndex;
        pasiveSceneIndex = temp;
    }

    private void fadeOutScene(Scene sceneToFadeInAfter, double lastSceneHeight, double lastSceneWidth) {
        FadeTransition ft = new FadeTransition(Duration.millis(1000), this.primaryStage.getScene().getRoot());
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setAutoReverse(false);
        ft.setOnFinished(event -> fadeInScene(sceneToFadeInAfter, lastSceneHeight, lastSceneWidth));
        ft.play();
    }

    private void fadeInScene(Scene sceneToPut, double lastSceneHeight, double lastSceneWidth) {
        sceneToPut.getRoot().setOpacity(0);

        switchToScene(sceneToPut, ScenesNames.GAMEWINDOW.value() + (activeSceneIndex + 1));
//        this.primaryStage.setScene(sceneToPut);
        //Maintain last scene size
        primaryStage.setHeight(lastSceneHeight);
        primaryStage.setWidth(lastSceneWidth);
        FadeTransition ft = new FadeTransition(Duration.millis(1000), sceneToPut.getRoot());
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    private void storeAttackTime() {
        long timeNowInMiliseconds = System.currentTimeMillis();
        long timeDiffInMilliSeconds = (timeNowInMiliseconds - turnTimerInMiliseconds);
        int timeDiffInSeconds = (int) (timeDiffInMilliSeconds / 1000);

        gameEngine.getPlayerData(activeSceneIndex).addAttackTime(timeDiffInSeconds);
    }

    private void setTurnTimerToCurrentTime() {
        turnTimerInMiliseconds = System.currentTimeMillis();
    }

    private void updateVisualElementsDueToAttackResult(AttackResult attackResult, int row, int column) {
        this.gameWindowControllers[activeSceneIndex].updateVisualBoardsOnAttack(attackResult, row, column);
        this.gameWindowControllers[pasiveSceneIndex].updateVisualBoardsOnBeenAttacked(attackResult, row, column);

        updatePlayersStats();
        if (gameEngine.getStatus().equals(GameStatus.END)) {
            onGameEndActions();
        }
    }

    @Override
    public void onMinePutEventHandler(Position minePosition) {
        this.gameEngine.insertMine(minePosition);
        handleSceneSwitches();
    }

    private void handleSceneSwitches() {
        if (needToSwitchScene()) {
            switchGameScene();
        }
    }

    @Override
    public void OnPlayerRetreatEventHandler(String playerName) {
        this.gameEngine.retreat();
//        showRetreatMsg(playerName);
        onGameEndActions();
    }

    //Ship Drown Interface
    @Override
    public void shipDrownEventHandler(Ship drownShip, int shipOwnerIndex) {
        int otherPlayerIndex = Utills.getOtherPlayerIndex(shipOwnerIndex);
        //Update shipTypeList's
        gameWindowControllers[shipOwnerIndex].updatePlayerVisualShipTypeMapOnShipDrown(drownShip);
        gameWindowControllers[otherPlayerIndex].updateEnemyVisualShipTypeMapOnEnemyShipDrown(drownShip);
    }

    public void startNewGame() {
        switchToScene(mainMenuScene,ScenesNames.MAINMENU.value());
    }

    public void restartGame() {
        notifyAllOnRestartGameEventListeners();
    }

    //On restart
    @Override
    public void notifyAllOnRestartGameEventListeners() {
        if (onRestartGameListeners != null) {
            onRestartGameListeners.forEach(listener -> listener.onRestartGameEventHandler());
        }
    }

    @Override
    public void addOnRestartGameEventListener(onRestartGameListener listener) {
        if (onRestartGameListeners == null) {
            onRestartGameListeners = new LinkedList<>();
        }
        onRestartGameListeners.add(listener);
    }

    @Override
    public void removeOnRestartGameEventListener(onRestartGameListener listener) {
        if (onRestartGameListeners != null) {
            onRestartGameListeners.remove(listener);
        }
    }
}