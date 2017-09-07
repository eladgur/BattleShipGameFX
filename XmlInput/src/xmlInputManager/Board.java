package xmlInputManager;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Board {
    @XmlElement(name = "ship",type = Ship.class)
    protected List<Ship> ships;

    public List<Ship> getShipList() {
        return ships;
    }
}
