package view.mainMenu;

import javafx.scene.control.Button;
import logic.data.PlayerData;

public interface PlayerDataToGridPaneFunc {
    void setItemBackgroundDueToPlayerData(PlayerData playerData, int i, int j, Button button);
}
