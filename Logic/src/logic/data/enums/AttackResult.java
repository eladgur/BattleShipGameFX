package logic.data.enums;

import logic.data.Ship;
import xmlInputManager.Position;

public enum AttackResult {
    SHIPHIT,
    SHIPDROWNHIT,
    REPEATEDHIT,
    MISSHIT,
    MINESHIP,
    MINEDROWNSHIP,
    MINEWATER,
    MINEMINE,
    MINEREAPETEDHIT,
    INSERTMINE;

    private Ship drownShip;

    public void setShip(Ship ship){
        this.drownShip = ship;
    }

    public Ship getShip() {
        return this.drownShip;
    }
}
