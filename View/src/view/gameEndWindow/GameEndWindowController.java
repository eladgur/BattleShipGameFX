package view.gameEndWindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import logic.MoveData;
import logic.data.enums.AttackResult;
import utills.MyIterator;
import utills.Utills;
import view.gameWindow.GameWindowController;

import javax.script.Bindings;
import java.util.List;

public class GameEndWindowController {
    @FXML GridPane layoutGridPane;
    @FXML Label attackResultLabel;
    @FXML Button previousMoveButton;
    @FXML Button nextMoveButton;
    private GameWindowController[] gameWindowControllers;
    private List<MoveData> movesHistoryList;
    private MyIterator<MoveData> moveIterator;

    public void set(GameWindowController gameWindowController1, GameWindowController gameWindowController2, List<MoveData> movesHistoryList) {
        //Set Stats Grid panes
        this.layoutGridPane.add(gameWindowController1.getGameStatsGridPane(), 0, 0);
        this.layoutGridPane.add(gameWindowController2.getGameStatsGridPane(), 1, 0);
        //Store GameWindowControllers
        this.gameWindowControllers = new GameWindowController[2];
        this.gameWindowControllers[0] = gameWindowController1;
        this.gameWindowControllers[1] = gameWindowController2;
        //Set Ship Grid panes
        this.layoutGridPane.add(gameWindowController1.getBoardTabPane(), 0, 1);
        this.layoutGridPane.add(gameWindowController2.getBoardTabPane(), 1, 1);
        //Set Moves History List
        this.movesHistoryList = movesHistoryList;
        this.moveIterator = new MyIterator<>(this.movesHistoryList);
        //Bind Previous and next button to Iterator
        this.nextMoveButton.disableProperty().bind(moveIterator.getHasNextProperty().not());
        this.previousMoveButton.disableProperty().bind(moveIterator.getHasPreviousProperty().not());
        //Load First Move Data For Replays
        if (movesHistoryList != null && movesHistoryList.size() >= 1) {
            onPreviusMoveButtonClicked(new ActionEvent());
        }
    }

    public void onNextMoveButtonClicked(ActionEvent actionEvent) {
            MoveData moveData = moveIterator.next();
            updateVisualComponentsWithOnReplay(moveData);
    }

    public void onPreviusMoveButtonClicked(ActionEvent actionEvent) {
            MoveData moveData = moveIterator.previous();
            updateVisualComponentsWithOnReplay(moveData);
    }

    private void updateVisualComponentsWithOnReplay(MoveData moveData) {
        int playerIndex = moveData.getPlayerIndex();
        int otherPlayerIndex = Utills.getOtherPlayerIndex(playerIndex);
        AttackResult attackResult = moveData.getAttackResult();

        //Update Attack Result Label
        this.attackResultLabel.setText(makeTextFromAttackResult(attackResult));
        //Update Boards
        this.gameWindowControllers[playerIndex].updateVisualComponantsOnReplay(moveData.getPlayerData(playerIndex));
        this.gameWindowControllers[otherPlayerIndex].updateVisualComponantsOnReplay(moveData.getPlayerData(otherPlayerIndex));
    }

    private String makeTextFromAttackResult(AttackResult attackResult) {
        String text;

        switch (attackResult) {
            case SHIPHIT:
                text = "Ship Hit";
                break;
            case SHIPDROWNHIT:
                text = "Ship Drown";
                break;
            case REPEATEDHIT:
                text = "";
                break;
            case MISSHIT:
                text = "Miss";
                break;
            case MINESHIP:
                text = "Mine Hit Ship";
                break;
            case MINEDROWNSHIP:
                text = "Mine Hit Drown Ship";
                break;
            case MINEWATER:
                text = "Mine Hit Water";
                break;
            case MINEMINE:
                text = "Mine Hit Mine";
                break;
            case MINEREAPETEDHIT:
                text = "";
                break;
            case INSERTMINE:
                text = "Insert Mine";
                break;
            default:
                text = "";
                break;
        }

        return text;
    }
}
