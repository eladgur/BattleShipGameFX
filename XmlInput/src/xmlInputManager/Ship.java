package xmlInputManager;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Ship {
    @XmlElement(required = true)
    protected String shipTypeId;
    @XmlElement(required = true)
    protected Position position;
    @XmlElement(required = true)
    protected String direction;

    public Position getPosition() {
        return position;
    }

    public String getDirection() {
        return direction;
    }

    public String getShipTypeId() {
        return shipTypeId;
    }

}





