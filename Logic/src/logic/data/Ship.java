package logic.data;

import logic.data.enums.Catagory;
import logic.data.enums.Direction;
import xmlInputManager.*;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private static int counter = 0;
    private final int uniqueId;
    private Catagory catagory;
    private String type;
    private int length, score, hits,numOfSquares;
    private Position position;
    private Direction direction;
    private boolean active, drown;

    public Ship(Catagory catagory, String type, int length, int score, Position position, Direction direction) {
        this.catagory = catagory;
        this.type = type;
        this.length = length;
        this.score = score;
        this.position = position;
        this.direction = direction;
        this.hits = 0;
        this.active = true;
        this.uniqueId = counter;
        setNumOfSquares(length,catagory);
        ++counter; //static
    }

    private void setNumOfSquares(int length, Catagory catagory) {
        if (catagory.equals(Catagory.L_SHAPE)) {
            this.numOfSquares = 2 * length - 1;
        } else {
            this.numOfSquares = length;
        }
    }

    private static Ship makeShipFromInfoShipAndTypeShip(xmlInputManager.Ship infoShip, ShipType shipType) {
        Ship resShip = new Ship(
                Catagory.valueOf(shipType.getCatagory().toUpperCase()),
                infoShip.getShipTypeId(),
                shipType.getLength(),
                shipType.getScore(),
                infoShip.getPosition(),
                Direction.valueOf(infoShip.getDirection().toUpperCase())
        );
        resShip.setLogicalPosition();
        return resShip;
    }

    private void setLogicalPosition() {
        position.dexXY();
    }

    public int leftPart() {
        return length - hits;
    }

    public Direction getDirection() {
        return direction;
    }

    public Position getStartPoint() {
        return position;
    }

    public int getLength() {
        return length;
    }

    public static List<Ship> MakeShipListFromGameInfo(int playerIndex, GameInfo gameInfo) {
        List<xmlInputManager.Ship> infoShipList = gameInfo.getShipList(playerIndex);
        List<Ship> logicShipList = new ArrayList<>(infoShipList.size());
        ShipTypes shipTypes = gameInfo.getShipTypes();

        for (xmlInputManager.Ship infoShip : infoShipList) {
            ShipType currentShipType = null;
            currentShipType = shipTypes.getShipTypeById(infoShip.getShipTypeId());
            Ship shipToAdd = makeShipFromInfoShipAndTypeShip(infoShip, currentShipType);
            logicShipList.add(shipToAdd);
        }

        return logicShipList;
    }

    public int getScore() {
        return score;
    }

    public boolean isDrown() {
        return hits == numOfSquares;
    }

    public void incHits() {
        hits++;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ship ship = (Ship) o;

        return uniqueId == ship.uniqueId;
    }

    @Override
    public int hashCode() {
        return uniqueId;
    }

    public int getUniqueId() {
        return uniqueId;
    }
}
