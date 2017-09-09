package logic;

import logic.data.PlayerData;
import logic.data.enums.AttackResult;

import static logic.data.Constants.NUM_OF_PLAYERS;

public class MoveData {
    PlayerData[] playersData;
    //Fields
    private int playerIndex;
    private AttackResult attackResult;
    private boolean insertMine;

    public MoveData(AttackResult attackResult, boolean insertMine, PlayerData[] playersData, int playerIndex) throws CloneNotSupportedException {
        this.playerIndex = playerIndex;
        this.attackResult = attackResult;
        this.insertMine = insertMine;
        this.playersData = new PlayerData[NUM_OF_PLAYERS];
        for (int i = 0; i < NUM_OF_PLAYERS; i++) {
            this.playersData[i] = playersData[i].clone();
        }
    }

    public int getPlayerIndex() {
        return this.playerIndex;
    }

    public boolean isMinePutted() {
        return this.insertMine;
    }

    public AttackResult getAttackResult() {
        return this.attackResult;
    }

    public PlayerData getPlayerData(int playerIndex) {
        return this.playersData[playerIndex];
    }
}
