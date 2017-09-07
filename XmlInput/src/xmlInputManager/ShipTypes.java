package xmlInputManager;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ShipTypes {
    @XmlElement(required = true, name = "shipType", type = ShipType.class)
    private List<ShipType> shipTypes;

    public List<ShipType> getShipTypesList() {
        return shipTypes;
    }

    public ShipType getShipTypeById(String shipTypeId) {
        ShipType resShipType = null;
        for (ShipType shipType : shipTypes) {
            if (shipType.id.equals(shipTypeId)) {
                resShipType = shipType;
                break;
            }
        }

        return resShipType;
    }
}