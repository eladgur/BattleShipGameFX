package logic.data;

import logic.data.enums.TrackBoardSquareValue;
import xmlInputManager.Position;

public class TrackBoard implements Cloneable {
    private TrackBoardSquareValue[][] trackBoard;
    private final int rows, columns;

    public TrackBoard(int boardSize) {
        this.rows = this.columns = boardSize;
        this.trackBoard = new TrackBoardSquareValue[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                trackBoard[i][j] = TrackBoardSquareValue.EMPTY;
            }
        }
    }

    public void setMiss(Position positionToAttack) {
        int x = positionToAttack.getX();
        int y = positionToAttack.getY();

        trackBoard[x][y] = TrackBoardSquareValue.MISS;
    }

    public TrackBoardSquareValue getSquareValue(Position position) {
        int x = position.getX();
        int y = position.getY();

        return trackBoard[x][y];
    }

    public void markPosition(Position position, TrackBoardSquareValue valueToMark) {
        trackBoard[position.getX()][position.getY()] = valueToMark;
    }

    @Override
    protected TrackBoard clone() throws CloneNotSupportedException {
        TrackBoard trackBoard = (TrackBoard) super.clone();
        trackBoard.trackBoard = new TrackBoardSquareValue[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                trackBoard.trackBoard[i][j] = this.trackBoard[i][j];
            }
        }
        return trackBoard;
    }
}
