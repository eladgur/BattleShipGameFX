package logic.exceptions;

public class NoShipAtPoisitionException extends Exception{
    public NoShipAtPoisitionException() {
        super("noShipAtPoisitionException: No ship at position");
    }

    public NoShipAtPoisitionException(int x, int y) {
            super(String.format("noShipAtPoisitionException: No ship at position %d,%d",x,y));
        }
    }
