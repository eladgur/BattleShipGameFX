package logic;

import logic.data.Ship;
import xmlInputManager.Position;

public interface ShipDrownListener {
    void shipDrownEventHandler(Ship drownShip, int shipOwnerIndex);
}
