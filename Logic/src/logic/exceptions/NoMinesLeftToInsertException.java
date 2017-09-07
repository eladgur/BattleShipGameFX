package logic.exceptions;

public class NoMinesLeftToInsertException extends Exception {
    public NoMinesLeftToInsertException() {
        super("0 mines left");
    }
}
