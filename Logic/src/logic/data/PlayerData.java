package logic.data;

import logic.data.enums.GameType;
import logic.data.enums.ShipBoardSquareValue;
import logic.data.enums.TrackBoardSquareValue;
import logic.exceptions.MinePositionAdjacentToShipException;
import logic.exceptions.LogicallyInvalidXmlInputException;
import logic.exceptions.NoShipAtPoisitionException;
import xmlInputManager.GameInfo;
import xmlInputManager.Position;

import java.util.List;

public class PlayerData implements Cloneable {
    private GameType gameType;
    private ShipBoard shipBoard;
    private TrackBoard trackBoard;
    private int hits, misses, numOfMines, score, turnsCounter, totalAttackTimeInSeconds, playerNumber;
    private GameInfo gameInfo;
    private List<Ship> shipList;
    private int numOfShips;


    public PlayerData(GameInfo gameInfo, int playerNumber, GameType gameType) throws LogicallyInvalidXmlInputException {
        this.hits = this.misses = this.score = 0;
        this.gameInfo = gameInfo;
        this.numOfMines = gameInfo.getMineAmount();
        this.gameType = gameType;
        this.playerNumber = playerNumber;
        setPlayerBoards(playerNumber);
    }

    private void setPlayerBoards(int playerNumber) throws LogicallyInvalidXmlInputException {
        int boardSize;

        boardSize = gameInfo.getBoardSize();
        this.shipList = Ship.MakeShipListFromGameInfo(playerNumber, gameInfo);//todo: to validate this line
        this.numOfShips = shipList.size();
        this.shipBoard = new ShipBoard(shipList, boardSize);
        this.trackBoard = new TrackBoard(boardSize);
    }

    public void markTrackBoard(Position position, TrackBoardSquareValue valueToMark) {
        trackBoard.markPosition(position, valueToMark);
    }

    public void missActions(Position position) {
        markTrackBoard(position, TrackBoardSquareValue.MISS);
        misses++;
    }

    public void updateSuccessShipAttack(Position position, int scoreToAdd, boolean isShipDrown, boolean mineIsTheAttacker) {
        if (!mineIsTheAttacker) {
            incHits();
        }
        markTrackBoard(position, TrackBoardSquareValue.HIT); //mark as a hit at the attacker Track board
        addScoreByGameType(position, scoreToAdd, isShipDrown);
    }

    public void markAttackedShip(Position position) { //mark the enemy ship as hitted
        shipBoard.markAttackedShipInShipBoard(position);
    }

    private void addScoreByGameType(Position position, int scoreToAdd, boolean isShipDrown) {
        if (gameType == GameType.BASIC) {
            score += scoreToAdd;
        } else if (isShipDrown) {
            score += scoreToAdd;
        }
    }

    public int getShipScoreValue(Position position) {
        return shipBoard.getShipBoardScoreValueAtPoint(position);
    }

    public ShipBoardSquareValue getShipBoardSquareValue(Position positionToAttack) {
        return shipBoard.getShipBoardSquareValue(positionToAttack);
    }

    public TrackBoardSquareValue getTrackBoardSquareValue(Position position) {
        return trackBoard.getSquareValue(position);
    }

    public boolean isPositionHaveBeenAttackedBefore(Position position) {
        return trackBoard.getSquareValue(position) != TrackBoardSquareValue.EMPTY;
    }

    public int getScore() {
        return score;
    }

    public ShipBoard getShipBoard() {
        return shipBoard;
    }

    public TrackBoard getTrackBoard() {
        return trackBoard;
    }

    public int getBoardSize() {
        return shipBoard.getBoardSize();
    }

    public boolean isShipDrown(Position position) {
        return shipBoard.shipDrown(position);
    }

    public Ship getship(Position position) throws NoShipAtPoisitionException {
        return shipBoard.getShip(position);
    }

    public void addAttackTime(int timeInSeconds) {
        totalAttackTimeInSeconds += timeInSeconds;
    }

    public int getNumOfMisses() {
        return misses;
    }

    public void incTurnCounter() {
        turnsCounter++;
    }

    public int getTurnsCounter() {
        return turnsCounter;
    }

    public int getTotalAttackTime() {
        return totalAttackTimeInSeconds;
    }

    public int getNumOfHits() {
        return hits;
    }

    public int getNumOfShipBoardSquares() {
        return shipBoard.getNumOfShipSquares();
    }

    public void insertMine(Position insertPosition) {
        shipBoard.addMineToShipBoard(insertPosition);
        numOfMines--;
    }

    public int getNumOfMines() {
        return numOfMines;
    }

    public void incHits() {
        hits++;
    }

    public void removeMine(Position minePosition) {
        shipBoard.removeMineFromBoard(minePosition);
    }

    public String getPlayerName() {
        return String.valueOf(playerNumber + 1);
    }

    public int getAvgAttackTime() {
        int avgAttackTimeInSeconds = 0;

        if (this.turnsCounter != 0) {
            avgAttackTimeInSeconds = this.totalAttackTimeInSeconds / this.turnsCounter;
        }

        return avgAttackTimeInSeconds;
    }

    public List<Ship> getShipList() {
        return shipList;
    }

    public int getNumOfShips() {
        return this.numOfShips;
    }

    public void decNumOfShips() {
        this.numOfShips--;
    }

    public PlayerData clone() throws CloneNotSupportedException {
        PlayerData playerData = (PlayerData) super.clone();
        playerData.trackBoard = (TrackBoard) this.trackBoard.clone();
        playerData.shipBoard = (ShipBoard) this.shipBoard.clone();

        return playerData;
    }
}