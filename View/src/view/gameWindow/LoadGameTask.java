package view.gameWindow;

import controller.GameController;
import javafx.concurrent.Task;
import logic.GameEngine;
import logic.exceptions.LogicallyInvalidXmlInputException;
import main.Main;
import xmlInputManager.GameInfo;
import xmlInputManager.InvalidXmFormatException;
import xmlInputManager.XmlReader;

import java.io.File;

public class LoadGameTask extends Task<Boolean> {

    private Main main;
    private GameEngine gameEngine;
    private GameInfo gameInfo;
    private int boardSize;
    private File fileToLoadFrom;
    private GameController gameController;

    public LoadGameTask(Main main) {
        this.main = main;
    }

    @Override
    public Boolean call() throws LogicallyInvalidXmlInputException, InvalidXmFormatException {
        Boolean isValid;

        updateValue(false);
        initializeGameEngine();
        isValid = true;
        updateProgressWithSleep(0.5);
        this.gameController = new GameController(this.gameEngine, main.getPrimaryStage(), this.gameInfo);
        succeeded();
        updateProgressWithSleep(1);

        return isValid;
    }

    private void initializeGameEngine() throws InvalidXmFormatException, LogicallyInvalidXmlInputException {
        this.gameEngine = new GameEngine();
        this.gameInfo = XmlReader.getDataFromXml(this.fileToLoadFrom.getPath());
        this.gameEngine.loadAndValidateGameInfo(gameInfo);
        this.boardSize = gameEngine.getPlayerData().getBoardSize();
    }

    private void updateProgressWithSleep(double workDone) {
        updateProgress(workDone, 1);
    }

    public void setFileToLoadFrom(File fileToLoadFrom) {
        this.fileToLoadFrom = fileToLoadFrom;
    }

    public GameController getGameController() {
        return this.gameController;
    }

    public GameEngine getGameEngine() {
        return this.gameEngine;
    }
}
