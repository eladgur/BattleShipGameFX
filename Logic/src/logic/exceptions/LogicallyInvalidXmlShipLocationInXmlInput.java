package logic.exceptions;

public class LogicallyInvalidXmlShipLocationInXmlInput extends LogicallyInvalidXmlInputException {
    int invalidX,invalidy;

    public LogicallyInvalidXmlShipLocationInXmlInput(int x, int y) {
        super(String.format("Invalid xml input file, illegal ship location: %d,%d", x,y));
        this.invalidX = x;
        this.invalidy = y;
    }

    public LogicallyInvalidXmlShipLocationInXmlInput(String message, int x, int y) {
        super(message);
        this.invalidX = x;
        this.invalidy = y;
    }
}
