package xmlInputManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "BattleShipGame")
public class GameInfo {
    @XmlElement(name = "GameType", required = true)
    private String gameType;
    private int boardSize;
    @XmlElement(required = true)
    private ShipTypes shipTypes;
    private Mine mine;

    public GameInfo() {
        mine = new Mine();
    }

    @XmlElement(required = true)
    private Boards boards;

    public String getGameType() {
        return gameType;
    }

    public List<Ship> getShipList(int PlayerIndex) {
        return boards.getBoard(PlayerIndex).getShipList();
    }

    public int getBoardSize() {
        return boardSize;
    }

    public ShipTypes getShipTypes() {
        return shipTypes;
    }

    public int getMineAmount() {
        return mine.getAmount();
    }

    public void setMineAmount(int mineAmount) {
        this.mine.setAmount(mineAmount);
    }
}
