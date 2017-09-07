package xmlInputManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ShipType {
    @XmlElement(required = true)
    protected String category;
    protected int amount;
    protected int length;
    protected int score;
    @XmlAttribute(name = "id", required = true)
    protected String id;

    public ShipType() {
        score = 1;
    }

    public String getCategory() {
        return category;
    }

    public int getAmount() {
        return amount;
    }

    public int getLength() {
        return length;
    }

    public int getScore() {
        return score;
    }

    public String getId() {
        return id;
    }

    public String getCatagory(){
       return category;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
