package logic.data;

import logic.data.enums.Direction;
import logic.data.enums.ShipBoardSquareValue;
import logic.exceptions.MinePositionAdjacentToShipException;
import logic.exceptions.LogicallyInvalidXmlInputException;
import logic.exceptions.LogicallyInvalidXmlShipLocationInXmlInput;
import logic.exceptions.NoShipAtPoisitionException;
import xmlInputManager.Position;

import java.util.List;

public class ShipBoard implements Cloneable{
    private ShipsBoardSquare[][] board;
    private final int rows, columns;
    private int shipSquaresCounter;

    public ShipBoard(List<Ship> shipList, int boardSize) throws LogicallyInvalidXmlInputException {
        this.rows = this.columns = boardSize;
        initShipBoard(shipList);
    }

    private void initShipBoard(List<Ship> shipList) throws LogicallyInvalidXmlInputException {
        board = new ShipsBoardSquare[rows][columns];
        initShipBoardSquares();
        for (Ship ship : shipList) {
            setShipInboard(ship);
        }
    }

    private void initShipBoardSquares() {
        for (int i = 0; i < rows; i++) { //Init all Squares if water value
            for (int j = 0; j < columns; j++) {
                board[i][j] = new ShipsBoardSquare(new Position(i, j));
            }
        }
    }

    //Throw LogicallyInvalidXmlInputException if there is any other ships near by
    private void setShipInboard(Ship ship) throws LogicallyInvalidXmlInputException {
        Direction shipDirection = ship.getDirection();
        Position startingPosition = ship.getStartPoint();
        int row = startingPosition.getX(), cul = startingPosition.getY(), shipLength = ship.getLength();

        switch (shipDirection) {
            case ROW:
                validateAndSetShipSquareInRow(ship, row, cul, shipLength);
                break;
            case COLUMN:
                validateAndSetShipSquareInCul(ship, row, cul, shipLength);
                break;
            case DOWN_RIGHT:
                validateAndSetShipSquareInCul(ship, row - shipLength + 1, cul, shipLength);
                validateAndSetShipSquareInRow(ship, row, cul, shipLength);
                break;
            case UP_RIGHT:
                validateAndSetShipSquareInCul(ship, row, cul, shipLength);
                validateAndSetShipSquareInRow(ship, row, cul, shipLength);
                break;
            case RIGHT_UP:
                validateAndSetShipSquareInRow(ship, row, cul - shipLength + 1, shipLength);
                validateAndSetShipSquareInCul(ship, row - shipLength + 1, cul, shipLength);
                break;
            case RIGHT_DOWN:
                validateAndSetShipSquareInRow(ship, row, cul - shipLength + 1, shipLength);
                validateAndSetShipSquareInCul(ship, row, cul, shipLength);
                break;
        }
    }

    private void validateAndSetShipSquareInRow(Ship ship, int row, int firstCul, int shipLength) throws LogicallyInvalidXmlInputException {
        for (int currentCul = firstCul; currentCul < firstCul + shipLength; currentCul++) {
                ensureValidLocation(row, currentCul, ship);
                board[row][currentCul].setShip(ship);
                shipSquaresCounter++;
        }
    }

    private void validateAndSetShipSquareInCul(Ship ship, int firstRow, int column, int shipLength) throws LogicallyInvalidXmlInputException {
        for (int currentRow = firstRow; currentRow < firstRow + shipLength; currentRow++) {
            if (!board[currentRow][column].isShip()) {
                ensureValidLocation(currentRow, column, ship);
                board[currentRow][column].setShip(ship);
                shipSquaresCounter++;
            }
        }
    }

    private void ensureValidLocation(int x, int y, Ship ship) throws LogicallyInvalidXmlInputException {

        ensureShiplocationIsInBoarders(x, y, ship);
        ensureShipLocationIsNotNearOtherShip(x, y, ship);
    }

