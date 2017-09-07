package logic;

import logic.data.*;
import logic.data.enums.*;
import logic.exceptions.IlegalIAttackPositionException;
import logic.exceptions.MinePositionAdjacentToShipException;
import logic.exceptions.LogicallyInvalidXmlInputException;
import logic.exceptions.NoShipAtPoisitionException;
import xmlInputManager.GameInfo;
import xmlInputManager.Position;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import static logic.data.Constants.*;
import static logic.data.enums.AttackResult.*;

public class GameEngine implements ShipDrownListenable {
    private GameType gameType;
    private GameStatus gameStatus;
    private GameInfo gameInfo;
    private int activePlayer, otherPlayer, winPlayer;
    private PlayerData[] playersdata;
    private GameInfoValidator gameInfoValidator;
    public List<ShipDrownListener> shipDrownListeners;
    private int turnsCounter;

    public GameEngine() {
        activePlayer = FIRSTPLAYER;
        otherPlayer = SECONDSPLAYER;
        gameStatus = GameStatus.WAITING_FOR_ATTACK_POSITION;
    }

    private LocalTime startTime;

    public void loadAndValidateGameInfo(GameInfo gameInfo) throws LogicallyInvalidXmlInputException {
        this.gameInfo = gameInfo;
        validateInputData();
        setGameType(gameInfo.getGameType());
        setPlayersData();
    }

    private void validateInputData() throws LogicallyInvalidXmlInputException {
        gameInfoValidator = new GameInfoValidator(gameInfo);
        gameInfoValidator.validate();
    }

    private void setPlayersData() throws LogicallyInvalidXmlInputException {
        playersdata = new PlayerData[NUM_OF_PLAYERS];
        for (int playerNumber = 0; playerNumber < NUM_OF_PLAYERS; playerNumber++) {
            playersdata[playerNumber] = new PlayerData(gameInfo, playerNumber, gameType);
        }
    }

    private void setGameType(String gameTypeName) {
        this.gameType = GameType.valueOf(gameTypeName);
    }

    public AttackResult attackPosition(Position positionToAttack, boolean mineIsTheAttacker) throws NoShipAtPoisitionException {
        ShipBoardSquareValue squareValue;
        AttackResult attackResult;

        turnsCounter++;
        playersdata[activePlayer].incTurnCounter();
        squareValue = playersdata[otherPlayer].getShipBoardSquareValue(positionToAttack);
        if (!positionBeenAttackedBefore(positionToAttack)) {
            switch (squareValue) {
                case WATER:
                    waterSquareAttackedActions(positionToAttack);
                    attackResult = MISSHIT;
                    break;
                case SHIP:
                    attackResult = attackShip(positionToAttack);
                    break;
                case MINE:
                    attackResult = mineAttackedSquareActions(positionToAttack);
                    break;
                default:
                    attackResult = MISSHIT;
                    swapActivePlayer();
                    break;
            }
        } else {
            attackResult = REPEATEDHIT;
        }

        return attackResult;
    }

    private void swapActivePlayer() {
        int temp = activePlayer;
        activePlayer = otherPlayer;
        otherPlayer = temp;
    }

    private boolean positionBeenAttackedBefore(Position position) {
        return playersdata[activePlayer].isPositionHaveBeenAttackedBefore(position);
    }

    private void waterSquareAttackedActions(Position positionToAttack) {
        playersdata[activePlayer].missActions(positionToAttack);
        swapActivePlayer();
    }

    private AttackResult attackShip(Position position) throws NoShipAtPoisitionException {
        AttackResult attackResult;
        boolean isShipDrown = false;
        int scoreToAdd;

        playersdata[otherPlayer].markAttackedShip(position);
        scoreToAdd = playersdata[otherPlayer].getShipScoreValue(position);
        isShipDrown = playersdata[otherPlayer].isShipDrown(position);
        playersdata[activePlayer].updateSuccessShipAttack(position, scoreToAdd, isShipDrown, false); //update hits, TrackBoard, score
        if (isShipDrown) {
            attackResult = shipDrownActions(position);
        } else {
            attackResult = AttackResult.SHIPHIT;
        }
        updateGameStatusAfterAttack();

        return attackResult;
    }

