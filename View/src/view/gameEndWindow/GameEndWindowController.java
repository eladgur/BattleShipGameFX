package view.gameEndWindow;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import view.gameWindow.GameWindowController;

public class GameEndWindowController {
    @FXML GridPane layoutGridPane;

    public void set(GameWindowController gameWindowController1, GameWindowController gameWindowController2) {
        //Set Stats Grid panes
        layoutGridPane.add(gameWindowController1.getGameStatsGridPane(),0,0);
        layoutGridPane.add(gameWindowController2.getGameStatsGridPane(),1,0);
        //Set Ship Grid panes
        layoutGridPane.add(gameWindowController1.getBoardTabPane(),0,1);
        layoutGridPane.add(gameWindowController2.getBoardTabPane(),1,1);
    }
}