    private void ensureShipLocationIsNotNearOtherShip(int x, int y, Ship ship) throws LogicallyInvalidXmlInputException {
        final int minLength = -1, maxLength = 1; //the adjacent length to check at
        boolean isAnotherShip;

        for (int row = x + minLength; row <= x + maxLength; row++) {
            for (int cul = y + minLength; cul <= y + maxLength; cul++) {
                if (isLocationValid(row, cul) && board[row][cul].isShip()) {
                    isAnotherShip = !(board[row][cul].getAnchorShip().equals(ship));
                    if (isAnotherShip) { //We found another ship near by
                        throw new LogicallyInvalidXmlShipLocationInXmlInput(String.format("Adjacent ships at location (%d,%d)", x + 1, y + 1), x + 1, y + 1);
                    }
                }
            }
        }
    }

    private void ensureShiplocationIsInBoarders(int x, int y, Ship ship) throws LogicallyInvalidXmlInputException {
        if (!isLocationValid(x, y)) {
            Position shipStartingPosition = ship.getStartPoint();
            int startX = shipStartingPosition.getX();
            int startY = shipStartingPosition.getY();
            throw new LogicallyInvalidXmlInputException(String.format("ship at starting location (%d,%d) get out of borders at location: (%d,%d)", startX + 1, startY + 1, x + 1, y + 1));
        }
    }

    private boolean isLocationValid(int x, int y) {
        boolean valid = true;

        if (x < 0 || x >= board.length) {
            valid = false;
        } else if (y < 0 || y >= board.length) {
            valid = false;
        }

        return valid;
    }

    public void addMineToShipBoard(Position minePosition) {
        int x = minePosition.getX(), y = minePosition.getY();

//        ensureMinePositionIsValid(x, y);
        board[x][y].setMine();
    }

    public void ensureMinePositionIsValid(int row, int coulmn) throws MinePositionAdjacentToShipException {
        if (!isValidMinePosition(row,coulmn)) {
            throw new MinePositionAdjacentToShipException("ship nearby at position ", row, coulmn);
        }
    }

    public boolean isValidMinePosition(int x, int y) {
        final int minLength = -1, maxLength = 1; //the adjacent length to check at
        boolean isValidPosition = true;

        for (int row = x + minLength; row <= x + maxLength; row++) {
            for (int cul = y + minLength; cul <= y + maxLength; cul++) {
                if (isLocationValid(row, cul) && board[row][cul].isShip()) {
                    isValidPosition = false;
                    break;
                }
            }
        }

        return isValidPosition;
    }

    public ShipBoardSquareValue getShipBoardSquareValue(Position position) {
        return board[position.getX()][position.getY()].getValue();
    }

    public void markAttackedShipInShipBoard(Position position) {
        board[position.getX()][position.getY()].setHit();
    }

    public int getShipBoardScoreValueAtPoint(Position position) {
        return board[position.getX()][position.getY()].getScoreValue();
    }

    public boolean shipDrown(Position position) {
        return board[position.getX()][position.getY()].isDrown();
    }

    public int getNumOfShipSquares() {
        return shipSquaresCounter;
    }

    public int getBoardSize() {
        return rows;
    }

    public boolean isShipBoardSqaureGotHit(Position position) {
        return board[position.getX()][position.getY()].isHit();
    }

    public Ship getShip(Position position) throws NoShipAtPoisitionException {
        int x, y;

        x = position.getX();
        y = position.getY();
        if (board[x][y].isShip()) {
            return board[x][y].getAnchorShip();
        } else {
            throw new NoShipAtPoisitionException(x,y);
        }
    }

    public void removeMineFromBoard(Position minePosition) {
        board[minePosition.getX()][minePosition.getY()].setWater();
    }

    protected ShipBoard clone() throws CloneNotSupportedException {
        ShipBoard shipBoard = (ShipBoard) super.clone();
        shipBoard.board = new ShipsBoardSquare[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                shipBoard.board[i][j] = this.board[i][j].clone();
            }
        }
        return shipBoard;
    }
}
