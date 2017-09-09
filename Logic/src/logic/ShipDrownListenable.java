package logic;

import logic.data.Ship;

public interface ShipDrownListenable {
    void addShipDrownListener(ShipDrownListener listenerToAdd);
    void removeShipDrownListener(ShipDrownListener listenerToRemove);
    void notifyAllShipDrownListeners(Ship drownShip,int shipOwnerIndex);
}
