package logic.data;

import logic.data.enums.Catagory;
import logic.data.enums.Direction;
import logic.data.enums.GameType;
import logic.exceptions.LogicallyInvalidXmlInputException;
import logic.exceptions.LogicallyInvalidXmlShipLocationInXmlInput;
import xmlInputManager.*;
import xmlInputManager.Ship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static logic.data.Constants.*;

/**
 * This validator class ensure that the input is logically correct,
 * Except for the check for all the ships square that will be checked in the creation of the ships board for better Performance
 */

public class GameInfoValidator {

    GameInfo gameInfo;
    int boardSize;
    GameType gameType;
    private Map<String,Integer> shipTypesMap;

    public GameInfoValidator(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        boardSize = gameInfo.getBoardSize();
    }

    public void validate() throws LogicallyInvalidXmlInputException {
        validateGameType();
            validateMineAmount();
            validateBoardSize();
            validateShipTypes();
            validateShipsAmount();
            validateShipsFields();
            //TODO: to check if it is ok becaouse all thuse line were in if(GameType=="BASIC"){} ....
    }

    private void validateGameType() throws LogicallyInvalidXmlInputException {
        String gameTypeName = gameInfo.getGameType().toUpperCase();

        if (!GameType.isValidGameTypeName(gameTypeName)) {
            throw new LogicallyInvalidXmlInputException(String.format("(%s) is not a valid game type", gameTypeName));
        } else {
            this.gameType = GameType.valueOf(gameTypeName);
        }
    }

    private void validateMineAmount() throws LogicallyInvalidXmlInputException {
        int minesAmount = gameInfo.getMineAmount();
        if (minesAmount < MIN_NUM_OF_MINE) {
            throw new LogicallyInvalidXmlInputException(String.format("Illegal mine amount: (%d)", minesAmount));
        }
    }

    private void validateShipTypes() throws LogicallyInvalidXmlInputException {
        List<ShipType> shipTypesList = gameInfo.getShipTypes().getShipTypesList();
        String shipTypeId;

        if (shipTypesList == null) {
            throw  new LogicallyInvalidXmlInputException("0 ship Types provided, please provide at least 1 ship type");
        } else {
            validateShipTypesSingleDeclerationAndSetShipTypesMap(shipTypesList);
        }

        for (ShipType shipType : shipTypesList) {
            shipTypeId = shipType.getId();
            validateCatagory(shipType.getCatagory(), shipTypeId);
            validateLength(shipType.getLength(), shipTypeId);
            if (gameType.equals(GameType.BASIC)) {
                shipType.setScore(1);
            } else {
                validateScore(shipType.getScore(), shipTypeId);
            }
        }
    }

    private void validateShipTypesSingleDeclerationAndSetShipTypesMap(List<ShipType> shipTypesList) throws LogicallyInvalidXmlInputException {
        int size = shipTypesList.size(), currentValue;
        String currentId;
        Map<String, Integer> shipTypesMap = new HashMap<>(size);

        for (ShipType shipType : shipTypesList) {
            currentId = shipType.getId();
            if (shipTypesMap.containsKey(currentId)) {
                throw new LogicallyInvalidXmlInputException(String.format("Multiple declarations of ship type id: (%s)", shipType.getId()));
            } else {
                shipTypesMap.put(currentId,1);
            }
        }

        this.shipTypesMap = shipTypesMap; // for other check's in O(1)
    }

    private void validateBoardSize() throws LogicallyInvalidXmlInputException {
        int boardSize = gameInfo.getBoardSize();

        if (boardSize < MIN_BOARD_SIZE || boardSize > MAX_BOARD_SIZE) {
            throw new LogicallyInvalidXmlInputException(String.format("invalid board size (%d)", boardSize));
        }
    }

    private void validateShipsAmount() throws LogicallyInvalidXmlInputException {
        int shipTypeDeclaredAmount, actualShipTypeAmount;
        String currentShipTypeId, declaredShipTypeId;

        for (ShipType shipType : gameInfo.getShipTypes().getShipTypesList()) {
            shipTypeDeclaredAmount = shipType.getAmount();
            declaredShipTypeId = shipType.getId();
            for (int playerNumber = 0; playerNumber < NUM_OF_PLAYERS; playerNumber++) {
                actualShipTypeAmount = 0;
                for (Ship ship : gameInfo.getShipList(playerNumber)) {
                    currentShipTypeId = ship.getShipTypeId();
                    if (currentShipTypeId.equals(declaredShipTypeId)) {
                        actualShipTypeAmount++;
                    }
                }
                if (actualShipTypeAmount != shipTypeDeclaredAmount) {
                    throw new  LogicallyInvalidXmlInputException(String.format("Declared amount: (%d) of Ship-Type: (%s) is different then actual given amount: (%d)",
                            shipTypeDeclaredAmount, declaredShipTypeId, actualShipTypeAmount));
                }
            }
        }
    }

    private void validateShipsFields() throws LogicallyInvalidXmlInputException {
        for (int playerNumber = 0; playerNumber < NUM_OF_PLAYERS; playerNumber++) {
            for (Ship ship : gameInfo.getShipList(playerNumber)) {
                validateShipTypeDeclared(ship.getShipTypeId());
                validatePositionInBoard(ship.getPosition());
                validateDirection(ship.getDirection());
            }
        }
    }

    private void validateDirection(String directionName) throws LogicallyInvalidXmlInputException {
        if (!(Direction.isValidDirectionName(directionName))) {
            throw new LogicallyInvalidXmlInputException(String.format("(%s) is not a valid direction", directionName));
        }
    }

    private void validateShipTypeDeclared(String shipTypeId) throws LogicallyInvalidXmlInputException {
        boolean isDeclared = shipTypesMap.containsKey(shipTypeId);

        if (!isDeclared) {
            throw new LogicallyInvalidXmlInputException(String.format("Ship type (%s) is not a declared ship type", shipTypeId));
        }
    }

    public void validatePositionInBoard(Position position) throws LogicallyInvalidXmlInputException {
        int min = 1, max = gameInfo.getBoardSize();
        int x = position.getX();
        int y = position.getY();

        //Validate X
        if (x < min || x > max) {
            throw new LogicallyInvalidXmlShipLocationInXmlInput(x, y);
        }
        //Validate Y
        if (y < min || y > max) {
            throw new LogicallyInvalidXmlShipLocationInXmlInput(x, y);
        }
    }

    private void validateCatagory(String catagoryName, String shipTypeId) throws LogicallyInvalidXmlInputException {
        if (!(Catagory.isACatagory(catagoryName,gameType))) {
            throw  new LogicallyInvalidXmlInputException(String.format("Catagory name (%s) not valid at ship type %s", catagoryName, shipTypeId));
        }
    }

    private void validateLength(int length, String shipTypeId) throws LogicallyInvalidXmlInputException {
        if (length < SHIP_MIN_LENGTH || length > boardSize) {
            throw new LogicallyInvalidXmlInputException(String.format("Ship type (%s) has a unvalid ship length (%d)",shipTypeId ,length));
        }
    }

    private void validateScore(int score, String shipTypeId) throws LogicallyInvalidXmlInputException {
        if (score < MIN_SHIP_SCORE) {
            throw new LogicallyInvalidXmlInputException(String.format("Ship type (%s) has un valid score value (%d)", shipTypeId, score));
        }
    }

}
