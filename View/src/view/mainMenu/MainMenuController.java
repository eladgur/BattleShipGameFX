package view.mainMenu;

import controller.GameController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;
import main.Main;
import view.gameWindow.LoadGameTask;

import java.awt.*;
import java.io.File;

public class MainMenuController implements onRestartGameListener {
    private Main main;
    private GameController gameController;
    private LoadGameTask loadGameTask;
    @FXML private ProgressBar progressBar;
    @FXML private ProgressIndicator progressIndicator;
    @FXML Button startGameButton;
    private File file;

    private BooleanProperty gameLoaded;

    public MainMenuController() {
        this.gameLoaded = new SimpleBooleanProperty(false);
    }

    private void setLoadGameTask() {
        //Create the Task
        this.loadGameTask = new LoadGameTask(this.main,this);
        //Set File to load from at the task
        loadGameTask.setFileToLoadFrom(this.file);
        //Bind ProgressBar
        //Visible Binding
        this.progressBar.visibleProperty().bind(loadGameTask.runningProperty());
        this.progressIndicator.visibleProperty().bind(loadGameTask.runningProperty());
        //Progress Binding
        this.progressBar.progressProperty().bind(loadGameTask.progressProperty());
        progressIndicator.progressProperty().bind(loadGameTask.progressProperty());
        //Bind Start Button
        this.startGameButton.disableProperty().bind(this.gameLoaded.not());
        //Register Events
        this.loadGameTask.setOnSucceeded(this::onSuccessLoadGameEventHandler);
        this.loadGameTask.setOnFailed(this::onFailedLoadGameEventHandler);
    }

    public void setMain(Main main) {
        this.main = main;
    }

    @FXML
    public void loadGameButtonClickHandler() {
        //Show progressItems
//        this.progressBar.setOpacity(1);
        this.progressIndicator.setOpacity(1);
        //Call load Game from user actions
        loadGameXmlFileFromUser();
    }

    private void loadGameXmlFileFromUser() {
        boolean isValidFile = false, isValidFilePath = false;
        FileChooser fileChooser;
        String errorMsg = "";

        isValidFilePath = getGameFileFromUser();
        if (isValidFilePath) {
            setLoadGameTask();
            new Thread(loadGameTask).start();
        }
    }

    private boolean getGameFileFromUser() {
        FileChooser fileChooser;
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML File", "*.xml"));
        fileChooser.setTitle("Choose xml file");
        this.file = fileChooser.showOpenDialog(main.getPrimaryStage());

        return !(this.file == null); //This Return False in case that user Hit Cancel
    }

    public void startGameButtonClickHandler(ActionEvent actionEvent) {
        this.gameLoaded.set(false); //For Next time the Main Menu will be Showed
        this.gameController = this.loadGameTask.getGameController();
        this.loadGameTask.getGameController().startGame();
    }

    //Load Game Task Event Handlers
    private void onSuccessLoadGameEventHandler(WorkerStateEvent workerStateEvent) {
        this.gameLoaded.set(true);
    }

    private void onFailedLoadGameEventHandler(WorkerStateEvent workerStateEvent) {
        Exception exception = (Exception) workerStateEvent.getSource().getException();

        //Show Error Alert Message
        new Alert(Alert.AlertType.ERROR, exception.getMessage()).showAndWait();
    }

    @Override
    public void onRestartGameEventHandler() {
        //Create the Task
        this.loadGameTask = new LoadGameTask(this.main, this);
        //Set File to load from at the task
        loadGameTask.setFileToLoadFrom(this.file);
        Thread thread = new Thread(loadGameTask);
        thread.start();
        try {
            thread.join();
            startGameButtonClickHandler(new ActionEvent());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
