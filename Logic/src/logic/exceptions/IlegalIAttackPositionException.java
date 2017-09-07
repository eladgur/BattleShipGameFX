package logic.exceptions;

public class IlegalIAttackPositionException extends Throwable {

    private static final String msg = "Illegal Attack position";
    private int x,y;

    public IlegalIAttackPositionException() {
        super(msg);
    }

    public IlegalIAttackPositionException(int x, int y) {
        super(msg);
        this.x = x;
        this.y = y;
    }
}
