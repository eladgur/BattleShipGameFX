package xmlInputManager;

import javax.xml.bind.annotation.*;
import java.util.List;
@XmlRootElement(name = "boards")
@XmlAccessorType(XmlAccessType.FIELD)
public class Boards {
    @XmlElement(name = "board",type = Board.class)
    protected List<Board> boards;

    public Board getBoard(int playerIndex) {
        return  boards.get(playerIndex);
    }
}
