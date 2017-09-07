package logic.exceptions;

public class MinePositionAdjacentToShipException extends Exception{
    private static final String msg = "Ilegal mine Postion: ";
    private int adjacentShipX, adjacentShipY;

    public MinePositionAdjacentToShipException(String msgToAdd, int adjacentShipX, int adjacentShipY) {
        this(msgToAdd);
        this.adjacentShipX = adjacentShipX;
        this.adjacentShipY = adjacentShipY;
    }

    public MinePositionAdjacentToShipException(String msgToAdd) {
        super(msg + msgToAdd);
    }

    public int getAdjacentShipX() {
        return adjacentShipX;
    }

    public int getAdjacentShipY() {
        return adjacentShipY;
    }

}
