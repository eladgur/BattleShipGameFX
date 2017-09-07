package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import logic.data.PlayerData;

public class PlayerStats {
    private StringProperty name;
    private StringProperty score;
    private StringProperty hits;
    private StringProperty misses;
    private StringProperty avgAttackTime;
    private StringProperty turns;

    public PlayerStats(PlayerData playerData, Label playerNameLabel, Label scoreLabel, Label hitsLabel, Label missesLabel, Label avgAttackTimeLabel, Label turnLabel) {
        this.name = new SimpleStringProperty("Player" + playerData.getPlayerName());
        this.score = new SimpleStringProperty(String.valueOf(playerData.getScore()));
        this.hits = new SimpleStringProperty(String.valueOf(playerData.getNumOfHits()));
        this.misses = new SimpleStringProperty(String.valueOf(playerData.getNumOfMisses()));
        this.avgAttackTime = new SimpleStringProperty(String.valueOf(playerData.getAvgAttackTime()));
        this.turns = new SimpleStringProperty(String.valueOf(playerData.getTurnsCounter()));
        bindLabesToProperties(playerNameLabel,scoreLabel,hitsLabel,missesLabel,avgAttackTimeLabel,turnLabel);
    }

    private void bindLabesToProperties(Label playerNameLabel, Label scoreLabel, Label hitsLabel, Label missesLabel, Label avgAttackTimeLabel, Label turnLabel) {
        playerNameLabel.textProperty().bind(name);
        scoreLabel.textProperty().bind(score);
        hitsLabel.textProperty().bind(hits);
        missesLabel.textProperty().bind(misses);
        avgAttackTimeLabel.textProperty().bind(avgAttackTime);
        turnLabel.textProperty().bind(turns);
    }

    public void update(PlayerData playerData){
        this.name.set("Player" + playerData.getPlayerName());
        this.score.set(String.valueOf(playerData.getScore()));
        this.hits.set(String.valueOf(playerData.getNumOfHits()));
        this.misses.set(String.valueOf(playerData.getNumOfMisses()));
        this.avgAttackTime.set(String.valueOf(playerData.getAvgAttackTime()));
        this.turns.set(String.valueOf(playerData.getTurnsCounter()));
    }
}
