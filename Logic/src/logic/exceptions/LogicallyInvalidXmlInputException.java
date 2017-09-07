package logic.exceptions;

public class LogicallyInvalidXmlInputException extends Exception {

    public LogicallyInvalidXmlInputException() {}

    public LogicallyInvalidXmlInputException(String message) {
        super("Invalid Xml input: " + message);
    }
}