    private AttackResult mineAttackedSquareActions(Position minePosition) throws NoShipAtPoisitionException {
        boolean isShipDrown = false;
        AttackResult attackResult = MINEWATER;
        ShipBoardSquareValue squareValue = playersdata[activePlayer].getShipBoardSquareValue(minePosition);

        //Update stats
        playersdata[activePlayer].incHits();
        playersdata[activePlayer].markAttackedShip(minePosition);
        //Check Attack result
        switch (squareValue) {
            case WATER:
                attackResult = MINEWATER;
                break;
            case SHIP:
                if (!playersdata[otherPlayer].isPositionHaveBeenAttackedBefore(minePosition)) {
                    int scoreToAdd = playersdata[activePlayer].getShipScoreValue(minePosition);
                    isShipDrown = playersdata[activePlayer].isShipDrown(minePosition);
                    playersdata[otherPlayer].updateSuccessShipAttack(minePosition, scoreToAdd, isShipDrown, true); //update hits, TrackBoard, score
                    if (isShipDrown) {
                        attackResult = MINEDROWNSHIP;
                        Ship drownShip = playersdata[activePlayer].getship(minePosition);
                        playersdata[activePlayer].decNumOfShips();
                        attackResult.setShip(drownShip);
                    } else {
                        attackResult = MINESHIP;
                    }
                    //TODO:(ADVANCE) To Check and handle ship down event
                } else {
                    attackResult = MINEREAPETEDHIT;
                }
                break;
            case MINE:
                playersdata[activePlayer].removeMine(minePosition);
                attackResult = MINEMINE;
                break;
            default:
                attackResult = MINEWATER;
                break;
        }

        playersdata[otherPlayer].removeMine(minePosition);
        swapActivePlayer();
        updateGameStatusAfterAttack();

        return attackResult;
    }

    private void updateGameStatusAfterAttack() {
        if (isActivePlayerWin()) {
            gameStatus = GameStatus.END;
            winPlayer = activePlayer;
        }
    }

    private boolean isActivePlayerWin() {
        return playersdata[otherPlayer].getNumOfShips() <= 0;
    }

    private AttackResult shipDrownActions(Position position) throws NoShipAtPoisitionException {
        Ship drownShip = getShip(position);
        AttackResult attackResult = AttackResult.SHIPDROWNHIT;
        attackResult.setShip(drownShip);
        playersdata[otherPlayer].decNumOfShips();
//        notifyAllShipDrownListeners(drownShip);
        return attackResult;
    }

    private Ship getShip(Position position) throws NoShipAtPoisitionException {
        Ship ship = playersdata[otherPlayer].getship(position);

        return ship;
    }

    public PlayerData getPlayerData() {
        return getPlayerData(activePlayer);
    }

    public PlayerData getPlayerData(int playerNumber) {
        return playersdata[playerNumber];
    }

    public PlayerData[] getPlayersdata() {
        return playersdata;
    }

    public String getActivePlayerName() {
        return String.format("Player%d", activePlayer + 1);
    }

    public void validateAttackPosition(Position position) throws IlegalIAttackPositionException {
        int x = position.getX();
        int y = position.getY();
        final int minBoardIndex = 0;
        final int maxBoardIndex = gameInfo.getBoardSize() - 1;

        if (position.getX() < minBoardIndex || position.getX() > maxBoardIndex) {
            throw new IlegalIAttackPositionException(x, y);
        }
        if (position.getY() < minBoardIndex || position.getY() > maxBoardIndex) {
            throw new IlegalIAttackPositionException(x, y);
        }
    }

    @Override
    public void addShipDrownListener(ShipDrownListener listenerToAdd) {
        if (shipDrownListeners == null) {
            shipDrownListeners = new LinkedList<ShipDrownListener>();
        }
        shipDrownListeners.add(listenerToAdd);
    }

    @Override
    public void removeShipDrownListener(ShipDrownListener listenerToRemove) {
        if (shipDrownListeners != null) {
            shipDrownListeners.remove(listenerToRemove);
        }
    }

    @Override
    public void notifyAllShipDrownListeners(Ship drownShip) {
        if (shipDrownListeners != null) {
            for (ShipDrownListener shipDrownListener : shipDrownListeners) {
                shipDrownListener.shipDrownEventHandler(drownShip);
            }
        }
    }

    public void startGame() {
        startTime = LocalTime.now();
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public int getTotalTurnsCounter() {
        return turnsCounter;
    }

    public void storePlayerAttackTime(int timeInSeconds) {
        playersdata[activePlayer].addAttackTime(timeInSeconds);
    }

    public GameStatus getStatus() {
        return gameStatus;
    }

    public void retreat() {
        winPlayer = otherPlayer;
        gameStatus = GameStatus.END;
    }

    public String getWinPlayerName() {
        return String.format("Player%d", winPlayer + 1);
    }

    public String getLosePlayerName() {
        return String.format("Player%d", ((winPlayer + 1) % NUM_OF_PLAYERS) + 1);
    }

    public void insertMine(Position insertPosition) {
        playersdata[activePlayer].insertMine(insertPosition);
        swapActivePlayer();
    }

    public int getActivePlayerIndex() {
        return this.activePlayer;
    }

    public boolean isValidMineLocation(Position logicalPoistion) {
        boolean isValidLocation;
        try {
            playersdata[activePlayer].getShipBoard().ensureMinePositionIsValid(logicalPoistion.getX(), logicalPoistion.getY());
            isValidLocation = true;
        } catch (MinePositionAdjacentToShipException e) {
            isValidLocation = false;
        }

        return isValidLocation;
    }
}
