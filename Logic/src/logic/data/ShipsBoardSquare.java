package logic.data;

import logic.data.enums.ShipBoardSquareValue;
import logic.exceptions.LogicallyInvalidXmlInputException;
import xmlInputManager.Position;

public class ShipsBoardSquare implements Cloneable{
    private ShipBoardSquareValue value;
    private Position position;
    private Ship anchorShip;
    private boolean hit;
    public ShipsBoardSquare(Position position) {
        this.position = position;
        this.value = ShipBoardSquareValue.WATER;
        hit = false;
    }

    public void setHit() {
        if (!this.hit && anchorShip != null) { // inc hits only if it the first time this square was hit!
            anchorShip.incHits();
        }
        this.hit = true;
    }

    //Throw LogicallyInvalidXmlInputException if we try to put a ship in a square who belong to other ship
    public void setShip(Ship ship) throws LogicallyInvalidXmlInputException {
//        if (value != ShipBoardSquareValue.WATER && !anchorShip.equals(ship)) {
//            throw  new LogicallyInvalidXmlInputException(String.format("Can't put more then one item at (%s)",position));
//        }
        anchorShip = ship;
        value = ShipBoardSquareValue.SHIP;
    }

    public void setMine() {
        this.value = ShipBoardSquareValue.MINE;
    }

    public ShipBoardSquareValue getValue() {
        return this.value;
    }

    public int getScoreValue() {
        if (anchorShip != null) {
            return anchorShip.getScore();
        } else {
            return 0;
        }
    }

    public boolean isWater() {
        return this.value == ShipBoardSquareValue.WATER;
    }

    public boolean isDrown() {
        return this.anchorShip.isDrown();
    }

    public boolean isShip() {
        return this.value == ShipBoardSquareValue.SHIP;
    }

    public Ship getAnchorShip() {
        return this.anchorShip;
    }

    public boolean isHit() {
        return this.hit;
    }

    public void setWater() {
        this.value = ShipBoardSquareValue.WATER;
    }

    public ShipsBoardSquare clone() throws CloneNotSupportedException {
        ShipsBoardSquare shipsBoardSquare = (ShipsBoardSquare) super.clone();

        return shipsBoardSquare;
    }
}
